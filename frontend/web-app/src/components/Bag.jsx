import React, { useEffect } from "react";
import BagSummary from "./BagSummary";
import BagItemContainer from "./BagItemContainer";
import { useSelector, useDispatch } from "react-redux";
import { fetchCartDetails } from "../store/cartSlice";
import { useNavigate } from "react-router-dom";

const Bag = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const bagItems = useSelector((store) => store.bag);
  const { items: finalItems, status, error } = useSelector(
    (store) => store.cart
  );

  useEffect(() => {
    dispatch(fetchCartDetails());
  }, [dispatch, bagItems]);

  if (status === "loading") {
    return (
      <main>
        <div className="bag-page">Loading your cart...</div>
      </main>
    );
  }

  if (status === "failed") {
    return (
      <main>
        <div className="bag-page">Error: {error}</div>
      </main>
    );
  }

  if (status === "succeeded" && finalItems.length === 0) {
    return (
      <main>
        <div style={styles.emptyWrapper}>
          <div style={styles.emptyCard}>
            <div style={styles.bagIcon}>🛍️</div>
            <h2 style={styles.emptyTitle}>Your bag is empty</h2>
            <p style={styles.emptySubtitle}>
              Looks like you haven't added anything yet.
              <br />
              Explore our collection and find something you love!
            </p>
            <button
              style={styles.shopBtn}
              onMouseEnter={e => e.target.style.background = "#c0175d"}
              onMouseLeave={e => e.target.style.background = "#ff3f6c"}
              onClick={() => navigate("/")}
            >
              START SHOPPING
            </button>
          </div>
        </div>
      </main>
    );
  }

  return (
    <main>
      <div className="bag-page">
        {finalItems.map((item) => (
          <BagItemContainer key={item.id} item={item} />
        ))}

        <BagSummary
          summary={finalItems}
          onOrderSuccess={() => dispatch(fetchCartDetails())}
        />
      </div>
    </main>
  );
};

const styles = {
  emptyWrapper: {
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    minHeight: "70vh",
    backgroundColor: "#f5f5f5",
  },
  emptyCard: {
    backgroundColor: "#fff",
    borderRadius: "16px",
    padding: "60px 48px",
    textAlign: "center",
    boxShadow: "0 4px 24px rgba(0,0,0,0.08)",
    maxWidth: "420px",
    width: "100%",
  },
  bagIcon: {
    fontSize: "72px",
    marginBottom: "16px",
  },
  emptyTitle: {
    fontSize: "22px",
    fontWeight: "700",
    color: "#282c3f",
    marginBottom: "12px",
  },
  emptySubtitle: {
    fontSize: "14px",
    color: "#94969f",
    lineHeight: "1.6",
    marginBottom: "32px",
  },
  shopBtn: {
    backgroundColor: "#ff3f6c",
    color: "#fff",
    border: "none",
    borderRadius: "8px",
    padding: "14px 40px",
    fontSize: "14px",
    fontWeight: "700",
    cursor: "pointer",
    letterSpacing: "1px",
    transition: "background 0.2s",
  },
};

export default Bag;