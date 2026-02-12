import React, { useEffect } from "react";
import HomeItem from "../components/HomeItem";
import { useDispatch, useSelector } from "react-redux";
import { fetchCartDetails } from "../store/cartSlice";

const Home = () => {
  const dispatch = useDispatch();

  // Load cart from DB on initial render
  useEffect(() => {
    dispatch(fetchCartDetails());
  }, [dispatch]);

  const items = useSelector((store) => store.items);

  return (
    <main>
      <div className="items-container">
        {items.map((item) => (
          <HomeItem key={item.id} item={item} />
        ))}
      </div>
    </main>
  );
};

export default Home;
