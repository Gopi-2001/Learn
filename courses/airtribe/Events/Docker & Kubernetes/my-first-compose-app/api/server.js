const http = require('http');
const { Pool } = require('pg');
const redis = require('redis');

// PostgreSQL connection — credentials come from env vars injected by Docker Compose
const db = new Pool({
  host: process.env.DB_HOST,
  port: process.env.DB_PORT,
  database: process.env.DB_NAME,
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
});

// Redis client
const cache = redis.createClient({
  socket: { host: process.env.REDIS_HOST, port: process.env.REDIS_PORT },
});

cache.on('error', (err) => console.error('Redis error:', err));

async function init() {
  await cache.connect();

  await db.query(`
    CREATE TABLE IF NOT EXISTS visits (
      id   SERIAL PRIMARY KEY,
      path TEXT NOT NULL,
      at   TIMESTAMPTZ DEFAULT NOW()
    )
  `);

  const server = http.createServer(async (req, res) => {
    const url = req.url;

    // Health check endpoint — used by HEALTHCHECK and load balancer
    if (url === '/health') {
      res.writeHead(200);
      return res.end('OK');
    }

    // Check Redis cache first
    const cached = await cache.get(url);
    if (cached) {
      res.writeHead(200, { 'Content-Type': 'application/json' });
      return res.end(JSON.stringify({ source: 'cache', data: JSON.parse(cached) }));
    }

    // Record visit in Postgres
    await db.query('INSERT INTO visits (path) VALUES ($1)', [url]);

    // Read all visits for this path
    const result = await db.query(
      'SELECT id, path, at FROM visits WHERE path = $1 ORDER BY at DESC LIMIT 10',
      [url]
    );

    const data = result.rows;

    // Store in Redis with 10-second TTL
    await cache.setEx(url, 100, JSON.stringify(data));

    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ source: 'db', data }));
  });

  server.listen(process.env.PORT || 3000, () => {
    console.log(`API running on port ${process.env.PORT || 3000}`);
  });
}

init().catch((err) => {
  console.error('Startup failed:', err);
  process.exit(1);
});
