import React, { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import { fetchCartDetails } from "../store/cartSlice";

const PAYMENT_API = (import.meta.env.VITE_API_BASE_URL?.trim()) || "";;


const Payment = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const { orderId, totalAmount, items, paymentIdempotencyKey} = location.state || {};

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
    console.log("Paying for orderId:", orderId);
    setLoading(true);
    try {
      const res = await fetch(`${PAYMENT_API}/payment/v1/orders/${orderId}/pay`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Idempotency-Key": paymentIdempotencyKey,
        },
        body: JSON.stringify({ paymentMethod: "MOCK" }),
      });

      if (!res.ok) throw new Error("Payment failed");

      const data = await res.json();
      if (data.status === "SUCCESS") {
        setSuccess(true);
        dispatch(fetchCartDetails());
      } else {
        alert("Payment declined. Please try again.");
      }
    } catch (err) {
      alert("Payment error: " + err.message);
    } finally {
      setLoading(false);
    }
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
  wrapper: { minHeight: "80vh", backgroundColor: "var(--surface)", padding: "32px 16px" },
  container: { display: "flex", gap: "24px", maxWidth: "900px", margin: "0 auto", flexWrap: "wrap" },
  formCard: { flex: 1, minWidth: "300px", backgroundColor: "var(--card)", borderRadius: "12px", padding: "32px", boxShadow: "var(--card-shadow)" },
  summaryCard: { width: "280px", backgroundColor: "var(--card)", borderRadius: "12px", padding: "24px", boxShadow: "var(--card-shadow)", height: "fit-content" },
  title: { fontSize: "20px", fontWeight: "700", color: "var(--fg)", marginBottom: "24px" },
  tabRow: { display: "flex", gap: "12px", marginBottom: "24px" },
  tab: { flex: 1, padding: "10px", border: "1px solid var(--border)", borderRadius: "8px", background: "var(--card)", cursor: "pointer", fontSize: "13px", color: "var(--fg)" },
  tabActive: { flex: 1, padding: "10px", border: "2px solid var(--primary)", borderRadius: "8px", background: "var(--surface)", cursor: "pointer", fontSize: "13px", color: "var(--primary)", fontWeight: "700" },
  form: { display: "flex", flexDirection: "column", gap: "16px", marginBottom: "24px" },
  input: { padding: "12px 16px", border: "1px solid var(--border)", borderRadius: "8px", fontSize: "14px", outline: "none", backgroundColor: "var(--bg)", color: "var(--fg)" },
  row: { display: "flex", gap: "12px" },
  payBtn: { width: "100%", padding: "14px", backgroundColor: "var(--primary)", color: "var(--text-on-primary)", border: "none", borderRadius: "8px", fontSize: "15px", fontWeight: "700", letterSpacing: "1px", transition: "background 0.2s" },
  secureNote: { textAlign: "center", color: "var(--muted)", fontSize: "12px", marginTop: "12px" },
  upiHint: { color: "var(--muted)", fontSize: "13px" },
  summaryTitle: { fontSize: "16px", fontWeight: "700", color: "var(--fg)", marginBottom: "8px" },
  orderId: { fontSize: "13px", color: "var(--muted)", marginBottom: "12px" },
  itemRow: { display: "flex", justifyContent: "space-between", padding: "8px 0", fontSize: "14px" },
  itemName: { color: "var(--fg)", flex: 1, marginRight: "8px" },
  itemPrice: { color: "var(--fg)", fontWeight: "600" },
  totalRow: { display: "flex", justifyContent: "space-between", padding: "12px 0", fontSize: "15px" },
  successCard: { maxWidth: "460px", margin: "60px auto", backgroundColor: "var(--card)", borderRadius: "16px", padding: "48px", textAlign: "center", boxShadow: "var(--card-shadow)" },
  successIcon: { fontSize: "64px", marginBottom: "16px" },
  successTitle: { fontSize: "24px", fontWeight: "700", color: "var(--fg)", marginBottom: "12px" },
  successSubtitle: { fontSize: "14px", color: "var(--muted)", lineHeight: "1.6", marginBottom: "24px" },
  summaryBox: { backgroundColor: "var(--surface)", borderRadius: "8px", padding: "16px", marginBottom: "24px" },
  summaryText: { fontSize: "14px", color: "var(--fg)", margin: "4px 0" },
  continueBtn: { backgroundColor: "var(--primary)", color: "var(--text-on-primary)", border: "none", borderRadius: "8px", padding: "14px 40px", fontSize: "14px", fontWeight: "700", cursor: "pointer", letterSpacing: "1px", transition: "background 0.2s" },
};

export default Payment;