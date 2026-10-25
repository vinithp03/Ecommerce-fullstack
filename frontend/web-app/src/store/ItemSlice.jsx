import { createSlice } from "@reduxjs/toolkit";

const itemSlice = createSlice({
  name: "items",
  initialState: {
    items: [],
    currentPage: 0,
    totalPages: 0,
    totalElements: 0,
    pageSize: 20,
    hasNext: false,
    hasPrevious: false,
  },
  reducers: {
    // Replace the whole page with the items + pagination metadata returned
    // by the catalog service for a given page number.
    setItems: (state, action) => {
      const { content, page, size, totalPages, totalElements, hasNext, hasPrevious } = action.payload;
      state.items = content;
      state.currentPage = page;
      state.pageSize = size;
      state.totalPages = totalPages;
      state.totalElements = totalElements;
      state.hasNext = hasNext;
      state.hasPrevious = hasPrevious;
    },
    clearItems: (state) => {
      state.items = [];
      state.currentPage = 0;
      state.totalPages = 0;
      state.totalElements = 0;
      state.hasNext = false;
      state.hasPrevious = false;
    },
  },
});

export const itemAction = itemSlice.actions;
export default itemSlice;