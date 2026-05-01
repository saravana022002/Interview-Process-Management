<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Admin login</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Merriweather:wght@700&family=Space+Grotesk:wght@400;500;600&display=swap" rel="stylesheet">
<link rel="stylesheet" href="assets/app.css">
</head>
<body>
<main class="page">
    <div class="topbar">
        <span class="kicker">Admin</span>
        <a class="btn ghost" href="index.html">Back</a>
    </div>

    <section class="card col-6" style="max-width:560px;">
        <h2>Sign In</h2>
        <p>Use your administrator credentials to manage tests and candidate permissions.</p>

        <form action="LoginCheck" method="post">
            <div class="field-grid">
                <label>Name
                    <input type="text" name="name" required>
                </label>
                <label>Password
                    <input type="password" name="password" required>
                </label>
            </div>
            <div class="actions">
                <input type="submit" name="Submit" value="Enter Dashboard">
            </div>
        </form>
    </section>
</main>
</body>
</html>
