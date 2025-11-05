<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <div class="login-container">
        <form class="login-form" method="post" action="">
            <img src="logo.png" alt="Company Logo" class="logo">
            <h2>Login</h2>
            <hr>
            <?php
            session_start();
            $error = "";

            if (isset($_POST["submit"])) {
                $email = trim($_POST["txtEmail"]);
                $pass  = trim($_POST["txtPassword"]);

                if (empty($email) || empty($pass)) {
                    $error = "All fields are required.";
                } else {
                    require "db.php";
                    $q1 = "SELECT * FROM logindetail WHERE email='$email' AND pass='$pass'";
                    $result = $conn->query($q1);

                    if ($result && $result->num_rows == 1) {
                        $_SESSION["email"] = $email;
                        header("Location: home.php");
                        exit;
                    } else {
                        $error = "Invalid email or password.";
                    }

                    $conn->close();
                }
            }

            if (!empty($error)) {
                echo "<p style='color:red; text-align:center;'>$error</p>";
            }
            ?>
            <div class="input-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="txtEmail" placeholder="Enter E-mail">
            </div>
            <div class="input-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="txtPassword" placeholder="Password">
            </div>
            <button type="submit" name="submit" class="submit-btn">Submit</button>
            <div class="register-link">
                Don't have an account? <a href="register.php">Register</a>
            </div>
        </form>
    </div>
</body>
</html>
