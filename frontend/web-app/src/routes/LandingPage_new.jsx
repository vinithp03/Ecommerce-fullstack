import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";

const IMAGE_BASE_URL =
  "https://pub-2026e42b61ba4a2b8f10a677f80ae76d.r2.dev/";

// Infinite carousel: frame 1 → frame 2 → frame 1 ... every 5 seconds
const SPOTLIGHT_DESKTOP_1 = `${IMAGE_BASE_URL}Hp_spotlight.png`;
const SPOTLIGHT_DESKTOP_2 = `${IMAGE_BASE_URL}HP_desk_spotlight_frame2.png`;
const SPOTLIGHT_MOBILE_1 = `${IMAGE_BASE_URL}Hp_MOBspotlight.png`;
const SPOTLIGHT_MOBILE_2 = `${IMAGE_BASE_URL}HP_mob_spotlight_frame2.png`;

const CAROUSEL_INTERVAL = 5000; // 5 seconds total

const LandingPage = () => {
  const navigate = useNavigate();
  const [frame, setFrame] = useState(0); // 0 or 1

  useEffect(() => {
    const id = setInterval(() => {
      setFrame((previous) => (previous === 0 ? 1 : 0));
    }, CAROUSEL_INTERVAL);

    return () => clearInterval(id);
  }, []);

  return (
    <main className="landing-page">
      <section className="landing-spotlight" onClick={() => navigate("/shop")}>
        <picture>
          <source
            media="(max-width: 768px)"
            srcSet={frame === 0 ? SPOTLIGHT_MOBILE_1 : SPOTLIGHT_MOBILE_2}
          />
          <img
            src={frame === 0 ? SPOTLIGHT_DESKTOP_1 : SPOTLIGHT_DESKTOP_2}
            alt="Spotlight"
            className="landing-spotlight-img"
            onError={(e) => {
              e.target.style.display = "none";
            }}
          />
        </picture>
      </section>

      {/* Hero section */}
      <section className="landing-hero">
        <div className="landing-hero-content">
          <p className="landing-eyebrow">✨ New Arrivals</p>
          <h1 className="landing-title">Great deals await you today.</h1>
          <p className="landing-subtitle">Discover the latest collections — from fashion to electronics. Limited-time offers with up to 50% off.</p>

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