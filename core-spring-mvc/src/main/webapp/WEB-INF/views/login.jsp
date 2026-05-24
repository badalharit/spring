<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>Admin Login</title>
<style>
body { font-family: Arial, sans-serif; margin: 0; padding: 0; }
        .wrap { min-height: 100vh; display: flex; align-items: center; justify-content: center; padding: 24px; box-sizing: border-box; }
        .box { width: 100%; max-width: 420px; padding: 18px; border: 1px solid #ddd; border-radius: 8px; background: #fff; }

        .row { margin: 10px 0; }
        label { display: block; font-size: 14px; margin-bottom: 6px; }
        input { width: 100%; padding: 10px; box-sizing: border-box; }
        button { padding: 10px 14px; }
        .error { color: #b00020; margin-top: 10px; }
    </style>
</head>
<body>
<div class="wrap">
    <div class="box">




    <h2>Admin Login</h2>

    <form method="post" action="<%= request.getContextPath() %>/login">
        <div class="row">
            <label>Username</label>
            <input type="text" name="username" required/>
        </div>
        <div class="row">
            <label>Password</label>
            <input type="password" name="password" required/>
        </div>
        <button type="submit">Login</button>
    </form>

    <script>
        const hasError = <%= request.getAttribute("loginError") != null ? "true" : "false" %>;


        if (hasError) {
            const div = document.createElement('div');
            div.className = 'error';
            div.textContent = 'Invalid credentials';
            document.querySelector('.box').appendChild(div);
        }
    </script>




</div>
</div>
</body>
</html>


