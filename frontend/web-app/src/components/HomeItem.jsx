import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { addItemToCart, removeItemFromCart } from "../store/cartSlice";
import { ImBoxAdd } from "react-icons/im";
import { FcDeleteDatabase } from "react-icons/fc";

const IMAGE_BASE_URL = "https://pub-2026e42b61ba4a2b8f10a677f80ae76d.r2.dev/";

const HomeItem = ({ item }) => {
  const dispatch = useDispatch();

  // Server-backed cart state
  const cartItems = useSelector((store) => store.cart.items);
  const status = useSelector((store) => store.cart.status);
  const isLoading = status === "loading";

  // Check if item already in bag
  const inBag =
    Array.isArray(cartItems) &&
    cartItems.some((ci) => ci.id === item.id);

  const handleAddToBag = async () => {
    await dispatch(addItemToCart(item.id));
  };

  const handleRemoveFromBag = async () => {
    await dispatch(removeItemFromCart(item.id));
  };

  return (
    <div className="item-container">
      <img className="item-image" src={`${IMAGE_BASE_URL}${item.image}`} alt="item" />

      <div className="rating">
        {(item?.rating?.stars ?? 0)} ⭐ |{" "}
        {(item?.rating?.count ?? 0)}
      </div>

      <div className="company-name">{item.company}</div>
      <div className="item-name">{item.item_name}</div>

      <div className="price">
        <span className="current-price">
          Rs {item.current_price}
        </span>
        <span className="original-price">
          Rs {item.original_price}
        </span>
        <span className="discount">
          ({item.discount_percentage}% OFF)
        </span>
      </div>

      {inBag ? (
        <button
          type="button"
          className="btn btn-danger btn-add-bag"
          onClick={handleRemoveFromBag}
          disabled={isLoading}
          title="Remove from bag"
        >
          <FcDeleteDatabase />{" "}
          {isLoading ? "Removing..." : "Remove from Bag"}
        </button>
      ) : (
        <button
          type="button"
          className="btn btn-success btn-add-bag"
          onClick={handleAddToBag}
          disabled={isLoading}
          title="Add to bag"
        >
          <ImBoxAdd />{" "}
          {isLoading ? "Adding..." : "Add to Bag"}
        </button>
      )}
    </div>
  );
};

export default HomeItem;
