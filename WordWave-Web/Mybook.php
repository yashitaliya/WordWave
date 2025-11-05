<?php
session_start();
require 'db.php';

if (!isset($_SESSION['email'])) {
    header("Location: index.php");
    exit();
}

$user_email = $_SESSION['email'];

// Fetch user's purchased books
$sql = "SELECT p.*, ub.purchase_date, ub.rental_duration 
        FROM user_books ub 
        JOIN pdfs p ON ub.book_id = p.id 
        WHERE ub.user_email = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $user_email);
$stmt->execute();
$result = $stmt->get_result();

?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Books - WordWave</title>
    <link rel="stylesheet" href="home.css"> <!-- For header/footer -->
    <style>
        /* --- Global Styles & Resets --- */
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body {
    font-family: 'Poppins', sans-serif;
    background-color: #f4f7f9;
    color: #333;
    line-height: 1.6;
}

.container {
    max-width: 1200px;
    margin: 40px auto;
    padding: 0 20px;
}

/* --- Header --- */
.site-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem 2.5rem;
    background-color: #fff;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.logo {
    font-size: 1.5rem;
    font-weight: 600;
    color: #0056b3;
}

.logo i {
    margin-right: 8px;
}

.main-nav ul {
    list-style: none;
    display: flex;
    gap: 2rem;
}

.main-nav a {
    text-decoration: none;
    color: #555;
    font-weight: 500;
    transition: color 0.3s ease;
}

.main-nav a:hover,
.main-nav a.active-nav {
    color: #007bff;
}

.user-profile i {
    font-size: 1.8rem;
    color: #333;
}


/* --- Main Content --- */
h1 {
    font-size: 2.5rem;
    margin-bottom: 1rem;
    color: #2c3e50;
}



/* --- Book List --- */
.book-list {
    display: grid;
    gap: 1.5rem;
}

/* --- Book Card --- */
.book-card {
    display: flex;
    align-items: center;
    gap: 1.5rem;
    background-color: #fff;
    border-radius: 8px;
    padding: 1rem;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    border-left: 5px solid #a0a0a0; /* Default neutral border */
    transition: transform 0.2s ease-in-out;
}

.book-card:hover {
    transform: translateY(-5px);
}

.book-card img {
    width: 100px;
    height: 150px;
    object-fit: cover;
    border-radius: 4px;
    flex-shrink: 0;
}

.book-details {
    flex-grow: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    height: 150px; /* Match image height */
}

.book-details h2 {
    font-size: 1.2rem;
    font-weight: 600;
    margin-bottom: 0.2rem;
}

.author {
    color: #777;
    font-size: 0.9rem;
}

.due-info {
    margin-top: auto; /* Pushes this to the bottom */
}

.due-label {
    font-size: 0.8rem;
    color: #999;
    margin-bottom: -5px;
}

.due-date {
    font-size: 1rem;
    font-weight: 600;
    color: #333;
}

/* --- Card Status Variations --- */
.book-card.overdue {
    border-left-color: #dc3545; /* Red */
}

.overdue-text {
    color: #dc3545;
}

.book-card.due-soon {
    border-left-color: #ffc107; /* Yellow/Orange */
}

/* --- Renew Button --- */
.btn-renew {
    background-color: #4CAF50;
    color: #fff;
    border: none;
    padding: 0.7rem 1.2rem;
    border-radius: 5px;
    font-weight: 500;
    cursor: pointer;
    transition: background-color 0.3s ease;
    align-self: flex-end; /* Aligns button to the bottom right */
}

.btn-renew:hover {
    background-color: #0056b3;
}

/* --- Responsive Design --- */
@media (max-width: 768px) {
    .site-header {
        flex-direction: column;
        gap: 1rem;
    }

    .book-card {
        flex-direction: column;
        align-items: flex-start;
        text-align: left;
    }

    .book-details {
        width: 100%;
        height: auto; /* Allow height to adjust */
        gap: 1rem;
    }

    .btn-renew {
        align-self: flex-start; /* Align button to the left on mobile */
    }
}
    </style>
</head>
<body>

    <header class="site-header">
        <div class="brand">WordWave</div>
            <nav class="nav">
                <a href="home.php">Home</a>
                <a href="#" class="active">My book</a>
                <a href="#">Help</a>
                <a class="btn" href="logout.php">Logout</a>
            </nav>
    </header>

    <main class="container">
        <h1>My Books</h1>

        

        <div class="book-list">
            <?php if ($result->num_rows > 0): ?>
                <?php while($book = $result->fetch_assoc()): 
                    $purchase_date = new DateTime($book['purchase_date']);
                    $expiry_date = (clone $purchase_date)->add(new DateInterval('P' . $book['rental_duration'] . 'D'));
                    $now = new DateTime();
                    $days_left = $expiry_date->diff($now)->days;
                    $is_expired = $now > $expiry_date;
                    $status_class = $is_expired ? 'overdue' : ($days_left <= 3 ? 'due-soon' : '');
                ?>
                    <div class="book-card <?php echo $status_class; ?>">
                        <div class="cover" style="background-image: url('<?php echo htmlspecialchars($book['cover_path']); ?>');"></div>
                        <div class="book-details">
                            <div>
                                <h2><?php echo htmlspecialchars(pathinfo($book['finename'], PATHINFO_FILENAME)); ?></h2>
                                <p class="author">by <?php echo htmlspecialchars($book['Author']); ?></p>
                                <p class="due-info">
                                    <span class="due-label">Access Until</span><br>
                                    <span class="due-date <?php echo $is_expired ? 'overdue-text' : ''; ?>">
                                        <?php echo $expiry_date->format('M j, Y'); ?>
                                    </span>
                                </p>
                            </div>
                            <a href="<?php echo htmlspecialchars($book['filepath']); ?>" class="btn-renew">Read Book</a>
                        </div>
                    </div>
                <?php endwhile; ?>
            <?php else: ?>
                <p>You haven't borrowed any books yet.</p>
            <?php endif; ?>
        </div>
    </main>

</body>
</html>