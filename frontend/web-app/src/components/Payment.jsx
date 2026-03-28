import React, { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import { fetchCartDetails } from "../store/cartSlice";

const Payment = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const { orderId, totalAmount, items } = location.state || {};

  const [paymentMethod, setPaymentMethod] = useState("card");
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  // Card fields
  const [cardNumber, setCardNumber] = useState("");
  const [expiry, setExpiry] = useState("");
  const [cvv, setCvv] = useState("");
  const [cardName, setCardName] = useState("");

  // UPI field
  const [upiId, setUpiId] = useState("");

  const handlePay = async () => {
    setLoading(true);
    // Simulate payment processing delay
    await new Promise((resolve) => setTimeout(resolve, 2000));
    setLoading(false);
    setSuccess(true);
    dispatch(fetchCartDetails());
  };

  if (success) {
    return (
      <main>
        <div style={styles.wrapper}>
          <div style={styles.successCard}>
            <div style={styles.successIcon}>✅</div>
            <h2 style={styles.successTitle}>Order Confirmed!</h2>
            <p style={styles.successSubtitle}>
              Your order <strong>#{orderId}</strong> has been placed successfully.
              <br />
              You will receive a confirmation shortly.
            </p>
            <div style={styles.summaryBox}>
              <p style={styles.summaryText}>Amount Paid: <strong>₹{totalAmount}</strong></p>
              <p style={styles.summaryText}>Items: <strong>{items?.length || 0}</strong></p>
            </div>
            <button
              style={styles.continueBtn}
              onMouseEnter={e => e.target.style.background = "#c0175d"}
              onMouseLeave={e => e.target.style.background = "#ff3f6c"}
              onClick={() => navigate("/")}
            >
              CONTINUE SHOPPING
            </button>
          </div>
        </div>
      </main>
    );
  }

  return (
    <main>
      <div style={styles.wrapper}>
        <div style={styles.container}>

          {/* Left — Payment Form */}
          <div style={styles.formCard}>
            <h2 style={styles.title}>Payment Details</h2>

            {/* Payment Method Toggle */}
            <div style={styles.tabRow}>
              <button
                style={paymentMethod === "card" ? styles.tabActive : styles.tab}
                onClick={() => setPaymentMethod("card")}
              >
                💳 Credit / Debit Card
              </button>
              <button
                style={paymentMethod === "upi" ? styles.tabActive : styles.tab}
                onClick={() => setPaymentMethod("upi")}
              >
                📱 UPI
              </button>
            </div>

            {/* Card Form */}
            {paymentMethod === "card" && (
              <div style={styles.form}>
                <input
                  style={styles.input}
                  placeholder="Card Number (16 digits)"
                  maxLength={16}
                  value={cardNumber}
                  onChange={e => setCardNumber(e.target.value.replace(/\D/g, ""))}
                />
                <input
                  style={styles.input}
                  placeholder="Name on Card"
                  value={cardName}
                  onChange={e => setCardName(e.target.value)}
                />
                <div style={styles.row}>
                  <input
                    style={{ ...styles.input, flex: 1 }}
                    placeholder="MM/YY"
                    maxLength={5}
                    value={expiry}
                    onChange={e => setExpiry(e.target.value)}
                  />
                  <input
                    style={{ ...styles.input, flex: 1 }}
                    placeholder="CVV"
                    maxLength={3}
                    type="password"
                    value={cvv}
                    onChange={e => setCvv(e.target.value.replace(/\D/g, ""))}
                  />
                </div>
              </div>
            )}

            {/* UPI Form */}
            {paymentMethod === "upi" && (
              <div style={styles.form}>
                <input
                  style={styles.input}
                  placeholder="Enter UPI ID (e.g. vinith@upi)"
                  value={upiId}
                  onChange={e => setUpiId(e.target.value)}
                />
                <p style={styles.upiHint}>
                  You will receive a payment request on your UPI app.
                </p>
              </div>
            )}

            <button
              style={{
                ...styles.payBtn,
                opacity: loading ? 0.7 : 1,
                cursor: loading ? "not-allowed" : "pointer"
              }}
              onMouseEnter={e => !loading && (e.target.style.background = "#c0175d")}
              onMouseLeave={e => !loading && (e.target.style.background = "#ff3f6c")}
              onClick={handlePay}
              disabled={loading}
            >
              {loading ? "PROCESSING..." : `PAY ₹${totalAmount}`}
            </button>

            <p style={styles.secureNote}>🔒 100% Secure Payments</p>
          </div>

          {/* Right — Order Summary */}
          <div style={styles.summaryCard}>
            <h3 style={styles.summaryTitle}>Order Summary</h3>
            <p style={styles.orderId}>Order ID: <strong>#{orderId}</strong></p>
            <hr />
            {items?.map((item, idx) => (
              <div key={idx} style={styles.itemRow}>
                <span style={styles.itemName}>{item.itemName}</span>
                <span style={styles.itemPrice}>₹{item.currentPrice}</span>
              </div>
            ))}
            <hr />
            <div style={styles.totalRow}>
              <span>Total Amount</span>
              <strong>₹{totalAmount}</strong>
            </div>
          </div>

        </div>
      </div>
    </main>
  );
};

const styles = {
  wrapper: { minHeight: "80vh", backgroundColor: "#f5f5f5", padding: "32px 16px" },
  container: { display: "flex", gap: "24px", maxWidth: "900px", margin: "0 auto", flexWrap: "wrap" },
  formCard: { flex: 1, minWidth: "300px", backgroundColor: "#fff", borderRadius: "12px", padding: "32px", boxShadow: "0 2px 16px rgba(0,0,0,0.08)" },
  summaryCard: { width: "280px", backgroundColor: "#fff", borderRadius: "12px", padding: "24px", boxShadow: "0 2px 16px rgba(0,0,0,0.08)", height: "fit-content" },
  title: { fontSize: "20px", fontWeight: "700", color: "#282c3f", marginBottom: "24px" },
  tabRow: { display: "flex", gap: "12px", marginBottom: "24px" },
  tab: { flex: 1, padding: "10px", border: "1px solid #d4d5d9", borderRadius: "8px", background: "#fff", cursor: "pointer", fontSize: "13px", color: "#282c3f" },
  tabActive: { flex: 1, padding: "10px", border: "2px solid #ff3f6c", borderRadius: "8px", background: "#fff0f3", cursor: "pointer", fontSize: "13px", color: "#ff3f6c", fontWeight: "700" },
  form: { display: "flex", flexDirection: "column", gap: "16px", marginBottom: "24px" },
  input: { padding: "12px 16px", border: "1px solid #d4d5d9", borderRadius: "8px", fontSize: "14px", outline: "none" },
  row: { display: "flex", gap: "12px" },
  payBtn: { width: "100%", padding: "14px", backgroundColor: "#ff3f6c", color: "#fff", border: "none", borderRadius: "8px", fontSize: "15px", fontWeight: "700", letterSpacing: "1px", transition: "background 0.2s" },
  secureNote: { textAlign: "center", color: "#94969f", fontSize: "12px", marginTop: "12px" },
  upiHint: { color: "#94969f", fontSize: "13px" },
  summaryTitle: { fontSize: "16px", fontWeight: "700", color: "#282c3f", marginBottom: "8px" },
  orderId: { fontSize: "13px", color: "#94969f", marginBottom: "12px" },
  itemRow: { display: "flex", justifyContent: "space-between", padding: "8px 0", fontSize: "14px" },
  itemName: { color: "#282c3f", flex: 1, marginRight: "8px" },
  itemPrice: { color: "#282c3f", fontWeight: "600" },
  totalRow: { display: "flex", justifyContent: "space-between", padding: "12px 0", fontSize: "15px" },
  successCard: { maxWidth: "460px", margin: "60px auto", backgroundColor: "#fff", borderRadius: "16px", padding: "48px", textAlign: "center", boxShadow: "0 4px 24px rgba(0,0,0,0.08)" },
  successIcon: { fontSize: "64px", marginBottom: "16px" },
  successTitle: { fontSize: "24px", fontWeight: "700", color: "#282c3f", marginBottom: "12px" },
  successSubtitle: { fontSize: "14px", color: "#94969f", lineHeight: "1.6", marginBottom: "24px" },
  summaryBox: { backgroundColor: "#f5f5f5", borderRadius: "8px", padding: "16px", marginBottom: "24px" },
  summaryText: { fontSize: "14px", color: "#282c3f", margin: "4px 0" },
  continueBtn: { backgroundColor: "#ff3f6c", color: "#fff", border: "none", borderRadius: "8px", padding: "14px 40px", fontSize: "14px", fontWeight: "700", cursor: "pointer", letterSpacing: "1px", transition: "background 0.2s" },
};

export default Payment;