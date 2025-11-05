<?php
session_start();
require 'db.php';

// Redirect if not logged in
if (!isset($_SESSION['email'])) {
    header("Location: index.php");
    exit();
}

// Check if the form was submitted
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $email = $_SESSION['email'];
    $fullname = $_POST['fullname'];
    // Note: 'bio' is not saved as it's not in the database schema.

    // --- 1. Update Full Name ---
    // This update runs regardless of the password change.
    $update_name_sql = "UPDATE registerdetail SET name = ? WHERE email = ?";
    $stmt_name = $conn->prepare($update_name_sql);
    $stmt_name->bind_param("ss", $fullname, $email);
    $stmt_name->execute();
    $stmt_name->close();

    // --- 2. Handle Password Change ---
    $current_password = $_POST['current_password'];
    $new_password = $_POST['new_password'];

    // Only proceed if user wants to change the password (both fields are filled)
    if (!empty($current_password) && !empty($new_password)) {
        // Fetch the current password from the database
        $fetch_pass_sql = "SELECT pass FROM logindetail WHERE email = ?";
        $stmt_pass = $conn->prepare($fetch_pass_sql);
        $stmt_pass->bind_param("s", $email);
        $stmt_pass->execute();
        $result = $stmt_pass->get_result();
        
        if ($result->num_rows > 0) {
            $user = $result->fetch_assoc();
            $stored_password = $user['pass'];

            // Compare the submitted current password with the stored one
            // IMPORTANT: This assumes plain text passwords. For production, use password_hash() and password_verify().
            if ($current_password === $stored_password) {
                // Passwords match, update to the new password in logindetail table
                $update_pass_sql_login = "UPDATE logindetail SET pass = ? WHERE email = ?";
                $stmt_update_log = $conn->prepare($update_pass_sql_login);
                $stmt_update_log->bind_param("ss", $new_password, $email);
                $stmt_update_log->execute();
                $stmt_update_log->close();

                // Also update the password in the registerdetail table for consistency
                $update_pass_sql_register = "UPDATE registerdetail SET password = ?, confirm_pass = ? WHERE email = ?";
                $stmt_update_reg = $conn->prepare($update_pass_sql_register);
                // Bind the new password to both password and confirmpassword fields
                $stmt_update_reg->bind_param("sss", $new_password, $new_password, $email);
                $stmt_update_reg->execute();
                $stmt_update_reg->close();

                $_SESSION['message'] = "Profile and password updated successfully!";
            } else {
                // Passwords do not match
                $_SESSION['message'] = "Incorrect current password. Please try again.";
            }
        }
        $stmt_pass->close();
    } else {
        // This message is set if only the name was updated
        $_SESSION['message'] = "Profile updated successfully!";
    }

    $conn->close();
    header("Location: MyProfile.php");
    exit();
}
?>