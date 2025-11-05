<?php
require 'db.php';
// --- 1. Database Connection ---
$featured_sql = "SELECT * from pdfs";
$featured_result = $conn->query($featured_sql);

// Fetch the next 6 books for the "Self-Help" section
$selfhelp_sql = "SELECT * FROM pdfs";
$selfhelp_result = $conn->query($selfhelp_sql);
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>WordWave</title>
    <link rel="stylesheet" href="home.css">
</head>
<body>
    <div class="app">
        <header class="top">
            <div class="brand">WordWave</div>
            <nav class="nav">
                <a class="active" href="#">Home</a>
                <a href="Mybook.php">My Book</a>
                <a href="#">Help</a>
                <a class="btn" href="index.php">Login</a>
                <a class="btn" href="MyProfile.php">Myprofile</a>
            </nav>
        </header>

        <section class="hero">
            <h1 style="color:white; text-align:center; font-size:40px;"> Discover Your Next Great Read </h1>
                    <h2 style="color:white; text-align:center; font-size:13px;">Explore our vast collection of books and find your next adventure.</h2>
            <form class="search" role="search" onsubmit="return false;">
                <input id="searchInput" placeholder="Search books, journals, and research papers..." />                 <button aria-label="Search">🔍</button>
            </form>
            
        </section>
        <div id="searchResults" class="search-results"></div>
       <section class="section">
            <h2 style="margin-left: 35px;">Featured Books</h2>
            <div class="row" style="gap: 30px; display:flex; flex-wrap:wrap; justify-content:center;"> <!-- Changed: added center alignment -->
                <?php if ($featured_result->num_rows > 0): ?>
                    <?php while($book = $featured_result->fetch_assoc()): ?>
                        <a href="book_details.php?id=<?php echo $book['id']; ?>" class="book-link">
                            <article class="book" style="width:150px;">
                                <div class="cover" style="background-image: url('<?php echo htmlspecialchars($book['cover_path']); ?>');background-size:cover;background-position:center; width:100%; height:160px;"></div>
                                <div class="book-meta" style="margin-top:8px;">
                                    <div class="title" style="word-break:break-word;"><?php echo htmlspecialchars(pathinfo($book['finename'], PATHINFO_FILENAME)); ?></div>
                                    <div class="author"><?php echo htmlspecialchars(pathinfo($book['Author'], PATHINFO_FILENAME)); ?></div>
                                </div>
                            </article>
                        </a>
                    <?php endwhile; ?>
                <?php else: ?>
                    <p>No featured books found.</p>
                <?php endif; ?>
            </div>
        </section>

        <section class="section">
            <h2 style="margin-top:24px">FAQ</h2>
            <div class="faq">
                <details>
                    <summary>How do I get a digital library card?</summary>
                    <p>Click Signup, complete the form, and verify email to activate instant access.</p>
                </details>
                <details>
                    <summary>Can I read offline?</summary>
                    <p>Yes. Use the app’s Download for Offline toggle on supported titles.</p>
                </details>
                <details>
                    <summary>What types of books are available?</summary>
                    <p>We offer a wide range of genres, including fiction, non-fiction, and academic texts.</p>
                </details>
                <details>
                    <summary>How do I return a digital book?</summary>
                    <p>Digital books are automatically returned on their due date, so no action is required.</p>
                </details>

                 <details>
                    <summary>Is there a limit to how many books I can borrow?</summary>
                    <p>Yes, the limit is 10 books at a time.</p>
                </details>
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
        function scrollRow(dir, rowId) { // Corrected: Added 'rowId' parameter
            const el = document.getElementById(rowId); // Corrected: Use 'rowId'
            if(el) { // Added a check to prevent errors
                el.scrollBy({ left: dir * 260, behavior: 'smooth' });
            }
        }

const searchInput = document.getElementById('searchInput');
const searchResults = document.getElementById('searchResults');

searchInput.addEventListener('input', function() {
    const query = this.value.trim();

    // Hide results if input is less than 2 chars
    if (query.length < 2) {
        searchResults.innerHTML = '';
        searchResults.style.display = 'none';
        searchResults.classList.remove('section');
        return;
    }

    // Fetch matching books
    fetch('search.php?q=' + encodeURIComponent(query))
            .then(res => res.json())
            .then(data => {
                searchResults.innerHTML = '';
                if (data.length === 0) {
                    searchResults.innerHTML = '<p style="padding:8px;">No books found</p>';
                    searchResults.style.display = 'block';
                    searchResults.classList.add('section');
                    return;
                }
                // Render as grid like featured books, with heading
                let html = '<div style="padding-bottom:10px;"><h2>Searched Books</h2></div>';
                html += '<div class="row" style="gap:16px; display:flex; flex-wrap:wrap; justify-content:center;">';
                data.forEach(book => {
                    html += `
                        <a href="book_details.php?id=${book.id}" class="book-link" style="text-decoration:none;">
                            <article class="book">
                                <div class="cover" style="background-image: url('${book.cover_path}'); background-size:cover; background-position:center;"></div>
                                <div class="title">${book.bookName}</div>
                                <div class="author">${book.author}</div>
                            </article>
                        </a>
                    `;
                });
                html += '</div>';
                searchResults.innerHTML = html;
                searchResults.style.display = 'block';
                searchResults.classList.add('section');
            })
            .catch(err => console.error('Search error:', err));
    });

</script>
</body>
</html>