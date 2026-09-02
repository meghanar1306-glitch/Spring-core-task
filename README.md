# Employee Leave Management Portal

A small Java web application built with **Servlet, JSP, Spring Core (IoC/DI), Spring MVC,
and JPA/Hibernate with MySQL**, deployed on **Apache Tomcat**.

> **Jakarta EE build.** This version targets **Tomcat 10.1.x**, which uses the `jakarta.*`
> package namespace (not the older `javax.*` Java EE APIs). That's why it needs **Java 17**,
> **Spring 6**, and **Hibernate 6**. If you need to run on Tomcat 9 instead, all `jakarta.*`
> imports would need to be changed back to `javax.*` and the dependency versions downgraded.

Employees can log in, view a dashboard, apply for leave, and see their leave request history.

---

## 1. Technology Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Web layer | Servlet API, Spring MVC |
| View | JSP + EL + JSTL (no scriptlets) |
| IoC / DI | Spring Core (constructor injection) |
| Persistence | JPA with Hibernate as the provider |
| Database | MySQL |
| Server | Apache Tomcat 10.1.x (Jakarta EE) |
| Logging | Plain `System.out.println` (console output — no external logging library) |
| Build | Maven |

---

## 2. Project / Package Structure

```
com.leaveportal
 ├── config         RootConfig, WebConfig, AppInitializer (Java-based Spring configuration - no XML)
 ├── entity        Employee, LeaveRequest, LeaveType, LeaveStatus (JPA entities/enums)
 ├── repository     EmployeeRepository, LeaveRequestRepository (plain classes, JPA/Hibernate code)
 ├── service        EmployeeService, LeaveService (+ impls: validation, business rules, @Transactional)
 │    └── exception  InvalidLoginException, InvalidLeaveRequestException, InsufficientLeaveBalanceException
 ├── controller     LoginController, DashboardController, LeaveController, HomeController (Spring MVC)
 │    └── advice     GlobalExceptionHandler (catches unexpected errors + 404s, shows error.jsp)
 └── filter         AuthenticationFilter (login check), RequestTracingFilter (request logging)
```

**Layered request flow:**
`Browser -> Tomcat/DispatcherServlet (Front Controller) -> Controller -> Service -> Repository -> JPA/Hibernate -> MySQL -> JSP View`

Controllers only handle web concerns (session, cookies, model, view name). All validation and
business rules live in the Service layer. All persistence code lives in the Repository layer
(each repository is a single class — no separate interface, to keep things simple).

---

## 3. Prerequisites

- JDK 17
- Maven 3.6+
- MySQL 8.x running locally (or reachable)
- Apache Tomcat 10.1.x (Jakarta EE - jakarta.* namespace)

---

## 4. Database Setup & Analysis

1. Make sure MySQL is running.
2. Run the provided script to create the database, tables, and demo data:
   ```
   mysql -u root -p < sql/schema.sql
   ```
   This creates the `leave_portal_db` database with `employee` and `leave_request` tables,
   and inserts demo employees and sample leave requests.

   > Note: the app is also configured with `hibernate.hbm2ddl.auto=update`, so tables will be
   > created automatically on first run even if you skip this step — but you will still need to
   > insert at least one employee row to be able to log in, so running the script is recommended.

3. Update the 4 `DB_*` constants at the top of `RootConfig.java`
   (`src/main/java/com/leaveportal/config/RootConfig.java`) if your MySQL username/password/port
   differ from the defaults (`root` / `root` / `3306`).

### 4.1 Tables

**`employee`** — one row per employee who can log in.

| Column | Type | Constraints | Notes |
|---|---|---|---|
| `employee_id` | `VARCHAR(20)` | `PRIMARY KEY`, `NOT NULL` | Also the login ID (e.g. `EMP001`); no separate auto-generated ID is used. |
| `password` | `VARCHAR(100)` | `NOT NULL` | Stored as plain text in this version (fine for a learning project, not for production). |
| `name` | `VARCHAR(100)` | `NOT NULL` | Display name. |
| `department` | `VARCHAR(100)` | `NOT NULL` | Department shown on the dashboard. |
| `leave_balance` | `INT` | `NOT NULL DEFAULT 0` | Days of leave still available; reduced automatically each time a leave request is approved by the app logic. |

**`leave_request`** — one row per leave application submitted by an employee.

| Column | Type | Constraints | Notes |
|---|---|---|---|
| `request_id` | `BIGINT` | `PRIMARY KEY`, `AUTO_INCREMENT` | Unique ID per request. |
| `employee_id` | `VARCHAR(20)` | `NOT NULL`, `FOREIGN KEY -> employee(employee_id)` | Which employee this request belongs to. |
| `leave_type` | `VARCHAR(20)` | `NOT NULL` | One of `CASUAL`, `SICK`, `EARNED` (matches the `LeaveType` enum). |
| `from_date` | `DATE` | `NOT NULL` | First day of leave. |
| `to_date` | `DATE` | `NOT NULL` | Last day of leave. |
| `number_of_days` | `INT` | `NOT NULL` | Calculated by the service layer as `(to_date - from_date) + 1`, not entered by the user. |
| `reason` | `VARCHAR(500)` | `NOT NULL` | Free-text reason for the request. |
| `status` | `VARCHAR(20)` | `NOT NULL` | One of `PENDING`, `APPROVED`, `REJECTED` (matches the `LeaveStatus` enum). Every new request starts as `PENDING`. |
| `created_date` | `DATETIME` | `NOT NULL` | When the request was submitted. |

### 4.2 Relationship

- **`employee` (1) → `leave_request` (many)**: one employee can have many leave requests, but
  each leave request belongs to exactly one employee. This is enforced by the
  `fk_leave_request_employee` foreign key on `leave_request.employee_id`, and mapped in code as
  a `@ManyToOne` on `LeaveRequest.employee` (see `LeaveRequest.java`).
- There is no cascading delete — an `employee` row can't be deleted while it still has
  `leave_request` rows pointing to it, unless those rows are removed first.

### 4.3 Sample data included in `schema.sql`

- 5 demo employees across different departments, all with password `password123`.
- 2 sample `leave_request` rows for `EMP001` (one `APPROVED`, one `REJECTED`), so the leave
  history page has something to show right after setup.
- Note: the script inserts `EMP003` twice (once as "Divya", once as "Nandini"). Because
  `employee_id` is the primary key and the insert uses `ON DUPLICATE KEY UPDATE employee_id =
  employee_id`, the second `EMP003` row is silently ignored — only "Divya" actually ends up in
  the table for that ID. This is existing sample data, left untouched; call it out if you want
  it corrected.

### Test credentials for evaluation

| Employee ID | Password | Name | Leave Balance |
|---|---|---|---|
| `EMP001` | `password123` | Anusha | 18 days |
| `EMP002` | `password123` | Dhanush | 20 days |
| `EMP003` | `password123` | Divya | 10 days |
| `EMP004` | `password123` | Priya | 12 days |

---

## 5. Build

```
mvn clean package
```

This produces `target/employee-leave-management-portal.war`.

---

## 6. Deploy on Tomcat

1. Copy `target/employee-leave-management-portal.war` into Tomcat's `webapps/` directory.
2. Start Tomcat.
3. Open: `http://localhost:8080/employee-leave-management-portal/`
   (it will redirect to the login page).

Alternatively, in an IDE, run the project as a Maven web application on a configured
Tomcat 10.1.x server.

---

## 7. Application Flow

1. **Login** (`/login`) — enter Employee ID and password. Invalid credentials show a generic,
   safe error message. On success, the employee ID is stored in the HTTP session
   (never the password).
2. **Dashboard** (`/dashboard`) — shows employee ID, name, department, leave balance, and a
   pending/approved request summary. Also lets the employee save a dashboard view preference
   (`detailed` / `compact`) as a non-sensitive cookie.
3. **Apply Leave** (`/leave/apply`) — form for Leave Type, From Date, To Date, Reason.
   Validates mandatory fields, date range, leave type, and available balance. On success, the
   leave request is saved with status `PENDING` and the employee's leave balance is reduced in
   the same transaction (see below).
4. **My Leave Requests** (`/leave/history`) — shows only the logged-in employee's own requests.
5. **Logout** (`/logout`) — invalidates the session.

`/dashboard`, `/leave/*` and `/preferences/*` are protected by `AuthenticationFilter`, which
redirects anonymous requests back to `/login`.

---

## 8. Key Design Notes

- **Session vs Cookie:** the session holds only the logged-in employee's ID (used to identify
  the user across Login → Dashboard → Apply Leave → History). The cookie holds only a
  non-sensitive UI preference (dashboard view). No passwords or business data are ever placed
  in a cookie.
- **Leave balance consistency:** `LeaveServiceImpl.applyLeave()` is annotated `@Transactional`.
  It both saves the new `LeaveRequest` and deducts the used days from the employee's leave
  balance. If anything fails partway through, Spring rolls back both changes together, so the
  database is never left with a leave request but no corresponding balance update (or vice
  versa).
- **Exception handling:** expected failures (bad login, invalid leave input, insufficient
  balance) are custom checked exceptions caught in the controller and shown as a clear message
  on the same page. Unexpected failures (and any URL with no matching controller) are caught by
  `GlobalExceptionHandler` (`@ControllerAdvice`), printed to the console with
  `ex.printStackTrace()`, and shown to the user as the same safe `error.jsp` page — no stack
  traces are ever exposed to the user.
- **Logging:** kept intentionally simple — plain `System.out.println` calls, no external
  logging library. `RequestTracingFilter` prints every request's method, path, response status
  and duration. Application code prints on normal operations (login success, leave submitted)
  and on failed logins and unexpected errors. Passwords are never printed.
- **Spring IoC / DI:** all Service and Repository beans are discovered via component scanning
  (`@Service`, `@Repository`, `@Controller`) and wired using **constructor injection** — no
  `new` is used to create Spring-managed beans. The application uses two contexts, both defined
  as **Java `@Configuration` classes** (no XML): a root context (`RootConfig`) for
  DataSource/JPA/Service/Repository beans, and a child web context (`WebConfig`) for
  Controllers and the JSP view resolver.

### 8.1 Configuration: Java-based, no XML, no Spring Boot

There is **no `web.xml`, no Spring XML file, no `application.properties`, and no Spring Boot**
anywhere in this project. Everything is plain Spring 6 (Spring Core / Spring MVC), wired with
annotated Java classes in `com.leaveportal.config`:

| Old file | Replaced by |
|---|---|
| `web.xml` | `AppInitializer` (implements `WebApplicationInitializer` — Tomcat auto-detects and runs it on startup, no `web.xml` needed at all) |
| `root-context.xml` | `RootConfig` (`@Configuration`, `@Bean` methods for `DataSource`, JPA `EntityManagerFactory`, `TransactionManager`) |
| `mvc-dispatcher-servlet.xml` | `WebConfig` (`@Configuration`, `@EnableWebMvc`, view resolver bean, static resource handler) |
| `<error-page>` entries | `GlobalExceptionHandler` (`@ControllerAdvice`) — same `error.jsp` is shown for a missing page (404) or any unexpected exception (500) |
| `application.properties` | Plain Java constants at the top of `RootConfig` (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JPA_DIALECT`, etc.) |

`application.properties` is gone completely — that file is a Spring Boot convention (Boot reads
it automatically to auto-configure beans), and this project does not use Spring Boot at all, so
keeping that file around would be misleading. The same 4 DB values and 3 JPA/Hibernate values it
used to hold are now just `private static final String` constants declared directly in
`RootConfig.java`, and are read straight into the `dataSource()` and `entityManagerFactory()`
`@Bean` methods — no property placeholders, no external file, nothing auto-configured. To point
the app at a different database, edit those constants and nothing else.

---

## 9. What Is Intentionally Out of Scope

Per the assignment: HTTPS, Spring Security, registration/password reset, notifications, file
upload, REST/JSON APIs, microservices, advanced UI/UX, caching/scheduling/i18n, advanced AOP,
custom Spring scopes, and advanced/centralized transaction-management implementation.

---

## 10. Git

```
git init
git add .
git commit -m "Initial commit: Employee Leave Management Portal"
```

No passwords, API keys, or database secrets are hard-coded in a *config file* — the only
credentials present are local-development defaults declared as constants at the top of
`RootConfig.java`, meant to be changed per environment.
