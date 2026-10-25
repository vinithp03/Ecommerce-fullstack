// src/store/themeSlice.js
import { createSlice } from "@reduxjs/toolkit";

const STORAGE_KEY = "app-theme";
const initial =
  typeof localStorage !== "undefined"
    ? localStorage.getItem(STORAGE_KEY) || "light"
    : "light";

const themeSlice = createSlice({
  name: "theme",
  initialState: {
    mode: initial, // "light" | "dark"
  },
  reducers: {
    setTheme: (state, action) => {
      state.mode = action.payload;
      if (typeof localStorage !== "undefined") {
        localStorage.setItem(STORAGE_KEY, action.payload);
      }
    },
    toggleTheme: (state) => {
      state.mode = state.mode === "dark" ? "light" : "dark";
      if (typeof localStorage !== "undefined") {
        localStorage.setItem(STORAGE_KEY, state.mode);
      }
    },
  },
});

export const themeAction = themeSlice.actions;
export default themeSlice.reducer;