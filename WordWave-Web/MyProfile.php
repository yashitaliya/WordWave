<?php
 session_start();
 if (!isset($_SESSION['email'])) {
     header("Location: index.php");
     exit();
 }

 require "db.php";

 $email =  $_SESSION['email'];
 $fullname = '';
 $username = '';
 $bio = '';

// Prepare and execute the query to get user details from the registerdetail table
 $sql = "SELECT name FROM registerdetail WHERE email = ?";
 $stmt = $conn->prepare($sql);

 if ($stmt) {
     $stmt->bind_param("s", $email);
     $stmt->execute();
     $result = $stmt->get_result();

     if ($result->num_rows > 0) {
         $user = $result->fetch_assoc();
         // Use the 'name' column for both fullname and username
         $fullname = $user['name'];
         $username = $user['name']; 
     }
     $stmt->close();
 }
 $conn->close();

?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Profile - WordWave</title>
    <link rel="stylesheet" href="home.css"> <!-- For header/footer -->
    <link rel="stylesheet" href="style.css"> <!-- For profile styles -->
</head>
<body class="profile-page-body">

    <div class="app">
        <header class="top">
            <div class="brand">WordWave</div>
            <nav class="nav">
                <a href="home.php">Home</a>
                <a href="Mybook.php">My Book</a>
                <a href="#" class="active">My Profile</a>
                <a href="#">Help</a>
                <a class="btn" href="logout.php">Logout</a>
            </nav>
        </header>

        <div class="profile-container">
            <h2>My Profile</h2>
            <?php
                // Display the success or error message
                if (isset($_SESSION['message'])) {
                    echo '<p class="profile-message">' . htmlspecialchars($_SESSION['message']) . '</p>';
                    unset($_SESSION['message']); // Clear the message after displaying it
                }
            ?>
            <form class="profile-content" action="update_profile.php" method="POST">
                
                <div class="left-profile">
                    <img src="users.png" alt="User Avatar" class="logo">
                    <h3><?php echo htmlspecialchars($username); ?></h3>
                    <label for="bio">Your Bio</label>
                    <textarea id="bio" name="bio" rows="4" placeholder="Tell us a little about yourself..."><?php echo htmlspecialchars($bio); ?></textarea>
                </div>

                <div class="right-profile">
                    <div class="right-data">
                        <label for="fullname">Full Name</label>
                        <input type="text" id="fullname" name="fullname" value="<?php echo htmlspecialchars($fullname); ?>">
                    </div>
                    <div class="right-data">
                        <label for="email">Email Address</label>
                        <input type="email" id="email" name="email" value="<?php echo htmlspecialchars($email); ?>">
                    </div>
                    
                    <hr>
                    
                     <div class="right-data">
                        <label for="current_password">Current Password</label>
                        <input type="password" id="current_password" name="current_password" 
                               maxlength="8" placeholder="Enter current password">
                    </div>
                    <div class="right-data">
                        <label for="new_password">New Password</label>
                        <input type="password" id="new_password" name="new_password" 
                               maxlength="8" placeholder="Enter new password">
                    </div>
                    <button type="submit" class="profile-submit">Save Changes</button>
                </div>
            </form>
        </div>

        <footer style="position:relative; bottom: 0; width: 100%;">
            <div class="footbar">
                <span>© 2025 WordWave</span>
                <span>Privacy · Terms · Help</span>
            </div>
        </footer>
    </div>

</body>
</html>
