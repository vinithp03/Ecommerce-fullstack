import React, { useState } from "react";
import { IoPerson } from "react-icons/io5";
import { GoHeartFill } from "react-icons/go";
import { FaCartPlus } from "react-icons/fa";
import { IoIosSearch } from "react-icons/io";
import { TfiFaceSad } from "react-icons/tfi";
import { Link } from "react-router-dom";
import { useSelector } from "react-redux";
import WelcomeCard from "./profile";

const IMAGE_BASE_URL = "https://pub-2026e42b61ba4a2b8f10a677f80ae76d.r2.dev/";

const Header = () => {
  // Cart count from server-backed cart
  const cartItems = useSelector((store) => store.cart.items);
  const cartCount = Array.isArray(cartItems) ? cartItems.length : 0;

  // Items for search
  const items = useSelector((store) => store.items);

  const [searchQuery, setSearchQuery] = useState("");
  const [filteredItems, setFilteredItems] = useState([]);
  const [isHovered, setIsHovered] = useState(false);

  const handleSearch = (e) => {
    const query = e.target.value.toLowerCase();
    setSearchQuery(query);

    const results = items.filter((item) =>
      (item?.item_name ?? "").toLowerCase().includes(query)
    );

    setFilteredItems(results);
  };

  const handleMouseEnter = () => setIsHovered(true);
  const handleMouseLeave = () => setIsHovered(false);

  return (
    <header>
      <div className="logo_container">
        <Link to="/">
          <img
            className="myntra_home"
            src={`${IMAGE_BASE_URL}${"eComm-logo.png"}`}
            alt="Project LOGO"
          />
        </Link>
      </div>

      {/* Navigation */}
      <nav className="nav_bar">
        <Link to="/">All</Link>
        <Link to="/men">Men</Link>
        <Link to="/women">Women</Link>
        <Link to="/kids">Kids</Link>
        <Link to="/home&Living">Home & Living</Link>
        <Link to="/electronics">
          Electronic<sup>New</sup>
        </Link>
      </nav>

      {/* Search */}
      <div className="search_bar">
        <span className="material-symbols-outlined search_icon">
          <IoIosSearch size={27} />
        </span>

        <input
          className="search_input"
          placeholder="Search for products, brands and more"
          value={searchQuery}
          onChange={handleSearch}
        />

        {searchQuery && (
          <div className="search_results">
            {filteredItems.length > 0 ? (
              filteredItems.map((item) => (
                <div key={item.id} className="search_item">
                  <img
                    src={`${IMAGE_BASE_URL}${item.image}`}
                    alt={item.item_name}
                    width="50"
                    height="60"
                  />
                  <span>
                    <p>{item.item_name}</p>
                  </span>
                </div>
              ))
            ) : (
              <p>
                <TfiFaceSad /> No results found
              </p>
            )}
          </div>
        )}
      </div>

      {/* Action Bar */}
      <div className="action_bar">
        {/* Profile */}
        <div
          className="action_container"
          onMouseEnter={handleMouseEnter}
          onMouseLeave={handleMouseLeave}
          style={{ position: "relative" }}
        >
          <IoPerson />
          <span className="action_name">Profile</span>

          {isHovered && (
            <div style={styles.welcomeCardContainer}>
              <WelcomeCard />
            </div>
          )}
        </div>

        {/* Wishlist */}
        <div className="action_container">
          <GoHeartFill />
          <span className="action_name">Wishlist</span>
        </div>

        {/* Bag */}
        <Link className="action_container" to="/bag">
          <FaCartPlus />
          <span className="action_name">Bag</span>
          <span className="bag-item-count">{cartCount}</span>
        </Link>
      </div>
    </header>
  );
};

const styles = {
  welcomeCardContainer: {
    position: "absolute",
    top: "100%",
    left: "-10px",
    zIndex: 1000,
  },
};

export default Header;
