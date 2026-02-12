import React from "react";
import { useDispatch } from "react-redux";
import { MdDeleteForever } from "react-icons/md";
import { removeItemFromCart } from "../store/cartSlice";

const IMAGE_BASE_URL = "https://pub-2026e42b61ba4a2b8f10a677f80ae76d.r2.dev/";

const BagItemContainer = ({ item }) => {
  const dispatch = useDispatch();

  const handleRemove = () => {
    // Call backend DELETE via thunk
    dispatch(removeItemFromCart(item.id));
    console.log(item.id);
    console.log(item.catalog_product_id);
  };

  return (
    <div className="bag-items-container">
      <div className="bag-item-container">
        <div className="item-left-part">
          <img
            className="bag-item-img"
            src={`${IMAGE_BASE_URL}${item.image}`}
            alt={item.item_name}
          />
        </div>

        <div className="item-right-part">
          <div className="company">{item.company}</div>
          <div className="item-name">{item.item_name}</div>

          <div className="price-container">
            <span className="current-price">
              Rs {item.current_price}
            </span>
            <span className="original-price">
              Rs {item.original_price}
            </span>
            <span className="discount-percentage">
              ({item.discount_percentage}% OFF)
            </span>
          </div>

          <div className="return-period">
            <span className="return-period-days">
              {item.return_period} days
            </span>{" "}
            return available
          </div>

          <div className="delivery-details">
            Delivery by{" "}
            <span className="delivery-details-days">
              {item.delivery_date}
            </span>
          </div>
        </div>

        <button
          type="button"
          className="remove-from-cart"
          onClick={handleRemove}
          aria-label="Remove from cart"
          title="Remove from cart"
        >
          <MdDeleteForever />
        </button>
      </div>
    </div>
  );
};

export default BagItemContainer;
