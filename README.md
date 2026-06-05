# Utility Billing System — Enterprise Spring Boot Backend

A complete production-ready Spring Boot backend system for postpaid utility billing management (water & electricity). Features dynamic JWT authentication, customer profiles, meter installations, automated meter reading validations, tariff configuration versioning, automated billing engine, payment ledger tracking, and automated system alerts/notifications.

---

## 🛠️ Technology Stack

| Layer | Technology |
|---|---|
| **Core Framework** | Spring Boot 3.4.5 |
| **Language Runtime** | Java 21 |
| **Security** | Spring Security + JWT (jjwt 0.12.6) |
| **Database** | PostgreSQL 12+ (tested on v18) |
| **Database Migrations** | Flyway |
| **Persistence (ORM)** | Spring Data JPA + Hibernate |
| **DTO Mappings** | Manual Mapper Pattern (Compile-safe & high performance) |
| **Validation Layer** | Jakarta Bean Validation |
| **API Documentation** | SpringDoc OpenAPI 3 (Swagger UI) |
| **Mailing** | Spring Boot Starter Mail (JavaMailSender) |
| **Developer Tools** | Project Lombok |

---

## 🚀 Getting Started

### Prerequisites
- **Java 21+** installed and set in `PATH`
- **Maven 3.9+** installed and set in `PATH`
- **PostgreSQL** instance running locally on port `5432`

### 1. Database Creation
Create a new PostgreSQL database called `utility`:
```sql
CREATE DATABASE utility;
```

### 2. Local Configuration Override
Credentials and local variables are managed in `src/main/resources/application-local.properties` (which is excluded from Git to prevent exposing credentials):

Create this file if it does not exist, and add:
```properties
# DataSource Settings
spring.datasource.url=jdbc:postgresql://127.0.0.1:5432/utility
spring.datasource.username=postgres
spring.datasource.password=your_db_password

# JWT Signing Secret (Base64 encoded, min 256-bit)
app.jwt.secret=ZXhhbXBsZS1qd3Qtc2VjcmV0LXdyaXR0ZW4td2l0aC1tdWx0aXBsZS1jaGFyYWN0ZXJzLWZvci1oczI1Ng==

# SMTP Mail Settings (Optional)
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
app.mail.from=noreply@yourdomain.com
app.mail.from-name=UtilityBillingSystem
app.mail.base-url=http://localhost:8080

# Flyway Settings for Local Dev
spring.flyway.clean-disabled=false
spring.flyway.clean-on-validation-error=true
```

### 3. Build & Run
Compile the application and download dependencies:
```powershell
mvn clean compile
```

Launch the Spring Boot application using the `local` profile:
```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

The database schemas, triggers, functions, and seed data will migrate automatically on boot.

---

## 🔑 Seeded Test Accounts
All seeded accounts share the default password: **`Secret@123`**

| Role | Username (Email) | Password | Description |
|---|---|---|---|
| **System Admin** | `admin@utility.com` | `Secret@123` | Can create tariffs, delete meters/customers, search records |
| **Utility Operator** | `operator@utility.com` | `Secret@123` | Responsible for creating customers and uploading meter readings |
| **Finance Officer** | `finance@utility.com` | `Secret@123` | Approves bills and logs customer payments |
| **Customer Support User** | `customer@utility.com` | `Secret@123` | Normal customer role accessing their own bills and payments |

---

## 📐 System Flows & Database Model

### 1. Database Entity-Relationship Diagram (ERD)

The relational schema is structured as follows, separating authentication credentials and roles from the physical customer asset register, readings ledger, tariffs, billing schedules, payment history, and system notifications:

```mermaid
erDiagram
    roles {
        bigint id PK
        varchar name UK
    }
    users {
        bigint id PK
        varchar full_name
        varchar email UK
        varchar phone_number
        varchar password
        varchar status
        bigint role_id FK
        timestamptz created_at
        timestamptz updated_at
        boolean deleted
    }
    customers {
        bigint id PK
        varchar customer_code UK
        varchar full_name
        varchar national_id UK
        varchar email UK
        varchar phone_number
        text address
        varchar status
        date registration_date
    }
    meters {
        bigint id PK
        varchar meter_number UK
        varchar meter_type
        date installation_date
        varchar status
        bigint customer_id FK
    }
    meter_readings {
        bigint id PK
        bigint meter_id FK
        decimal previous_reading
        decimal current_reading
        decimal consumption
        date reading_date
        int month
        int year
        bigint captured_by FK
    }
    tariffs {
        bigint id PK
        varchar tariff_name
        varchar meter_type
        varchar tariff_type
        decimal rate_per_unit
        decimal fixed_charge
        decimal vat_percentage
        decimal penalty_percentage
        int version
        date effective_from
        date effective_to
        varchar status
    }
    bills {
        bigint id PK
        varchar bill_number UK
        bigint customer_id FK
        bigint meter_id FK
        bigint meter_reading_id FK
        bigint tariff_id FK
        int billing_month
        int billing_year
        decimal consumption
        decimal amount_before_tax
        decimal tax_amount
        decimal penalty_amount
        decimal total_amount
        decimal paid_amount
        decimal balance
        varchar status
        timestamptz generated_date
    }
    payments {
        bigint id PK
        varchar payment_reference UK
        bigint bill_id FK
        decimal amount_paid
        varchar payment_method
        timestamptz payment_date
        bigint received_by FK
    }
    notifications {
        bigint id PK
        bigint customer_id FK
        text message
        varchar status
        timestamptz created_at
    }
    email_verification_tokens {
        bigint id PK
        varchar token UK
        bigint user_id FK
        timestamptz expiry_date
    }
    password_reset_tokens {
        bigint id PK
        varchar token UK
        bigint user_id FK
        timestamptz expiry_date
    }

    users ||--|| roles : "has"
    users ||--o| email_verification_tokens : "has"
    users ||--o| password_reset_tokens : "has"
    customers ||--o{ meters : "owns"
    customers ||--o{ bills : "receives"
    customers ||--o{ notifications : "receives"
    meters ||--o{ meter_readings : "logs"
    meters ||--o{ bills : "tracks"
    meter_readings ||--|| users : "captured by"
    meter_readings ||--|| bills : "billed in"
    tariffs ||--o{ bills : "applies to"
    bills ||--o{ payments : "has"
    payments ||--|| users : "received by"
```

---

### 2. Operational System Lifecycle Flows

The sequence diagram below visualizes the three primary system pipelines: Onboarding/Verification, Meter Readings/Billing calculations, and Finance Payment/Ledger settlement.

```mermaid
sequenceDiagram
    autonumber
    actor Customer as Customer / User
    actor Operator as Utility Operator
    actor Admin as System Admin
    actor Finance as Finance Officer
    participant DB as PostgreSQL Database
    participant Email as Asynchronous Email Service

    Note over Customer, DB: 1. Onboarding & Registration Flow
    Operator->>DB: Onboard Customer Profile (Email, National ID, Code)
    Customer->>DB: Public Register Account (Same Email, status='PENDING')
    DB-->>Email: Generate & Save EmailVerificationToken
    Email->>Customer: Send Verification HTML Email (Async)
    Customer->>DB: Call /verify-email?token=<token>
    DB->>DB: Delete token & Set User status to 'ACTIVE'

    Note over Customer, DB: 2. Reading & Billing Engine Flow
    Operator->>DB: Input Meter Reading (currentReading, month, year)
    Admin->>DB: Generate Bill for Meter Reading
    DB->>DB: Trigger: Create PENDING Notification record
    DB-->>Email: Fetch Customer details & Bill info
    Email->>Customer: Send HTML Bill Notification (Async)

    Note over Customer, DB: 3. Ledger Posting & Payment Flow
    Finance->>DB: Record Payment for Bill (amountPaid, method)
    DB->>DB: Trigger: Update Bill paidAmount, balance, and status (PAID/PARTIAL)
    DB->>DB: Trigger: Create PENDING Payment Receipt Notification
    DB-->>Email: Fetch Customer details & Payment info
    Email->>Customer: Send HTML Payment Receipt (Async)
```

---

## 📖 API Documentation & Verification

Once the application is running, you can access documentation and verify endpoints using the following resources:

*   **Swagger UI (Interactive API docs)**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
*   **OpenAPI 3 JSON Specification**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
*   **Postman Collection**: Locate the [UtilityBillingSystem.postman_collection.json](UtilityBillingSystem.postman_collection.json) file at the root of the workspace. Import it into Postman to run pre-configured flows (Login, Create Customer, Log Reading, Generate Bill, Post Payment, and view Notifications).

---

## 🏗️ Core Architecture & Logic

### 1. Database-Level Automated Triggers
To enforce high integrity and speed up billing operations, the PostgreSQL database contains triggers defined in Flyway migration `V2__triggers_and_routines.sql`:
*   **Bill Alert trigger** (`bill_created_trigger`): Whenever a new bill is inserted, a pending notification is automatically inserted into `notifications` for the customer.
*   **Payment & Bill Sync trigger** (`payment_received_trigger`): Whenever a payment is registered, the target bill's `paid_amount` is incremented and its `balance` is updated. If the balance falls to `0`, the bill status is marked as `PAID` (or `PARTIAL` if partially paid). A transaction notification receipt is also logged automatically.

### 2. Billing Engine
Bills are calculated inside `BillingServiceImpl` according to the following formulas:
$$\text{Consumption Cost} = \text{Consumption (Units)} \times \text{Tariff Rate}$$
$$\text{Subtotal} = \text{Consumption Cost} + \text{Fixed Charge}$$
$$\text{Tax Amount} = \text{Subtotal} \times \text{VAT Percentage}$$
$$\text{Penalty Amount} = \text{Subtotal} \times \text{Penalty Percentage} \text{ (If Overdue)}$$
$$\text{Total Bill Amount} = \text{Subtotal} + \text{Tax Amount} + \text{Penalty Amount}$$

### 3. Email Verification & Password Reset Lifecycle
*   **Account Registration**: Newly registered users are assigned a status of `PENDING` and are blocked from logging in (returning a `403 Forbidden` response). An activation link is sent to the registered email address containing a registration token (valid for 24 hours).
*   **Verification**: Invoking `GET /api/v1/auth/verify-email?token=<token>` activates the user status to `ACTIVE`. A helper endpoint `POST /api/v1/auth/resend-verification?email=<email>` exists to request a new token if needed.
*   **Password Reset**: Requesting a reset via `POST /api/v1/auth/forgot-password` (with body `{"email": "..."}`) sends a reset link to the email. The password can then be reset via `POST /api/v1/auth/reset-password` (with body `{"token": "...", "password": "..."}`) containing the 15-minute token.

### 4. Asynchronous HTML Email System
*   Emails are compiled using Thymeleaf HTML templates: `verification.html` (for registrations), `password-reset.html` (for password resets), and `notification.html` (for bill and payment transactions).
*   All email operations run asynchronously under a configured executor pool (`async-email-` thread prefix) to prevent blocking the REST API request threads.

### 5. Strict Payload Validations
*   **National ID**: Validated using `^\d{16}$` to ensure it is exactly 16 digits.
*   **Phone Numbers**: Validated using `^\+?[0-9]{10,15}$` (optional `+` followed by 10 to 15 digits).
*   **Meter Numbers**: Validated using `^[A-Z0-9\-]{5,20}$` (alphanumeric with optional hyphens, 5-20 characters long).
*   **Reading Dates & Months**: Months must be integers between 1 and 12, years must be after 2000, and current reading values must be positive.

---

## 📂 Directory Layout

```
src/main/java/com/utility/billing/
├── audit/                  # AuditAware context for BaseEntity tracing
├── common/                 # Pagination, Specs, validation constraints, and envelopes
├── config/                 # Security configs, Thread pools (Async), and OpenAPI/Swagger Config
├── controller/             # REST Endpoints (Auth, Customer, Meter, Bill, Payment, Notification)
├── dto/                    # Request bodies and API response models
├── entity/                 # Hibernate models (User, Role, Customer, Meter, Bill, Payment, etc.)
├── enums/                  # System-wide Enums (BillStatus, MeterType, CustomerStatus, etc.)
├── exception/              # ControllerAdvice translating logic faults into JSON responses
├── mapper/                 # Compile-safe DTO/Entity mappings
├── repository/             # JPA/Hibernate query interfaces
├── security/               # JWT validation filters and token issuance engines
└── service/                # Core business layer implementation
```

---

## 📜 License
This project is licensed under the MIT License - see the LICENSE file for details.
