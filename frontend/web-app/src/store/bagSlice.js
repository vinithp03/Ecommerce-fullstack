// src/store/bagSlice.js
import { createSlice } from "@reduxjs/toolkit";

const bagSlice = createSlice({
  name: "bag",
  initialState: [], // array of product IDs
  reducers: {
    addInitialItems: (state, action) => {
      const id = action.payload;
      // Prevent duplicates
      if (!state.includes(id)) {
        state.push(id);
      }
    },
    removeFromBag: (state, action) => {
      const id = action.payload;
      return state.filter((itemId) => itemId !== id);
    },
    clearBag: () => {
      return [];
    },
  },
});

export const bagSliceAction = bagSlice.actions;
export default bagSlice.reducer;
