import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

const ORDER_API = "http://localhost:2003";
const USER_ID = 1;

const BagSummary = ({ summary, onOrderSuccess }) => {
  const navigate = useNavigate();
  const CONVENIENCE_FEES = 49;
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const totalItem = summary.length;

  let totalMRP = 0;
  let totalDiscount = 0;

  summary.forEach((bagItem) => {
    totalMRP += bagItem.original_price;
    totalDiscount += bagItem.original_price - bagItem.current_price;
  });

  const finalPayment = totalMRP - totalDiscount + CONVENIENCE_FEES;

  const handlePlaceOrder = async () => {
    setLoading(true);
    setError(null);

    try {
      const orderPayload = {
        items: summary.map((item) => ({
          productId: item.id,
          quantity: item.quantity,
        })),
      };

      const res = await fetch(
        `${ORDER_API}/order/v1/users/${USER_ID}/orders`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(orderPayload),
        }
      );

      if (!res.ok) {
        const text = await res.text().catch(() => "");
        throw new Error(`Order failed: ${res.status} ${text}`);
      }

      const data = await res.json();
      navigate("/payment", {
        state: {
          orderId: data.orderId,
          totalAmount: finalPayment,
          items: summary,
        },
      });
      if (onOrderSuccess) onOrderSuccess();

    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bag-summary">
      <div className="bag-details-container">
        <div className="price-header">
          PRICE DETAILS ({totalItem} Items)
        </div>

        <div className="price-item">
          <span className="price-item-tag">Total MRP</span>
          <span className="price-item-value">₹{totalMRP}</span>
        </div>

        <div className="price-item">
          <span className="price-item-tag">Discount on MRP</span>
          <span className="price-item-value priceDetail-base-discount">
            -₹{totalDiscount}
          </span>
        </div>

        <div className="price-item">
          <span className="price-item-tag">Convenience Fee</span>
          <span className="price-item-value">₹{CONVENIENCE_FEES}</span>
        </div>

        <hr />

        <div className="price-footer">
          <span className="price-item-tag">Total Amount</span>
          <span className="price-item-value">₹{finalPayment}</span>
        </div>
      </div>

      {error && (
        <div style={{ color: "red", marginBottom: "8px", fontSize: "14px" }}>
          {error}
        </div>
      )}

      <button
        className="btn-place-order"
        onClick={handlePlaceOrder}
        disabled={loading || summary.length === 0}
      >
        <div className="css-xjhrni">
          {loading ? "PLACING ORDER..." : "PLACE ORDER"}
        </div>
      </button>
    </div>
  );
};

export default BagSummary;