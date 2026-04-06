import { useState, useEffect } from "react";

function WelcomeBanner() {
  const [welcome, setWelcome] = useState("");
  const [banner, setBanner] = useState("");

  useEffect(() => {
    fetch("http://localhost/catalog/welcome")
      .then(res => res.json())
      .then(data => setWelcome(data.message))
      .catch(() => setWelcome("Welcome! Great deals await you today. 🛍️"));

    fetch("http://localhost/catalog/offer-banner")
      .then(res => res.json())
      .then(data => setBanner(data.banner))
      .catch(() => setBanner("Flash Sale — Up to 50% off today! ⚡"));
  }, []);

  return (
    <div>
      {banner && (
        <div style={{
          background: "#16a34a",
          color: "white",
          textAlign: "center",
          padding: "8px",
          fontSize: "14px",
          fontWeight: "500",
          letterSpacing: "0.3px"
        }}>
          ⚡ {banner}
        </div>
      )}

      {welcome && (
        <div style={{
          textAlign: "center",
          padding: "14px",
          fontSize: "18px",
          fontWeight: "600",
          color: "#15803d"
        }}>
          {welcome}
        </div>
      )}
    </div>
  );
}

export default WelcomeBanner;