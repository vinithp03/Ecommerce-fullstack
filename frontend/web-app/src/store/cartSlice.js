// src/store/cartSlice.js
import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { fetchCart, removeCartItem, upsertCartItem } from "../api/cartService";

export const fetchCartDetails = createAsyncThunk(
  "cart/fetchCartDetails",
  async () => {
    const items = await fetchCart();
    return Array.isArray(items) ? items : [];
  }
);

export const addItemToCart = createAsyncThunk(
  "cart/addItemToCart",
  async (id, { dispatch, rejectWithValue }) => {
    try {
      await upsertCartItem(id); // POST /gap/cart/items/{id}
      await dispatch(fetchCartDetails()); // refresh
      return { id };
    } catch (err) {
      return rejectWithValue(err.message || "Failed to add item");
    }
  }
);

export const removeItemFromCart = createAsyncThunk(
  "cart/removeItemFromCart",
  async (id, { dispatch, rejectWithValue }) => {
    try {
      await removeCartItem(id); // DELETE /gap/cart/items/{id}
      await dispatch(fetchCartDetails()); // refresh
      return { id };
    } catch (err) {
      return rejectWithValue(err.message || "Failed to Remove item");
    }
  }
);

const cartSlice = createSlice({
  name: "cart",
  initialState: { items: [], status: "idle", error: null },
  reducers: {},
  extraReducers: (b) => {
    // fetch
    b.addCase(fetchCartDetails.pending, (s) => {
      s.status = "loading";
      s.error = null;
    })
      .addCase(fetchCartDetails.fulfilled, (s, a) => {
        s.status = "succeeded";
        s.items = Array.isArray(a.payload) ? a.payload : [];
      })
      .addCase(fetchCartDetails.rejected, (s, a) => {
        s.status = "failed";
        s.error = a.error?.message || "Failed to fetch cart";
      });

    // add
    b.addCase(addItemToCart.pending, (s) => {
      s.status = "loading";
      s.error = null;
    })
      .addCase(addItemToCart.fulfilled, (s) => {
        s.status = "succeeded";
        s.error = null;
      })
      .addCase(addItemToCart.rejected, (s, a) => {
        s.status = "failed";
        s.error = a.payload || "Failed to add item";
      });

    // remove
    b.addCase(removeItemFromCart.pending, (s) => {
      s.status = "loading";
      s.error = null;
    })
      .addCase(removeItemFromCart.fulfilled, (s) => {
        s.status = "succeeded";
        s.error = null;
      })
      .addCase(removeItemFromCart.rejected, (s, a) => {
        s.status = "failed";
        s.error = a.payload || "Failed to Remove item";
      });
  },
});

export default cartSlice.reducer;
