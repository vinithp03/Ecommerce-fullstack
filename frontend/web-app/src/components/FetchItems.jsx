import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { itemAction } from "../store/ItemSlice";
import { fetchStatusAction } from "../store/FetchingStatusSlice";

const BASE_URL =
  import.meta.env.VITE_CATALOG_BASE_URL?.trim() || "http://localhost";

const DEFAULT_PAGE_SIZE = 20;

/**
 * Headless pagination hook.
 *
 * Fetches one page of products at a time from the catalog service and stores
 * the page content + pagination metadata in the Redux store. Returns the
 * pagination state plus a `goToPage(page)` helper so the consuming route can
 * render traditional "Previous / page numbers / Next" buttons.
 *
 * @param {string|null} section - optional section filter (e.g. "men")
 * @param {number} pageSize - items per page
 * @returns {{ currentPage, totalPages, totalElements, hasNext, hasPrevious, currentlyFetching, goToPage }}
 */
export const useFetchItems = (section = null, pageSize = DEFAULT_PAGE_SIZE) => {
  const { currentlyFetching, currentPage } = useSelector(
    (store) => store.fetchStatus
  );
  const {
    items,
    hasNext,
    hasPrevious,
    totalPages,
    totalElements,
  } = useSelector((store) => store.items);

  const dispatch = useDispatch();
  // Track the page currently held in the store so we can avoid re-fetching
  // the same page and re-fetch after a route change.
  const [storePage, setStorePage] = useState(0);

  const fetchPage = (page) => {
    if (currentlyFetching) return;

    const sectionQuery = section ? `&section=${encodeURIComponent(section)}` : "";
    const url = `${BASE_URL}/catalog/v1/products?page=${page}&size=${pageSize}${sectionQuery}`;

    dispatch(fetchStatusAction.markFetchingStarted(page));

    fetch(url)
      .then((res) => res.json())
      .then((data) => {
        dispatch(
          itemAction.getItems({
            content: data.content,
            page: data.page,
            size: data.size,
            totalPages: data.totalPages,
            totalElements: data.totalElements,
            hasNext: data.hasNext,
            hasPrevious: data.hasPrevious,
          })
        );
        setStorePage(data.page);
        dispatch(fetchStatusAction.markFetchDone());
      })
      .catch((err) => {
        console.error("Fetch error:", err);
        dispatch(fetchStatusAction.markFetchingFinished());
      });
  };

  // Load the first page on mount / when the section or page size changes.
  useEffect(() => {
    fetchPage(0);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [section, pageSize]);

  const goToPage = (page) => {
    if (page < 0 || page >= totalPages || page === storePage) return;
    fetchPage(page);
  };

  return {
    items,
    currentPage: storePage,
    totalPages,
    totalElements,
    hasNext,
    hasPrevious,
    currentlyFetching,
    goToPage,
  };
};

/**
 * Backwards-compatible component wrapper. Routes that don't need to render
 * pagination buttons can keep using <FetchItems />.
 */
const FetchItems = () => {
  useFetchItems();
  return null;
};

export default FetchItems;