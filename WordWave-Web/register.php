<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registration</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <div class="login-container">
        <form class="login-form" method="post" action="">
            <img src="logo.png" alt="Company Logo" class="logo">
            <h2>Register</h2>
            <hr>
            <?php
            $error = "";

            if (isset($_POST["submit"])) {
                $name = trim($_POST["txtName"]);
                $email = trim($_POST["txtEmail"]);
                $pass = trim($_POST["txtPassword"]);
                $confirmpass = trim($_POST["txtConfirmPassword"]);

                if (empty($name) || empty($email) || empty($pass) || empty($confirmpass)) {
                    $error = "All fields are required.";
                } elseif (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
                    $error = "Invalid email format.";
                } elseif ($pass !== $confirmpass) {
                    $error = "Passwords do not match.";
                } else {
                    require "db.php";
                    
                    $q1 = "INSERT INTO registerdetail VALUES ('$name', '$email', '$pass','$confirmpass') ";
                    $conn->query($q1);

                    $q2 = "INSERT INTO logindetail VALUES ('$email', '$pass')";
                    $conn->query($q2);

                    $conn->close();
                    session_start();
                    $_SESSION["email"] = $email;
                    
                    header("Location: home.php");
                    exit;
                }
            }

            if (!empty($error)) {
                echo "<p style='color:red; text-align:center;'>$error</p>";
            }
            ?>
            <div class="input-group">
                <label for="name">Full Name</label>
                <input type="text" id="name" name="txtName" placeholder="Enter your full name">
            </div>
            <div class="input-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="txtEmail" placeholder="Enter your E-mail">
            </div>
            <div class="input-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="txtPassword" placeholder="Password">
            </div>
            <div class="input-group">
                <label for="confirm-password">Confirm Password</label>
                <input type="password" id="confirm-password" name="txtConfirmPassword" placeholder="Confirm password">
            </div>
            <button type="submit" name="submit" class="submit-btn">Register</button>
            <div class="register-link">
                Already have an account? <a href="index.php">Login</a>
            </div>
        </form>
    </div>
</body>
</html>
