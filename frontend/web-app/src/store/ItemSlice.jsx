import { createSlice } from "@reduxjs/toolkit";

const itemSlice = createSlice({
  name: "items",
  initialState: [],
  reducers: {
    addInitialItems: (state, action) => {
      return action.payload;
    },
    addMoreItems: (state, action) => {
      return [...state, ...action.payload];
    },
  },
});

export const itemAction = itemSlice.actions;
export default itemSlice;