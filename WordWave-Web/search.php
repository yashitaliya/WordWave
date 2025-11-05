<?php
require 'db.php';

header('Content-Type: application/json');

if (!isset($_GET['q']) || trim($_GET['q']) === '') {
    echo json_encode([]);
    exit;
}

$q = $conn->real_escape_string($_GET['q']);

// Corrected: Use lowercase 'author' for consistency with JavaScript
$sql = "SELECT id, finename AS bookName, Author AS author, cover_path FROM pdfs WHERE finename LIKE '%$q%' LIMIT 10";

$result = $conn->query($sql);

$books = [];
if ($result && $result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $books[] = $row;
    }
}

echo json_encode($books);

?>