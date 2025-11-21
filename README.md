# Internship Placement Management System (SC2002 Project)

### Overview
Console-based system to manage internship postings, applications, approvals, and placements for **Students**, **Company Representatives**, and **Career Center Staff**. Designed with a clean layered architecture and SOLID principles. Data persists across restarts via serialized repositories, with CSV seeding on first run.

### Contributions
1. Aaron Wee Zhi Rong
2. Pan Yifan
3. Loh Gan Sui
4. Daniel Chua Han
5. Yeo Swe Hon 

### Considerations
1. How to enforce **eligibility & visibility** (Y1–Y2 BASIC only; date window; approved & public only)?  
2. How to make filters **role-specific** yet consistent (student vs staff/rep)?  
3. How to persist data reliably while keeping storage **swappable** (files now, DB later)?  
4. How to guarantee **single accepted placement** per student and consistent application states?

### System Summary
1. **Roles & Flows**
   - **Student** — view (filtered), apply, view status, accept one offer, request withdrawal.  
   - **Company Rep** — register (requires staff approval), create/edit **DRAFT**, submit for approval, toggle visibility, review applicants.  
   - **Staff** — approve/reject **rep registrations** & **postings**, handle withdrawal requests, generate **comprehensive report** (respects filters).
2. **Eligibility & Visibility Rules**
   - Only **APPROVED** + **PUBLIC** postings within **open/close dates** are listed.  
   - **Y1–Y2** see **BASIC** only; **Y3–Y4** may also see **INTERMEDIATE/ADVANCED**.  
   - Capacity, major, and date-window checks enforced at listing/apply time.
3. **Filtering (then sorting by Title)**
   - **Student** — Company name, Level (policy-aware), Closing date (≤).  
   - **Staff/Rep** — Company name, Level, Closing date (≤), Major, Status (DRAFT/PENDING/APPROVED/REJECTED).  
   - Filters are **per-user, in-memory** for the session; not persisted to disk.

### Architecture (at a glance)
1. **Boundary (UI)** — console pages collect input & render output; they **delegate** to services.  
2. **Services** — business rules & workflows (Auth, Posting, Application, Approval, Reporting).  
3. **Repositories** — domain-oriented interfaces; serialized adapters implement them.  
4. **Entities** — state + small invariants (e.g., `hasCapacity()`), no policy logic.  
5. **Wiring** — `AppContext` + `Startup` configure and expose a single registry of services.

### Build & Run

**Windows PowerShell**
```powershell
Remove-Item -Recurse -Force .\out -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Path .\out | Out-Null
$sources = Get-ChildItem -Recurse -Filter *.java -Path .\src -File | % FullName
javac -encoding UTF-8 -d .\out $sources
java -cp .\out app.Main
