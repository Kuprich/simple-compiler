# Java Compiler API

A simple REST API that compiles and runs Java code inside Docker containers.  
It uses **Spring Boot** together with **Docker-in-Docker (DinD)** to safely execute untrusted code.

---

## 📌 API Endpoints

### `POST /api/compiler/run`

Compile and execute Java code inside an isolated container.

#### Request Example

```http
POST http://localhost:8080/api/compiler/run
Content-Type: application/json
{
  "filename": "Hello.java",
  "code": "public class Hello { public static void main(String[] args) { System.out.println(\"Hello from Dockerized compiler!\"); } }"
}
```
Response Example
```json

{
  "success": true,
  "logs": "Hello from Dockerized compiler!\n",
  "containerId": "3a0bea56da7cb82ffab0550f36c98973ed511539ad1aff5ca6c416132026b123"
}
```
- success → true if the program compiled and executed successfully.
- logs → stdout/stderr output from the program.
- containerId → ID of the temporary container used for compilation.

### 🚀 Run Locally with Docker Compose
Make sure you have Docker and Docker Compose installed.
Then run:

```bash
docker compose up --build
```
The API will be available at: <br>
👉 http://localhost:8080/api/compiler/run

### 📂 Project Structure
- compiler-api → Spring Boot service that exposes REST API and handles compilation requests
- dind → Docker-in-Docker service used to run containers securely and isolate code execution

### ⚠️ Notes
The filename in the request must match the name of the public class in Java.

- Example: Hello.java → public class Hello
- Temporary source files are created inside a shared /code volume between API and DinD.
- Containers are automatically cleaned up after execution.

### 🛠️ Future Improvements
Auto-detect public class name if filename is missing.
Support additional languages (Python, C++, etc.).
Simple web-based UI (code editor + output window).