<?php
$conn = mysqli_connect('localhost', 'root', '');
mysqli_select_db($conn, 'wordwave');
if ($conn->connect_error) {
    die("
    ". $conn->connect_error);
}
?>