import React, { useEffect } from "react";
import HomeItem from "../components/HomeItem";
import WelcomeBanner from "../components/WelcomeBanner";
import { useFetchItems } from "../components/FetchItems";
import Pagination from "../components/Pagination";
import { fetchCartDetails } from "../store/cartSlice";
import { useDispatch } from "react-redux";

const Home = () => {
  const dispatch = useDispatch();
  const { items, currentPage, totalPages, hasNext, hasPrevious, goToPage } =
    useFetchItems();

  // Load cart from DB on initial render
  useEffect(() => {
    dispatch(fetchCartDetails());
  }, [dispatch]);

  return (
    <main>
      <WelcomeBanner />
      <div className="items-container">
        {items.map((item) => (
          <HomeItem key={item.id} item={item} />
        ))}
      </div>

          {/* Classic pagination controls */}
      <Pagination
        currentPage={currentPage}
        totalPages={totalPages}
        hasNext={hasNext}
        hasPrevious={hasPrevious}
        goToPage={goToPage}
      />
    </main>
  );
};

export default Home;