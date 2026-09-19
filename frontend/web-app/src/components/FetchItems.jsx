import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { itemAction } from "../store/ItemSlice";
import { fetchStatusAction } from "../store/FetchingStatusSlice";

const BASE_URL =
  import.meta.env.VITE_CATALOG_BASE_URL?.trim() || "http://localhost";

const FetchItems = () => {
  const { currentlyFetching, fetchDone } = useSelector(
    (store) => store.fetchStatus
  );

  const dispatch = useDispatch();
  const [cursor, setCursor] = useState(null);
  const [hasNext, setHasNext] = useState(true);
  const pageSize = 20;

  const fetchProducts = (cursorValue) => {
    if (currentlyFetching || !hasNext) return;

    const url = cursorValue
      ? `${BASE_URL}/catalog/v1/products?cursor=${cursorValue}&size=${pageSize}`
      : `${BASE_URL}/catalog/v1/products?size=${pageSize}`;

    dispatch(fetchStatusAction.markFetchingStarted());

    fetch(url)
      .then((res) => res.json())
      .then((data) => {
        if (cursorValue === null) {
          dispatch(itemAction.addInitialItems(data.content));
        } else {
          dispatch(itemAction.addMoreItems(data.content));
        }
        setCursor(data.nextCursor);
        setHasNext(data.hasNext);
        dispatch(fetchStatusAction.markFetchDone());
      })
      .catch((err) => {
        console.error("Fetch error:", err);
        dispatch(fetchStatusAction.markFetchingFinished());
      });
  };

  useEffect(() => {
    fetchProducts(null);
  }, []);

  useEffect(() => {
    const handleScroll = () => {
      if (
        window.innerHeight + window.scrollY >=
        document.documentElement.scrollHeight - 500
      ) {
        if (hasNext && !currentlyFetching) {
          fetchProducts(cursor);
        }
      }
    };

    window.addEventListener("scroll", handleScroll);
    return () => window.removeEventListener("scroll", handleScroll);
  }, [cursor, hasNext, currentlyFetching]);

  return null;
};

export default FetchItems;