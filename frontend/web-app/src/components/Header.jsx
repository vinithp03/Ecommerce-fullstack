import React, { useState } from "react";
import { IoPerson } from "react-icons/io5";
import { GoHeartFill } from "react-icons/go";
import { FaCartPlus } from "react-icons/fa";
import { IoIosSearch } from "react-icons/io";
import { TfiFaceSad } from "react-icons/tfi";
import { HiMenu, HiX } from "react-icons/hi";
import { Link } from "react-router-dom";
import { useSelector } from "react-redux";
import WelcomeCard from "./profile";

const IMAGE_BASE_URL =
  "https://pub-2026e42b61ba4a2b8f10a677f80ae76d.r2.dev/";

const Header = () => {
  // Cart count
  const cartItems = useSelector((store) => store.cart.items);
  const cartCount = Array.isArray(cartItems) ? cartItems.length : 0;

  // Items for search
  const items = useSelector((store) => store.items);

  const [searchQuery, setSearchQuery] = useState("");
  const [filteredItems, setFilteredItems] = useState([]);

  const [isHovered, setIsHovered] = useState(false);

  // Mobile menu
  const [menuOpen, setMenuOpen] = useState(false);

  // Mobile search
  const [mobileSearchOpen, setMobileSearchOpen] = useState(false);

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

  return (
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


        {/* Profile */}

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
  );
};

export default Header;