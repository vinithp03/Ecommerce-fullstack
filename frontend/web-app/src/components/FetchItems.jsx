import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { itemAction } from "../store/ItemSlice";
import { fetchStatusAction } from "../store/FetchingStatusSlice";

const BASE_URL =
  import.meta.env.VITE_CATALOG_BASE_URL?.trim() || "http://localhost";

const FetchItems = () => {
  const { isFetching, fetchDone } = useSelector(
    (store) => store.fetchStatus
  );

  const dispatch = useDispatch();

  useEffect(() => {
    // Run only if we haven't fetched yet
    // and we are not already fetching
    if (fetchDone || isFetching) return;

    const controller = new AbortController();
    const { signal } = controller;

    // Prevent re-entry
    dispatch(fetchStatusAction.markFetchingStarted());

    fetch(`${BASE_URL}/catalog/v1/products`, { signal })
      .then((res) => res.json())
      .then((data) => {
        dispatch(itemAction.addInitialItems(data));
        dispatch(fetchStatusAction.markFetchDone());
      })
      .catch((err) => {
        console.log("Fetch error:", err);
        dispatch(
          fetchStatusAction.markFetchingFinished()
          // or markFetchFailed()
        );
      });

    return () => {
      controller.abort();
    };
  }, [dispatch, fetchDone, isFetching]);

  return null;
};

export default FetchItems;
