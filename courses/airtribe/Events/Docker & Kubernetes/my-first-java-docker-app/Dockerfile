# ═══════════════════════════════════════════════════════════════════════
#  MULTI-STAGE DOCKERFILE — Java / Spring Boot
#
#  Why multi-stage?
#  ┌─────────────────────────────────────────────────────────────────┐
#  │  JS (your old app)        │  Java (this app)                    │
#  │──────────────────────────────────────────────────────────────── │
#  │  No compilation needed    │  Must compile .java → .class → JAR │
#  │  node_modules shipped     │  Only the final JAR is shipped      │
#  │  Source code visible      │  Source code stays in build stage   │
#  └─────────────────────────────────────────────────────────────────┘
#
#  Stage 1 (builder) — JDK + Maven: compile source → fat JAR
#  Stage 2 (runner)  — JRE only:    run the JAR  (smaller & safer)
# ═══════════════════════════════════════════════════════════════════════


# ── STAGE 1: BUILD ──────────────────────────────────────────────────────
#  maven:3.9-eclipse-temurin-21-alpine includes both Maven AND the JDK.
#  "eclipse-temurin" is the recommended OpenJDK distribution.
#  "alpine" keeps the base image small.
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

LABEL maintainer="gk@example.com"

WORKDIR /build

# ── LAYER CACHING TRICK (same as copying package.json before npm install) ──
# Copy pom.xml first. Docker caches this layer.
# Maven dependencies are re-downloaded ONLY when pom.xml changes.
# If only src/ changes, Docker reuses the cached dependency layer → faster builds.
COPY pom.xml .
RUN mvn dependency:go-offline -B
#   dependency:go-offline : download all deps into the local Maven cache
#   -B                    : batch mode (no interactive prompts, cleaner logs)

# Now copy source code and compile
COPY src ./src
RUN mvn package -DskipTests -B
#   package       : compile + run tests + create JAR
#   -DskipTests   : skip tests here (run them separately in CI pipeline)
#   Result: /build/target/hello-docker-1.0.0.jar


# ── STAGE 2: RUN ────────────────────────────────────────────────────────
#  eclipse-temurin:21-jre-alpine = JRE only (no compiler, no Maven).
#  This image is ~80 MB vs ~500 MB for the full JDK + Maven image.
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="gk@example.com"
LABEL version="1.0.0"

WORKDIR /app

# Security: never run as root inside a container
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy ONLY the compiled JAR from Stage 1 — source code never enters this image
COPY --from=builder /build/target/*.jar app.jar
RUN chown appuser:appgroup app.jar

USER appuser

# PORT env var — Spring Boot reads it via: server.port=${PORT:8080}
ENV PORT=8080

EXPOSE 8080

# HEALTHCHECK — Spring Boot Actuator exposes /actuator/health automatically
# wget is available in Alpine Linux (curl is not installed by default)
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

# Run the fat JAR directly
ENTRYPOINT ["java", "-jar", "app.jar"]
