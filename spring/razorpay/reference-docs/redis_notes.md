# Redis — Complete Notes (Installation → CLI → Java/Spring Boot)

## What is Redis?

Redis (**RE**mote **DI**ctionary **S**erver) is an in-memory, key-value data store. It's used as a cache, session store, message broker, and — as in this project — a backing store for distributed rate limiters (via Sorted Sets, Hashes, and Lua scripts). Because it lives in memory, reads/writes are extremely fast (sub-millisecond), but data is only as durable as its persistence configuration (see [Persistence](#persistence-rdbaof) below).

---

## Step 1: Install & Run Redis on Windows

Redis isn't officially supported natively on Windows, so use Docker or WSL.

### Option A: Docker (recommended)

Fastest option if you already have Docker Desktop installed.

```bash
docker run --name local-redis -p 6379:6379 -d redis
```

- `--name local-redis` — friendly container name so you can `docker start/stop local-redis` later.
- `-p 6379:6379` — maps Redis's default port to your host.
- `-d` — runs in detached mode (background).

Useful follow-up commands:
```bash
docker ps                          # confirm it's running
docker stop local-redis            # stop it
docker start local-redis           # start it again (data persists in the container)
docker exec -it local-redis redis-cli   # open a redis-cli shell inside the running container
docker logs local-redis             # view Redis server logs
```

To persist data across container recreation, mount a volume:
```bash
docker run --name local-redis -p 6379:6379 -v redis-data:/data -d redis redis-server --save 60 1 --appendonly yes
```

### Option B: WSL (Windows Subsystem for Linux)

If you'd rather run a real Linux Redis binary directly:

```powershell
# In an elevated (Administrator) PowerShell
wsl --install
```

Restart if prompted, then in your new Ubuntu terminal:
```bash
sudo apt update
sudo apt install redis-server
sudo service redis-server start
```

To have Redis start automatically whenever you open WSL, add `sudo service redis-server start` to your `~/.bashrc`, or configure it as a proper systemd service if your WSL version supports systemd (`wsl.conf` → `[boot]` `systemd=true`).

### Option C: macOS / Linux (native)

```bash
# macOS
brew install redis
brew services start redis

# Ubuntu/Debian
sudo apt install redis-server
sudo systemctl enable --now redis-server
```

### Verifying the installation

```bash
redis-cli ping
```
Expected response: `PONG`

Check the version and basic info:
```bash
redis-cli --version
redis-cli info server
```

---

## Step 2: Using `redis-cli` (the command-line client)

`redis-cli` is Redis's interactive shell — essential for debugging your rate limiter keys, inspecting cache state, and running ad-hoc commands.

### Connecting

```bash
redis-cli                          # connects to localhost:6379 by default
redis-cli -h <host> -p <port>       # connect to a remote/custom instance
redis-cli -h <host> -p <port> -a <password>   # with auth (avoid -a in shared shell history; prefer REDISCLI_AUTH env var)
```

### General commands

| Command | Purpose |
|---|---|
| `PING` | Health check — should return `PONG` |
| `KEYS pattern` | List keys matching a pattern (⚠️ blocks the server on large datasets — avoid in production; use `SCAN` instead) |
| `SCAN cursor MATCH pattern COUNT n` | Non-blocking, cursor-based iteration over keys — the production-safe alternative to `KEYS` |
| `TYPE key` | Shows the data type stored at a key (`string`, `hash`, `zset`, `list`, `set`) |
| `TTL key` | Seconds until the key expires (`-1` = no expiry set, `-2` = key doesn't exist) |
| `EXPIRE key seconds` | Set/refresh a TTL on a key |
| `PERSIST key` | Remove a key's TTL (make it permanent) |
| `DEL key` | Delete a key |
| `EXISTS key` | Check if a key exists (`1`/`0`) |
| `FLUSHDB` | Wipe all keys in the current DB (⚠️ destructive, dev/test only) |
| `FLUSHALL` | Wipe all keys in **all** DBs (⚠️ destructive) |
| `DBSIZE` | Number of keys in the current DB |
| `SELECT n` | Switch to logical DB `n` (Redis supports 16 DBs, 0–15, by default) |
| `MONITOR` | Live stream of every command hitting the server (great for debugging, never in production under load) |

### String commands

```bash
SET name "value"
GET name
SET name "value" EX 60      # set with a 60-second TTL
INCR counter                 # atomic increment (creates key at 0 first if absent)
```

### Hash commands (used by the Token Bucket limiter in this project)

A Hash is a single key holding multiple field→value pairs — like a small object.

```bash
HSET ratelimit:tokenBucket:user123 tokens 10 ts 1721990400000
HMSET ratelimit:tokenBucket:user123 tokens 10 ts 1721990400000   # same, deprecated alias
HGET ratelimit:tokenBucket:user123 tokens
HMGET ratelimit:tokenBucket:user123 tokens ts     # fetch multiple fields at once
HGETALL ratelimit:tokenBucket:user123             # fetch all fields+values
HDEL ratelimit:tokenBucket:user123 tokens
```

> `HMSET` is deprecated since Redis 4.0 in favor of `HSET` (which now accepts multiple field-value pairs too), but it's kept for backward compatibility.

### Sorted Set / ZSET commands (used by the Sliding Window Log limiter)

A ZSET stores members with an associated numeric **score**, kept sorted by that score — perfect for storing request timestamps.

```bash
ZADD ratelimit:sliding:user123 1721990400000 "req-uuid-1"
ZRANGE ratelimit:sliding:user123 0 -1 WITHSCORES     # view all entries, oldest first
ZCARD ratelimit:sliding:user123                       # count of members (= request count in window)
ZREMRANGEBYSCORE ratelimit:sliding:user123 -inf 1721990340000   # prune entries older than a cutoff
ZRANGEBYSCORE ratelimit:sliding:user123 1721990340000 +inf      # entries within a time range
```

### List, Set, and other types (for completeness)

```bash
LPUSH mylist "a"          # push to front of a list
RPUSH mylist "b"          # push to back of a list
LRANGE mylist 0 -1        # view whole list

SADD myset "a" "b"        # add to a set (unique values, no order)
SMEMBERS myset

# Basic pub/sub (used for messaging, not caching)
SUBSCRIBE channel
PUBLISH channel "message"
```

### Inspecting rate-limiter keys during development

```bash
redis-cli keys "ratelimit:*"                    # dev only — see redis-cli KEYS warning above
redis-cli type ratelimit:sliding:user123
redis-cli zrange ratelimit:sliding:user123 0 -1 withscores
redis-cli ttl ratelimit:sliding:user123
```

### Running a Lua script from the CLI (handy for testing scripts before wiring into Java)

```bash
redis-cli --eval script.lua KEYS_LIST , ARGV_LIST
# example:
redis-cli --eval token_bucket.lua ratelimit:tokenBucket:test , 10 2 1721990400000 60
```
Note the literal comma `,` — it separates `KEYS[]` from `ARGV[]` in the `--eval` syntax.

---

## Step 3: Configure Your Spring Boot Project

### 1. Add the dependency

**Maven (`pom.xml`)**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

**Gradle (`build.gradle`)**
```groovy
implementation 'org.springframework.boot:spring-boot-starter-data-redis'
```

This pulls in **Lettuce** as the default Redis client (thread-safe, non-blocking, backed by Netty). If you specifically want **Jedis** instead (simpler, blocking, one connection per thread), exclude Lettuce and add the Jedis starter:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
    <exclusions>
        <exclusion>
            <groupId>io.lettuce</groupId>
            <artifactId>lettuce-core</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
</dependency>
```
Lettuce is the recommended default for most Spring Boot apps (better concurrency model), so only switch if you have a specific reason to.

### 2. Connection properties

`src/main/resources/application.properties`:
```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.password=
spring.data.redis.database=0

# Connection pool (Lettuce) — tune under real load
spring.data.redis.lettuce.pool.max-active=8
spring.data.redis.lettuce.pool.max-idle=8
spring.data.redis.lettuce.pool.min-idle=0
spring.data.redis.lettuce.pool.max-wait=100ms

# Timeouts
spring.data.redis.timeout=2000ms
spring.data.redis.connect-timeout=2000ms
```

Or `application.yml`:
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      timeout: 2000ms
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
```

### 3. `RedisTemplate` vs `StringRedisTemplate`

Spring Data Redis gives you two main templates:

| Template | Key/Value type | When to use |
|---|---|---|
| `RedisTemplate<String, Object>` | Arbitrary Java objects (serialized) | Caching complex objects, need custom serializers |
| `StringRedisTemplate` | Always `String` keys and values | Simpler, human-readable in `redis-cli`, ideal for counters, rate limiting, simple flags — **this is what the rate limiter classes in this project use** |

`StringRedisTemplate` is a pre-configured `RedisTemplate<String, String>` using `StringRedisSerializer` for both keys and values — no serialization config needed, and what you see in `redis-cli` matches exactly what your Java code stored.

```java
@Autowired
private StringRedisTemplate stringRedisTemplate;

// String ops
stringRedisTemplate.opsForValue().set("key", "value");
String val = stringRedisTemplate.opsForValue().get("key");

// Hash ops (used by TokenBucketRateLimiter)
stringRedisTemplate.opsForHash().put("hashKey", "field", "value");
Object field = stringRedisTemplate.opsForHash().get("hashKey", "field");

// Sorted Set ops (used by SlidingWindowRateLimiter)
stringRedisTemplate.opsForZSet().add("zsetKey", "member", 123.0);
Long count = stringRedisTemplate.opsForZSet().zCard("zsetKey");
```

### 4. Using `RedisTemplate<String, Object>` for arbitrary objects

If you need to cache actual Java objects (not just strings), use `RedisTemplate` with a JSON serializer:

```java
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
```

```java
@Service
public class CacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void saveData(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public Object getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
```

Without a serializer configured explicitly, Spring Boot defaults `RedisTemplate<String,Object>` to Java's built-in serialization (`JdkSerializationRedisSerializer`), which produces binary, non-human-readable values in `redis-cli` and requires objects to implement `Serializable`. Configuring `GenericJackson2JsonRedisSerializer` as shown above stores readable JSON instead — generally the better default.

### 5. Declarative caching with `@Cacheable`

For simple "cache the result of this method" use cases, Spring's caching abstraction is less boilerplate than manual `RedisTemplate` calls:

```java
@SpringBootApplication
@EnableCaching
public class Application { ... }
```

```java
@Service
public class ProductService {

    @Cacheable(value = "items", key = "#id")
    public Product getProduct(String id) {
        // Only runs on a cache miss; result is cached under "items::id" afterward
        return productRepository.findById(id);
    }

    @CacheEvict(value = "items", key = "#id")
    public void updateProduct(String id, Product product) {
        productRepository.save(product);
    }

    @CachePut(value = "items", key = "#product.id")
    public Product refreshProduct(Product product) {
        return productRepository.save(product);
    }
}
```

Configure a default TTL for `@Cacheable` entries:
```java
@Bean
public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
    RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                    .fromSerializer(new GenericJackson2JsonRedisSerializer()));
    return RedisCacheManager.builder(factory).cacheDefaults(config).build();
}
```

### 6. Running Lua scripts from Java (as used in this project's rate limiters)

```java
private static final RedisScript<List<Long>> SCRIPT =
        RedisScript.of(luaScriptString, (Class<List<Long>>) (Class<?>) List.class);

List<Long> result = stringRedisTemplate.execute(
        SCRIPT,
        List.of("someKey"),        // KEYS[1], KEYS[2], ...
        "arg1", "arg2"             // ARGV[1], ARGV[2], ... (all passed as Strings)
);
```

Why use Lua scripts at all: Redis executes a script as a single atomic operation — no other client's commands can interleave mid-script. This is essential for check-and-set style logic (like "read token count, decide, then write new count") where doing the same steps as separate Java calls would create a race condition under concurrent load.

### 7. Testing with an embedded/ephemeral Redis

For unit/integration tests, avoid depending on a real running Redis instance:

- **Testcontainers** (recommended, closest to production behavior):
```java
@Container
static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
        .withExposedPorts(6379);

@DynamicPropertySource
static void redisProps(DynamicPropertyRegistry registry) {
    registry.add("spring.data.redis.host", redis::getHost);
    registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
}
```
- Alternatively, embedded-Redis libraries exist (e.g., `it.ozimov:embedded-redis`), but they're less actively maintained — Testcontainers is generally the more reliable choice today.

---

## Persistence (RDB/AOF)

By default Redis is in-memory — a restart with no persistence configured loses all data. Two persistence strategies (can be combined):

- **RDB (snapshotting)**: periodic point-in-time dumps to disk (`save 60 1000` = snapshot every 60s if ≥1000 keys changed). Fast, compact, but can lose the last few seconds/minutes of writes on a crash.
- **AOF (Append Only File)**: logs every write operation; replayed on restart. More durable, larger file size, slightly higher overhead per write.

For a rate limiter specifically, persistence usually matters less (losing counters on restart just means clients briefly get a "fresh" limit), but for caching real business data or session state, enable at least one.

## Eviction policies

When Redis hits its configured `maxmemory`, it needs a policy for what to evict:

| Policy | Behavior |
|---|---|
| `noeviction` | Reject new writes once memory is full (default) |
| `allkeys-lru` | Evict least-recently-used keys across the whole keyspace |
| `volatile-lru` | Evict least-recently-used keys, but only among keys with a TTL set |
| `allkeys-random` / `volatile-random` | Evict randomly |
| `volatile-ttl` | Evict keys with the shortest remaining TTL first |

For a cache, `allkeys-lru` is a common choice. For rate-limiter/session keys where you always set a TTL anyway, `volatile-lru` or `volatile-ttl` work well.

## GUI tools (optional, for visual inspection)

- **RedisInsight** (free, official from Redis) — browse keys, run commands, visualize data structures, monitor memory usage.
- **Another Redis Desktop Manager** — lightweight cross-platform alternative.

## Production considerations checklist

- [ ] Set a password (`requirepass` in `redis.conf`, or `spring.data.redis.password`).
- [ ] Don't expose port 6379 publicly — bind to internal network / use a VPC / security group rules.
- [ ] Configure `maxmemory` and an eviction policy appropriate to your use case.
- [ ] Enable persistence (RDB and/or AOF) if losing data on restart is unacceptable.
- [ ] Use `SCAN` instead of `KEYS` anywhere in application code or scripts that touch production.
- [ ] Consider Redis Sentinel (automatic failover) or Redis Cluster (sharding + HA) for anything beyond a single dev instance.
- [ ] Monitor memory usage, hit rate, and slow log (`SLOWLOG GET`) — a rate limiter or cache silently falling back to "fail open" (as in this project's limiters) can mask a Redis outage if you're not also alerting on connection errors.

## References

- [Redis official documentation](https://redis.io/docs/latest/)
- [Spring Data Redis reference docs](https://docs.spring.io/spring-data/redis/reference/)
- [Redis command reference](https://redis.io/commands/)