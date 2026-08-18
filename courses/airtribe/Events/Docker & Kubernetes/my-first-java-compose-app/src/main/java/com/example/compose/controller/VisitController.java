package com.example.compose.controller;

import com.example.compose.model.Visit;
import com.example.compose.repository.VisitRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.List;
import java.util.Map;

// This mirrors the JS server.js logic:
//   1. Check Redis cache first
//   2. On cache miss: save visit to PostgreSQL, count, then cache
@RestController
public class VisitController {

    private final VisitRepository visitRepo;
    private final StringRedisTemplate redis;   // replaces: redis.createClient()

    // Constructor injection — Spring wires these automatically
    public VisitController(VisitRepository visitRepo, StringRedisTemplate redis) {
        this.visitRepo = visitRepo;
        this.redis     = redis;
    }

    // GET /  →  records the visit, returns count
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home(HttpServletRequest request) {
        return trackVisit(request.getRequestURI());
    }

    // GET /visits  →  all recorded visits (for debugging)
    @GetMapping("/visits")
    public List<Visit> allVisits() {
        return visitRepo.findAll();
    }

    // GET /health  →  used by Dockerfile HEALTHCHECK and Nginx
    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    private ResponseEntity<Map<String, Object>> trackVisit(String path) {
        String cacheKey = "visits:" + path;

        // ── Check Redis cache ─────────────────────────────────────────
        // Equivalent to: const cached = await cache.get(url)
        String cached = redis.opsForValue().get(cacheKey);
        if (cached != null) {
            return ResponseEntity.ok(Map.of(
                "source", "cache",
                "path",   path,
                "visits", Long.parseLong(cached)
            ));
        }

        // ── Cache miss: write to PostgreSQL ───────────────────────────
        // Equivalent to: await db.query('INSERT INTO visits (path) VALUES ($1)', [url])
        visitRepo.save(new Visit(path));
        long count = visitRepo.countByPath(path);

        // ── Store result in Redis for 30 seconds ──────────────────────
        // Equivalent to: await cache.set(url, JSON.stringify(data), { EX: 30 })
        redis.opsForValue().set(cacheKey, String.valueOf(count), Duration.ofSeconds(30));

        return ResponseEntity.ok(Map.of(
            "source", "db",
            "path",   path,
            "visits", count
        ));
    }
}
