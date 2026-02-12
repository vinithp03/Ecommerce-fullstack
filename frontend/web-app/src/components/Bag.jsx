import React, { useEffect } from "react";
import BagSummary from "./BagSummary";
import BagItemContainer from "./BagItemContainer";
import { useSelector, useDispatch } from "react-redux";
import { fetchCartDetails } from "../store/cartSlice";

const Bag = () => {
  const dispatch = useDispatch();

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
        <div className="bag-page">
          Loading your cart...
        </div>
      </main>
    );
  }

  if (status === "failed") {
    return (
      <main>
        <div className="bag-page">
          Error: {error}
        </div>
      </main>
    );
  }

  if (status === "succeeded" && finalItems.length === 0) {
    return (
      <main>
        <div className="bag-page">
          Your bag is empty.
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

        <BagSummary summary={finalItems} />
      </div>
    </main>
  );
};

export default Bag;
