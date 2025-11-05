<?php
require 'db.php';

// Check if book ID is provided
if (!isset($_GET['id']) || !is_numeric($_GET['id'])) {
    die('Book ID not specified or invalid.');
}

$book_id = (int)$_GET['id'];

// Fetch book details from the database
$sql = "SELECT * FROM pdfs WHERE id = ?";
$stmt = $conn->prepare($sql);

if ($stmt === false) {
    die('SQL Error: ' . htmlspecialchars($conn->error));
}

$stmt->bind_param("i", $book_id);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    $book = $result->fetch_assoc();
} else {
    die('Book not found.');
}
$stmt->close();

// Fetch related books (e.g., 5 random books, excluding the current one)
$related_sql = "SELECT * FROM pdfs WHERE id != ? ORDER BY RAND() LIMIT 15";
$related_stmt = $conn->prepare($related_sql);
$related_stmt->bind_param("i", $book_id);
$related_stmt->execute();
$related_result = $related_stmt->get_result();

$conn->close();
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title><?php echo htmlspecialchars(pathinfo($book['finename'], PATHINFO_FILENAME)); ?> - WordWave</title>
    <link rel="stylesheet" href="home.css">
    <style>

    .book-details-section {
    display: flex;
    gap: 40px;
    padding: 40px 20px;
    max-width: 1200px;
    margin: 0 auto;
    background: #fff;
    align-items: flex-start; /* Align items to the top */
}

.left-panel {
    flex: 0 0 300px; /* Do not grow, do not shrink, base width 300px */
}

.book-cover-lg {
    width: 280px;
    height: 400px;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.right-panel {
    flex: 1; /* Grow to fill available space */
    padding-left: 20px;
}

.right-panel h1 {
    font-size: 2.5rem;
    margin-top: 70px;
    margin-bottom: 8px;
}

.right-panel .author,
.right-panel .publish-date {
    font-size: 1.1rem;
    color: #555;
    margin-bottom: 20px;
}

.borrow-btn {
    margin-top: 30px;
    padding: 6px 30px;
    font-size: 1.2rem;
    text-decoration: none;
    display: inline-block;
    
}

.description-section {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px 40px 20px; /* Add padding to align with content above */
    background: #fff;
}

.description-section h2 {
    font-size: 1.5rem;
    border-bottom: 2px solid #eee;
    padding-bottom: 8px;
    margin-top: 30px;
    margin-bottom: 10px;
}

.description-section .description {
    font-size: 1rem;
    line-height: 1.6;
    color: #333;
}

/* Add margin to the new section */
.section {
    padding-bottom: 40px;
}

    </style>
</head>
<body>
    <div class="app">
        <header class="top">
            <div class="brand">WordWave</div>
            <nav class="nav">
                <a href="home.php">Home</a>
                <a href="#">Catalog</a>
                <a href="#">About us</a>
                <a href="#">Help</a>
                <a class="btn" href="index.php">Login</a>
                     <a class="btn primary" href="#">Signup</a>
            </nav>
        </header>

        <section class="book-details-section">
            <div class="left-panel">
                <div class="book-cover-lg" style="background-image: url('<?php echo htmlspecialchars($book['cover_path']); ?>');background-size:cover;background-position:center; "></div>
            </div>
            <div class="right-panel">
                <h1><?php echo htmlspecialchars(pathinfo($book['finename'], PATHINFO_FILENAME)); ?></h1>
                <p class="author"><?php echo htmlspecialchars(pathinfo($book['Author'], PATHINFO_FILENAME)); ?></p>
                <p class="publish-date"><?php echo "₹ ".htmlspecialchars(pathinfo($book['price'], PATHINFO_FILENAME)); ?></p>
                
                <a href="payment.php?id=<?php echo $book['id']; ?>" class="btn primary borrow-btn" style="background-color:#4CAF50">Pay</a>
            </div>
        </section>

        <section class="description-section">
            <h2>Description</h2>
            <p class="description">
                <?php echo htmlspecialchars(pathinfo($book['descri'], PATHINFO_FILENAME)); ?>
            </p>
        </section>

        <section class="section">
            <h2>You Might Also Like</h2>
            <div class="carousel">
                <button class="arrow left" aria-label="Previous" onclick="scrollRow(-1, 'related-row')">&larr;</button>
                <div id="related-row" class="row">
                    <?php if ($related_result->num_rows > 0): ?>
                        <?php while($related_book = $related_result->fetch_assoc()): ?>
                            <a href="book_details.php?id=<?php echo $related_book['id']; ?>" class="book-link">
                                <article class="book">
                                    <div class="cover" style="background-image: url('<?php echo htmlspecialchars($related_book['cover_path']); ?>');background-size:cover;background-position:center; "></div>
                                    <div class="title"><?php echo htmlspecialchars(pathinfo($related_book['finename'], PATHINFO_FILENAME)); ?></div>
                                    <div class="author"><?php echo htmlspecialchars(pathinfo($related_book['Author'], PATHINFO_FILENAME)); ?></div>
                                </article>
                            </a>
                        <?php endwhile; ?>
                    <?php else: ?>
                        <p>No related books found.</p>
                    <?php endif; ?>
                </div>
                <button class="arrow right" aria-label="Next" onclick="scrollRow(1, 'related-row')">&rarr;</button>
            </div>
        </section>

        <footer>
            <div class="fcol">
                <h4>Browse</h4>
                <a href="#">Catalog</a>
                <a href="#">eBooks</a>
                <a href="#">Audiobooks</a>
            </div>
            <div class="fcol">
                <h4>Services</h4>
                <a href="#">Library Card</a>
                <a href="#">Interlibrary Loan</a>
                <a href="#">Study Rooms</a>
            </div>
            <div class="fcol">
                <h4>About</h4>
                <a href="#">Hours & Locations</a>
                <a href="#">Contact</a>
                <a href="#">Accessibility</a>
            </div>
            <div class="fcol">
                <h4>Newsletter</h4>
                <form class="newsletter" onsubmit="return false;">
                    <div class="left">Get new arrivals and event updates in inbox.</div>
                    <input type="email" placeholder="Email address" />
                    <button>Subscribe</button>
                </form>
            </div>
            <div class="footbar">
                <span>© 2025 WordWave</span>
                <span>Privacy · Terms · Help</span>
            </div>
        </footer>
    </div>
    <script>
        function scrollRow(dir, rowId) {
            const el = document.getElementById(rowId);
            el.scrollBy({ left: dir * 260, behavior: 'smooth' });
        }
    </script>
</body>
</html>