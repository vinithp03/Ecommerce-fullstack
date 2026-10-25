// src/components/LoginPopup.jsx
import React from "react";
import { Link } from "react-router-dom";

/**
 * Small modal shown when a guest tries to perform an action that
 * requires authentication (e.g. adding an item to the bag).
 */
const LoginPopup = ({ open, onClose }) => {
  if (!open) return null;

  return (
    <div className="login-popup-overlay" onClick={onClose}>
      <div className="login-popup-card" onClick={(e) => e.stopPropagation()}>
        <button className="login-popup-close" onClick={onClose} type="button">
          ✕
        </button>
        <div className="login-popup-icon">🛒</div>
        <h3>Please login to continue order</h3>
        <p>
          You need to be signed in to add items to your bag. Login or create
          an account to keep shopping.
        </p>
        <Link to="/login" className="login-popup-btn" onClick={onClose}>
          Login / Signup
        </Link>
      </div>
    </div>
  );
};

export default LoginPopup;