import React from "react";
import { Link, useNavigate } from "react-router-dom";

const IMAGE_BASE_URL =
  "https://pub-2026e42b61ba4a2b8f10a677f80ae76d.r2.dev/";

const LandingPage = () => {
  const navigate = useNavigate();

  return (
    <main className="landing-page">
      {/* Hero */}
      <section className="landing-hero">
        <div className="landing-hero-content">
          <p className="landing-eyebrow">✨ New Arrivals</p>
          <h1 className="landing-title">
            Great deals await you today.
          </h1>
          <p className="landing-subtitle">
            Discover the latest collections — from fashion to electronics.
            Limited-time offers with up to 50% off.
          </p>

          <div className="landing-cta-row">
            <button
              type="button"
              className="landing-cta landing-cta-primary"
              onClick={() => navigate("/shop")}
            >
              Shop Women
            </button>
            <button
              type="button"
              className="landing-cta landing-cta-secondary"
              onClick={() => navigate("/shop")}
            >
              Shop Men
            </button>
          </div>
        </div>

        <div className="landing-hero-image">
          <img
            src={`${IMAGE_BASE_URL}landing-hero.png`}
            alt="Lifestyle"
            className="landing-hero-img"
            onError={(e) => {
              e.target.style.display = "none";
            }}
          />
        </div>
      </section>

      {/* Offer strip */}
      <section className="landing-offers">
        <div className="landing-offer-card">
          <span className="landing-offer-emoji">🚚</span>
          <div>
            <strong>Free Delivery</strong>
            <p>On orders above ₹999</p>
          </div>
        </div>
        <div className="landing-offer-card">
          <span className="landing-offer-emoji">↩️</span>
          <div>
            <strong>Easy Returns</strong>
            <p>30-day return window</p>
          </div>
        </div>
        <div className="landing-offer-card">
          <span className="landing-offer-emoji">💳</span>
          <div>
            <strong>Secure Payment</strong>
            <p>UPI, cards & EMI</p>
          </div>
        </div>
      </section>

      {/* Featured categories */}
      <section className="landing-categories">
        <h2 className="landing-section-title">Shop by category</h2>
        <div className="landing-cat-grid">
          <Link to="/shop" className="landing-cat-card">
            <span className="landing-cat-emoji">👗</span>
            <span>Women</span>
          </Link>
          <Link to="/shop" className="landing-cat-card">
            <span className="landing-cat-emoji">👕</span>
            <span>Men</span>
          </Link>
          <Link to="/shop" className="landing-cat-card">
            <span className="landing-cat-emoji">🎒</span>
            <span>Kids</span>
          </Link>
          <Link to="/shop" className="landing-cat-card">
            <span className="landing-cat-emoji">🏠</span>
            <span>Home &amp; Living</span>
          </Link>
        </div>
      </section>

      <div className="landing-enter">
        <button
          type="button"
          className="landing-enter-btn"
          onClick={() => navigate("/shop")}
        >
          Enter the store →
        </button>
      </div>
    </main>
  );
};

export default LandingPage;