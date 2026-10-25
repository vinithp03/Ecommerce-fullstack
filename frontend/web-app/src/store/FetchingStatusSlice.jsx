import { createSlice } from "@reduxjs/toolkit";

const fetchStatusSlice = createSlice({
  name: "fetchStatus",
  initialState: { fetchDone: false, currentlyFetching: false, currentPage: 0 },
  reducers: {
    markFetchDone: (state) => {
      state.fetchDone = true;
      state.currentlyFetching = false;
    },
    markFetchingStarted: (state, action) => {
      state.currentlyFetching = true;
      if (action.payload !== undefined) {
        state.currentPage = action.payload;
      }
    },
    markFetchingFinished: (state) => {
      state.currentlyFetching = false;
    },
  },
});

export const fetchStatusAction = fetchStatusSlice.actions;
export default fetchStatusSlice;