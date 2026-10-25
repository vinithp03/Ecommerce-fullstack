import React, { useState } from "react";
import { IoPerson } from "react-icons/io5";
import { GoHeartFill } from "react-icons/go";
import { FaCartPlus } from "react-icons/fa";
import { IoIosSearch } from "react-icons/io";
import { TfiFaceSad } from "react-icons/tfi";
import { HiMenu, HiX } from "react-icons/hi";
import { Link, useNavigate } from "react-router-dom";
import { useSelector, useDispatch } from "react-redux";
import WelcomeCard from "./profile";
import { themeAction } from "../store/themeSlice";

const IMAGE_BASE_URL =
  "https://pub-2026e42b61ba4a2b8f10a677f80ae76d.r2.dev/";

const Header = () => {
  // Cart count
  const cartItems = useSelector((store) => store.cart.items);
  const cartCount = Array.isArray(cartItems) ? cartItems.length : 0;

  // Auth
  const authUser = useSelector((store) => store.auth.user);

  // Theme
  const themeMode = useSelector((store) => store.theme.mode);
  const dispatch = useDispatch();

  const toggleTheme = () => {
    dispatch(themeAction.toggleTheme());
  };

  const openSettings = () => {
    setSettingsOpen(true);
  };

  const closeSettings = () => {
    setSettingsOpen(false);
  };

  // Items for search
  const items = useSelector((store) => store.items.items);

  const [searchQuery, setSearchQuery] = useState("");
  const [filteredItems, setFilteredItems] = useState([]);

  const [isHovered, setIsHovered] = useState(false);

  // Mobile menu
  const [menuOpen, setMenuOpen] = useState(false);

  // Mobile search
  const [mobileSearchOpen, setMobileSearchOpen] = useState(false);

  // Settings panel
  const [settingsOpen, setSettingsOpen] = useState(false);

  const navigate = useNavigate();

  const handleSearch = (e) => {
    const query = e.target.value.toLowerCase();

    setSearchQuery(query);

    const results = items.filter((item) =>
      (item?.item_name ?? "").toLowerCase().includes(query)
    );

    setFilteredItems(results);
  };

  const closeMobileMenu = () => {
    setMenuOpen(false);
  };

  const handleProfileClick = () => {
    if (authUser) {
      navigate("/profile");
    } else {
      navigate("/login");
    }
  };

  return (
    <>
      <header className="main-header">

        {/* ================= LOGO ================= */}
        <div className="logo_container">
          <Link to="/" onClick={closeMobileMenu}>
            <img
              className="myntra_home"
              src={`${IMAGE_BASE_URL}eComm-logo.png`}
              alt="Project LOGO"
            />
          </Link>
        </div>

        {/* ================= DESKTOP NAV ================= */}
        <nav className="nav_bar desktop_nav">

          <Link to="/">All</Link>

          <Link to="/men">
            Men
          </Link>

          <Link to="/women">
            Women
          </Link>

          <Link to="/kids">
            Kids
          </Link>

          <Link to="/home&Living">
            Home & Living
          </Link>

          <Link to="/electronics">
            Electronics
            <sup>New</sup>
          </Link>

        </nav>


        {/* ================= SEARCH ================= */}
        <div
          className={`search_bar ${
            mobileSearchOpen ? "mobile_search_active" : ""
          }`}
        >

          <span className="search_icon">
            <IoIosSearch size={25} />
          </span>

          <input
            className="search_input"
            placeholder="Search for products, brands and more"
            value={searchQuery}
            onChange={handleSearch}
          />

          {/* Search results */}
          {searchQuery && (
            <div className="search_results">

              {filteredItems.length > 0 ? (

                filteredItems.map((item) => (

                  <div
                    key={item.id}
                    className="search_item"
                  >

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

                <p className="no_results">
                  <TfiFaceSad />
                  No results found
                </p>

              )}

            </div>
          )}

        </div>


        {/* ================= ACTION BAR ================= */}
        <div className="action_bar">

          {/* Search button - mobile only */}
          <button
            className="mobile_search_button"
            onClick={() =>
              setMobileSearchOpen((previous) => !previous)
            }
          >
            <IoIosSearch />
          </button>


          {/* Profile / Settings */}
          <div
            className="action_container profile_action"
            onMouseEnter={() => setIsHovered(true)}
            onMouseLeave={() => setIsHovered(false)}
          >

            <IoPerson />

            <span className="action_name">
              Profile
            </span>

            {isHovered && (
              <div className="welcome_card_container">
                <WelcomeCard />
              </div>
            )}
          </div>

          {/* Settings */}
          <button
            className="action_container"
            onClick={openSettings}
            title="Settings"
          >
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"></path>
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
            </svg>
          </button>

          {/* Wishlist */}
          <div className="action_container">

            <GoHeartFill />

            <span className="action_name">
              Wishlist
            </span>

          </div>


          {/* Bag */}
          <Link
            className="action_container bag_action"
            to="/bag"
          >

            <FaCartPlus />

            <span className="action_name">
              Bag
            </span>

            {cartCount > 0 && (
              <span className="bag-item-count">
                {cartCount}
              </span>
            )}

          </Link>


          {/* Hamburger */}
          <button
            className="hamburger_button"
            onClick={() => setMenuOpen((previous) => !previous)}
          >

            {menuOpen ? (
              <HiX />
            ) : (
              <HiMenu />
            )}

          </button>

        </div>


        {/* ================= MOBILE NAV ================= */}
        <nav
          className={`mobile_nav ${
            menuOpen ? "mobile_nav_open" : ""
          }`}
        >

          <Link
            to="/"
            onClick={closeMobileMenu}
          >
            All
          </Link>

          <Link
            to="/men"
            onClick={closeMobileMenu}
          >
            Men
          </Link>

          <Link
            to="/women"
            onClick={closeMobileMenu}
          >
            Women
          </Link>

          <Link
            to="/kids"
            onClick={closeMobileMenu}
          >
            Kids
          </Link>

          <Link
            to="/home&Living"
            onClick={closeMobileMenu}
          >
            Home & Living
          </Link>

          <Link
            to="/electronics"
            onClick={closeMobileMenu}
          >
            Electronics
            <sup>New</sup>
          </Link>

        </nav>

      </header>

      {/* ================= SETTINGS PANEL ================= */}
      <div
        className={`settings-overlay ${settingsOpen ? "open" : ""}`}
        onClick={closeSettings}
      />

      <div
        className={`settings-panel ${settingsOpen ? "open" : ""}`}
      >
        <div className="settings-header">
          <h3>Settings</h3>
          <button className="settings-close" onClick={closeSettings}>
            ✕
          </button>
        </div>
        <div className="settings-content">
          <div className="settings-section">
            <h4>Theme</h4>
            <div className="theme-option">
              <span className="theme-option-label">Dark mode</span>
              <input
                type="checkbox"
                className="theme-option-input"
                checked={themeMode === "dark"}
                onChange={toggleTheme}
              />
            </div>
          </div>
          <div className="settings-section">
            <h4>Account</h4>
            <button
              onClick={() => {
                closeSettings();
                navigate(authUser ? "/profile" : "/login");
              }}
              style={{
                width: "100%",
                padding: "10px",
                backgroundColor: themeMode === "dark" ? "#2d323a" : "#f5f5f6",
                border: "1px solid var(--border)",
                color: "var(--fg)",
                borderRadius: "6px",
                cursor: "pointer",
                fontWeight: "600",
              }}
            >
              {authUser ? "My Profile" : "Login / Signup"}
            </button>
          </div>
        </div>
      </div>
    </>
  );
};

export default Header;