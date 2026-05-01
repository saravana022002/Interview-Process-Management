# Interview-Process-Management
This is a web application in which there are two roles of users,one is Client who can attend the Test, another one is Admin who can block or allow a particular user to write the test.

Note:
   I have added the Db Structure in the DbDesign file,so that you can create the tables in MySql with that.

## Run locally

### 1) Setup database

Create the schema and seed one admin user:

```sql
source Interview/dbdesign/setup.sql;
```

Default admin login:

- username: `admin`
- password: `admin123`

### 2) Configure DB connection

Use environment variables before starting Tomcat:

```bash
export DB_URL="jdbc:mysql://localhost:3306/employee?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export DB_USER="root"
export DB_PASSWORD="your_mysql_password"
```

### 3) Build WAR

```bash
cd Interview
mvn clean package
```

WAR output:

- `Interview/target/interview-process-management-1.0.0.war`

### 4) Deploy

Deploy the generated WAR in Tomcat 9+ and open:

- `http://localhost:8080/interview-process-management-1.0.0/`

## V2 workflow

- Admin login goes to centralized sidebar control center: `admin-dashboard.jsp`
- Admin can:
  - create tests and set base duration
  - add questions to selected test
  - allow/block candidates instantly
  - add extra time for a running candidate
- Candidate gets a real test window with countdown timer: `candidate-test.jsp`
- Real-time updates (block/unblock/time extension) use WebSocket endpoint:
  - `/ws/candidate/{attemptId}`

### Note for old databases

`setup.sql` now defines the V2-only schema (`tests`, `test_questions`, `candidate_test`, `candidate_answers`). Legacy `questions` table migration support is removed.
