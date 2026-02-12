// src/store/Index.js
import { configureStore } from "@reduxjs/toolkit";
import itemSlice from "./ItemSlice";
import fetchStatusSlice from "./FetchingStatusSlice";
import bagReducer from "./bagSlice"; // default export = reducer
import cartReducer from "./cartSlice"; // default export = reducer

const myntraStore = configureStore({
  reducer: {
    items: itemSlice.reducer,
    fetchStatus: fetchStatusSlice.reducer,
    bag: bagReducer,
    cart: cartReducer,
  },
});

export default myntraStore;
