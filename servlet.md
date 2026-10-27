# Java Servlet Cheat Sheet — Tomcat 10.1

A beginner-friendly guide to writing, running, and understanding Java Servlets using **Tomcat 10.1** and **Java 17**.

> **Important:** Tomcat 10+ uses `jakarta.servlet.*`. Older tutorials may use `javax.servlet.*`; do not mix the two APIs. This guide uses `jakarta.servlet.*`.

---

## 1. What is a Servlet?

A **Servlet** is a Java class that runs inside a servlet container such as Apache Tomcat. It receives an HTTP request, runs Java logic, and sends an HTTP response.

**Remember the flow:**

`Browser/Form → HTTP Request → Tomcat → Servlet → Java Logic → HTTP Response → Browser`

Common uses:
- Process HTML form submissions.
- Read query parameters.
- Calculate or validate values.
- Display a response in the browser.
- Connect to a database (usually through a separate DAO/service class).

---

## 2. Basic project structure

For a simple project deployed as a WAR:

```text
MyServletApp/
├── src/
│   └── com/example/
│       └── HelloServlet.java
└── web/
    └── index.html
```

A traditional web project may use `src/main/java` and `src/main/webapp`. If compiling manually, the key requirements are that the servlet class is compiled into `WEB-INF/classes` and the HTML page is inside the web app.

Typical deployed structure:

```text
MyServletApp/
├── index.html
└── WEB-INF/
    └── classes/
        └── com/example/HelloServlet.class
```

---

## 3. Smallest working servlet

Create `HelloServlet.java`:

```java
package com.example;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/hello")
public class HelloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();
        out.println("<h1>Hello from Servlet!</h1>");
    }
}
```

**What each important line means:**
- `@WebServlet("/hello")`: maps the servlet to `/hello`.
- `extends HttpServlet`: makes this class an HTTP servlet.
- `doGet(...)`: handles HTTP GET requests.
- `request`: contains incoming data.
- `response`: is used to send output back.
- `setContentType(...)`: tells the browser what kind of response it is receiving.
- `getWriter()`: obtains a writer to send text/HTML.

---

## 4. HTML form that sends two numbers

Create `index.html` in the web app's root:

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Two Numbers</title>
</head>
<body>
    <h2>Add Two Numbers</h2>

    <form action="add" method="post">
        <label>First number:</label>
        <input type="number" name="num1" required>
        <br><br>

        <label>Second number:</label>
        <input type="number" name="num2" required>
        <br><br>

        <button type="submit">Add</button>
    </form>
</body>
</html>
```

Create `AddServlet.java`:

```java
package com.example;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/add")
public class AddServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        try {
            int num1 = Integer.parseInt(request.getParameter("num1"));
            int num2 = Integer.parseInt(request.getParameter("num2"));
            int sum = num1 + num2;

            PrintWriter out = response.getWriter();
            out.println("<h2>Sum = " + sum + "</h2>");
            out.println("<a href='index.html'>Go back</a>");
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Please enter valid whole numbers.");
        }
    }
}
```

**Form-to-servlet flow:**
1. User enters values into fields named `num1` and `num2`.
2. The form submits a POST request to `add`.
3. Tomcat routes `/add` to `AddServlet`.
4. `request.getParameter("num1")` reads the first value as a string.
5. `Integer.parseInt(...)` converts the string into an integer.
6. The servlet adds the numbers and writes the result to the response.

### GET versus POST

- `doGet()` is used for GET requests, commonly for reading or displaying data.
- `doPost()` is used for POST requests, commonly for form submissions that create or process data.
- The form's `method` must match the servlet method you implement.

---

## 5. The five-step servlet writing pattern

For most basic lab questions:

1. **Import** servlet classes and `IOException`.
2. **Extend** `HttpServlet`.
3. **Map** the URL using `@WebServlet("/path")`.
4. **Override** `doGet()` or `doPost()`.
5. **Read → Process → Respond**.

Memorize this pattern:

```java
@WebServlet("/path")
public class MyServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String value = request.getParameter("fieldName");
        // Process value here.

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println("Result: " + value);
    }
}
```

---

## 6. Reading form inputs

HTML:

```html
<input type="text" name="username">
<input type="number" name="age">
```

Servlet:

```java
String username = request.getParameter("username");
String ageText = request.getParameter("age");
```

`getParameter()` returns a `String`, even when the HTML input is numeric. Convert it before arithmetic:

```java
int age = Integer.parseInt(ageText);
```

For production code, validate nulls, empty values, ranges, and invalid input before conversion.

---

## 7. Running with Tomcat 10.1 on Windows

### A. Check Java

Open Command Prompt:

```bat
java -version
```

If `java` is not found, install/configure a JDK and set `JAVA_HOME` to the JDK folder.

Example (change the path to match your computer):

```bat
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"
```

### B. Start Tomcat

Open Command Prompt and move into Tomcat's `bin` directory. Example:

```bat
cd C:\path\to\apache-tomcat-10.1\bin
startup.bat
```

If the console window closes or you need to see the error, run:

```bat
catalina.bat run
```

Leave that terminal open while testing. Default address:

`http://localhost:8080`

### C. Deploy the web app

For a manually compiled project, make sure the app has the structure described above. Put the web app folder inside Tomcat's `webapps` directory, or package it as a `.war` file and copy the WAR into `webapps`.

If the folder is named `MyServletApp`, open:

- `http://localhost:8080/MyServletApp/` for `index.html`
- `http://localhost:8080/MyServletApp/hello` for `HelloServlet`
- `http://localhost:8080/MyServletApp/add` is the form's POST endpoint; open the form page and submit it instead of typing the endpoint into the address bar.

Tomcat deploys applications from `webapps` by default. Watch the Tomcat console/logs for deployment errors.

---

## 8. Compiling manually (without Maven)

You need Tomcat's servlet API JAR on the compile classpath. For Tomcat 10.1, it is normally:

`%CATALINA_HOME%\lib\servlet-api.jar`

Example Command Prompt commands (adjust all paths and filenames):

```bat
set "CATALINA_HOME=C:\path\to\apache-tomcat-10.1"
mkdir MyServletApp\WEB-INF\classes
javac -cp "%CATALINA_HOME%\lib\servlet-api.jar" -d MyServletApp\WEB-INF\classes src\com\example\HelloServlet.java
```

Copy `index.html` into `MyServletApp`, then copy the entire `MyServletApp` folder into Tomcat's `webapps` directory. Compile `AddServlet.java` in the same way if using the add-form example.

**Note:** The `package` statement must match the directory path. A class declared as `package com.example;` belongs under `WEB-INF/classes/com/example/`.

---

## 9. Common errors

### `package javax.servlet does not exist`
For Tomcat 10.1, replace old imports such as:

```java
import javax.servlet.*;
import javax.servlet.http.*;
```

with the corresponding `jakarta.servlet.*` imports. Do not combine `javax.servlet` and `jakarta.servlet` in one project.

### `JAVA_HOME` or `JRE_HOME` is not defined
Set `JAVA_HOME` to the installed **JDK directory**, not to its `bin` folder and not to `java.exe`. Then open a new terminal or set it in the current terminal as shown above.

### HTTP 404
Check:
- Tomcat is running.
- The application folder/WAR is deployed under `webapps`.
- The application context name is correct.
- The URL in `@WebServlet("/add")` matches the URL being requested.
- The Java class compiled successfully.

### HTTP 405
The URL may be correct, but the HTTP method is wrong. For example, opening a POST-only `/add` endpoint in the address bar sends GET. Submit the HTML form instead, or implement `doGet()` if GET is intended.

### `NumberFormatException`
The submitted input is not a valid integer. Check the input names and validate the text before converting it.

### Servlet changes do not appear
Recompile the class and redeploy/restart the application if needed. Confirm the updated `.class` file is under the deployed app's `WEB-INF/classes`.

---

## 10. Quick viva questions

**1. What is a Servlet?**  
A Java server-side component that handles requests and creates responses.

**2. What is Tomcat?**  
A web server and servlet container that runs Java web applications.

**3. What is `HttpServlet`?**  
A base class for servlets that handle HTTP requests.

**4. What is `doGet()`?**  
A method that handles HTTP GET requests.

**5. What is `doPost()`?**  
A method that handles HTTP POST requests.

**6. What is `HttpServletRequest`?**  
An object that gives the servlet access to request data, parameters, and headers.

**7. What is `HttpServletResponse`?**  
An object used to set response details and send output to the client.

**8. What does `getParameter()` do?**  
Reads a named request parameter, usually from a query string or submitted form.

**9. What is `@WebServlet`?**  
An annotation that maps a servlet to a URL pattern.

**10. Why use `jakarta.servlet` instead of `javax.servlet` here?**  
Tomcat 10+ uses the Jakarta Servlet namespace.

**11. Does `getParameter()` return an integer?**  
No. It returns a `String`; convert and validate it before numeric operations.

**12. What is the servlet lifecycle?**  
The container loads and creates the servlet, calls `init()`, calls `service()` for requests (which dispatches to methods such as `doGet()`/`doPost()`), then calls `destroy()` before removing it.

---

## 11. Last-minute memory sheet

| Item | Remember |
|---|---|
| Servlet class | `extends HttpServlet` |
| URL mapping | `@WebServlet("/path")` |
| Read a field | `request.getParameter("name")` |
| GET handler | `doGet()` |
| POST handler | `doPost()` |
| Set HTML response | `response.setContentType("text/html;charset=UTF-8")` |
| Print response | `response.getWriter().println(...)` |
| Convert text to integer | `Integer.parseInt(text)` |
| Tomcat 10+ imports | `jakarta.servlet.*` |
| Typical deployment path | `webapps/MyServletApp/` |
| Request flow | Request → Servlet logic → Response |

**Best exam strategy:** Write the imports, annotation, class declaration, method signature, input-reading code, logic, and response in that order.
