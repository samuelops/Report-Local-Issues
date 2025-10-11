# Report Local Issues

Public, lightweight civic-tech platform to report local civic issues (potholes, broken lights, garbage), track them with a tracking ID, and manage resolution.

**Live:** https://report-issues-latest.onrender.com/  
**Source:** https://github.com/samuelops/Report-Local-Issues

---

## Features
- Submit complaints with optional image & location
- Track issues by tracking ID
- Admin panel for viewing / updating complaints
- Image uploads and CSV export
- Spring Boot backend + MySQL/Postgres support + Leaflet map frontend

---

## Quick start (local)
Requirements:
- JDK 17+ (or 21+ depending on your pom)
- Maven
- Docker (optional, for container run)
- PostgreSQL or MySQL

```bash
# 1️⃣ Clone
git clone https://github.com/samuelops/Report-Local-Issues.git
cd Report-Local-Issues

# 2️⃣ Copy and edit environment file
cp application.properties.example application.properties
# set your DB credentials and mail config

# 3️⃣ Build & Run
./mvnw clean package
java -jar target/report-issues-0.0.1-SNAPSHOT.jar
