<?php
require 'db.php';

// Default values
$book_id = null;
$book_title = 'Book not found';
$book_author = 'N/A';

if (isset($_GET['id']) && is_numeric($_GET['id'])) {
    $book_id = (int)$_GET['id'];
    $sql = "SELECT * FROM pdfs WHERE id = ?";
    $stmt = $conn->prepare($sql);
    if ($stmt) {
        $stmt->bind_param("i", $book_id);
        $stmt->execute();
        $result = $stmt->get_result();
        if ($result->num_rows > 0) {
            $book = $result->fetch_assoc();
            $book_title = htmlspecialchars(pathinfo($book['finename'], PATHINFO_FILENAME));
            // Using a placeholder for author name as it's not in the DB yet
            $book_author = htmlspecialchars(pathinfo($book['Author'], PATHINFO_FILENAME));
        }
        $stmt->close();
    }
}
$conn->close();
?>
<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Payment - <?php echo $book_title; ?></title>
  <link rel="stylesheet" href="home.css"> <!-- Linking home.css for header/footer styles -->
  <style>
    
  body {
  font-family: 'Inter', sans-serif;
  background-color: #f0f2f5;
}

.payment-container {
  width: 100%;
  background-color: #fff;
  padding: 20px 0; /* Adjusted padding */
  min-height: calc(100vh - 150px); /* Ensure it takes up vertical space */
}

.payment-header {
  display: flex;
  justify-content: center;
  position: relative;
  margin-bottom: 30px;
  max-width: 900px; /* Center the header content */
  margin-left: auto;
  margin-right: auto;
  padding: 0 20px; /* Add side padding */
}


.main-content {
  display: flex;
  gap: 40px;
  flex-wrap: wrap;
  max-width: 900px; /* Center the main content */
  margin: 0 auto; /* Center the main content */
  padding: 0 20px; /* Add side padding */
}

.book-details {
  flex: 1;
  text-align: center;
  padding: 20px;
  background-color: #fafafa;
  border-radius: 15px;
  border: 1px solid #eee;
  min-width: 300px;
}

.book-image {
  width: 180px;
  height: 270px;
  background: #ADDBE6; /* Placeholder background */
  border-radius: 10px;
  margin: 0 auto 15px auto;
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
}

.book-title {
  font-size: 1.5rem;
  font-weight: 600;
  margin: 0 0 5px;
  color: #333;
}

.book-author {
  font-size: 1.1rem;
  color: #777;
  margin: 0;
}

.payment-form {
  flex: 2;
  display: flex;
  flex-direction: column;
  gap: 25px;
  min-width: 400px;
}

.section-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 15px;
}

.rental-options {
  display: flex;
  gap: 15px;
}

.rental-button {
  flex: 1;
  padding: 15px;
  background-color: #fff;
  border: 1px solid #ccc;
  border-radius: 12px;
  text-align: center;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color .3s, border-color .3s, transform .1s;
}

.rental-button.selected {
  background-color: #e0f2f1;
  border-color: #4CAF50;
  font-weight: 600;
}

.rental-button:hover {
  border-color: #888;
}

.payment-method input[type="radio"] {
  position: absolute;
  left: -9999px;
}

.payment-method .radio-option {
  display: inline-block;
  padding: 8px 12px;
  margin: 0 12px 10px 0;
  border: 1px solid #ccc;
  border-radius: 12px;
  background: #fff;
  cursor: pointer;
  font-weight: 500;
  color: #555;
  user-select: none;
  transition: background-color .2s, border-color .2s;
}

#pm-card:checked+label.radio-option,
#pm-upi:checked+label.radio-option {
  background-color: #e0f2f1;
  border-color: #4CAF50;
  font-weight: 600;
  color: #333;
}

.method-panels {
  margin-top: 10px;
}

.panel {
  display: none;
}
.card-details {
  display: none;
  flex-direction: column;
  gap: 15px;
}

#pm-card:checked~.method-panels .card-details,
#pm-upi:checked~.method-panels .upi-details {
  display: flex;
}

.input-field {
  width: calc(100% - 24px);
  padding: 15px 12px;
  border: 1px solid #ccc;
  border-radius: 12px;
  font-size: 1rem;
  color: #555;
  transition: border-color .3s;
}

.input-field:focus {
  outline: none;
  border-color: #4CAF50;
}

.borrow-button {
  width: 100%;
  padding: 18px;
  background-color: #4CAF50;
  color: #fff;
  border: none;
  border-radius: 15px;
  font-size: 1.2rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color .3s, transform .1s;
}

.borrow-button:hover {
  background-color: #45a049;
}

.borrow-button:active {
  transform: scale(0.99);
}

@media (max-width: 768px) {
  .main-content {
    flex-direction: column;
  }

  .book-details,
  .payment-form {
    min-width: auto;
  }
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
        </nav>
    </header>

    <div class="payment-container">
        <div class="payment-header">
            <h1>Payment</h1>
        </div>

        <div class="main-content">
            <div class="book-details">
                <div class="book-image" style="background-image: url('<?php echo htmlspecialchars($book['cover_path']); ?>');background-size:cover;background-position:center; "></div>
                <h2 class="book-title"><?php echo $book_title; ?></h2>
                <p class="book-author"><?php echo $book_author; ?></p>
            </div>

            <form class="payment-form" id="paymentForm" onsubmit="return false;">
                <div>
                    <div class="section-title">Rental Duration</div>
                    <div class="rental-options">
                        <div class="rental-button selected" onclick="selectRental(this)" data-duration="7">7 DAYS</div>
                        <div class="rental-button" onclick="selectRental(this)" data-duration="14">14 DAYS</div>
                        <div class="rental-button" onclick="selectRental(this)" data-duration="30">30 DAYS</div>
                    </div>
                </div>

                <div>
                    <div class="section-title">Payment Method</div>
                    <div class="payment-method">
                        <input type="radio" id="pm-card" name="payment-method" value="card" checked>
                        <label class="radio-option" for="pm-card">Card</label>

                        <input type="radio" id="pm-upi" name="payment-method" value="upi">
                        <label class="radio-option" for="pm-upi">UPI ID</label>

                        <div class="method-panels">
                            <div class="card-details panel">
                                <input type="text" id="cardName" class="input-field" placeholder="Card Holder Name">
                                <input type="text" id="cardNumber" class="input-field" placeholder="Card Number (16 digits)">
                                <input type="text" id="cardCvv" class="input-field" placeholder="CVV (3-4 digits)">
                            </div>
                            <div class="upi-details panel">
                                <input type="text" id="upiId" class="input-field" placeholder="UPI ID (e.g., name@bank)">
                            </div>
                        </div>
                    </div>
                </div>

                <!--extra-->
                <button 
    type="button" 
    class="borrow-button"
    onclick="borrowBook('<?php echo htmlspecialchars($book['filepath']); ?>')">
    BORROW
    
</button>


                
            </form>
        </div>
    </div>

    <footer style="position:relative; bottom: 0; width: 100%;">
        <div class="footbar">
            <span>© 2025 WordWave</span>
            <span>Privacy · Terms · Help</span>
        </div>
    </footer>
</div>

<script>
function selectRental(button) {
    // Remove selected class from all buttons
    document.querySelectorAll('.rental-button').forEach(btn => {
        btn.classList.remove('selected');
    });
    // Add selected class to clicked button
    button.classList.add('selected');
}

function borrowBook(pdfPath) {
    const paymentMethod = document.querySelector('input[name="payment-method"]:checked').value;
    const bookId = <?php echo $book_id; ?>;
    const selectedDuration = document.querySelector('.rental-button.selected').innerText.split(' ')[0];
    let isValid = true;

    if (paymentMethod === 'card') {
        const cardName = document.getElementById('cardName').value.trim();
        const cardNumber = document.getElementById('cardNumber').value.trim();
        const cardCvv = document.getElementById('cardCvv').value.trim();

        if (cardName === '') {
            alert('Please enter the card holder name.');
            isValid = false;
        } else if (!/^\d{16}$/.test(cardNumber)) {
            alert('Please enter a valid 16-digit card number.');
            isValid = false;
        } else if (!/^\d{3,4}$/.test(cardCvv)) {
            alert('Please enter a valid 3 or 4-digit CVV.');
            isValid = false;
        }
    } else if (paymentMethod === 'upi') {
        const upiId = document.getElementById('upiId').value.trim();
        if (!/^[a-zA-Z0-9.\-_]{2,256}@[a-zA-Z]{2,64}$/.test(upiId)) {
            alert('Please enter a valid UPI ID (e.g., name@bank).');
            isValid = false;
        }
    }

    if (isValid) {
        // Save purchase to database
        fetch('save_purchase.php', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                book_id: bookId,
                rental_duration: parseInt(selectedDuration)
            })
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert('Payment successful! Opening your book...');
                if (pdfPath) {
                    window.open(pdfPath, '_self');
                } else {
                    alert("PDF not found for this book.");
                }
            } else {
                alert('Error processing purchase. Please try again.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error processing purchase. Please try again.');
        });
    }
}
</script>
</body>
</html>