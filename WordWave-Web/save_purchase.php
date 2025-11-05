<?php
session_start();
require 'db.php';

header('Content-Type: application/json');

if (!isset($_SESSION['email'])) {
    echo json_encode(['success' => false, 'message' => 'User not logged in']);
    exit;
}

// Get POST data
$data = json_decode(file_get_contents('php://input'), true);
$user_email = $_SESSION['email'];
$book_id = $data['book_id'];
$rental_duration = $data['rental_duration'];

// Insert into user_books table
$sql = "INSERT INTO user_books (user_email, book_id, rental_duration) VALUES (?, ?, ?)";
$stmt = $conn->prepare($sql);
$stmt->bind_param("sii", $user_email, $book_id, $rental_duration);

if ($stmt->execute()) {
    echo json_encode(['success' => true]);
} else {
    echo json_encode(['success' => false, 'message' => 'Database error']);
}

$stmt->close();
$conn->close();
?>