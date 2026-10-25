// src/store/authSlice.js
import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { login, signup, getMe, logout } from "../api/authService";

export const loginThunk = createAsyncThunk("auth/login", login, {
  rejectedValue: (meta, err) => err?.message || "Login failed",
});
export const signupThunk = createAsyncThunk("auth/signup", signup, {
  rejectedValue: (meta, err) => err?.message || "Signup failed",
});
export const meThunk = createAsyncThunk("auth/me", getMe, {
  rejectedValue: () => null,
});

const authSlice = createSlice({
  name: "auth",
  initialState: {
    user: null,          // { id, name, email } | null
    token: null,         // JWT string | null
    status: "idle",      // idle | loading | succeeded | failed
    error: null,
  },
  reducers: {
    logout: (state) => {
      state.user = null;
      state.token = null;
      state.status = "idle";
      state.error = null;
    },
    clearAuthError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (b) => {
    // login
    b.addCase(loginThunk.pending, (s) => {
      s.status = "loading";
      s.error = null;
    })
      .addCase(loginThunk.fulfilled, (s, a) => {
        s.status = "succeeded";
        s.user = a.payload.user;
        s.token = a.payload.token;
        s.error = null;
        localStorage.setItem("authToken", a.payload.token);
      })
      .addCase(loginThunk.rejected, (s, a) => {
        s.status = "failed";
        s.error = a.payload || "Login failed";
      });

    // signup
    b.addCase(signupThunk.pending, (s) => {
      s.status = "loading";
      s.error = null;
    })
      .addCase(signupThunk.fulfilled, (s, a) => {
        s.status = "succeeded";
        s.user = a.payload.user;
        s.token = a.payload.token;
        s.error = null;
        localStorage.setItem("authToken", a.payload.token);
      })
      .addCase(signupThunk.rejected, (s, a) => {
        s.status = "failed";
        s.error = a.payload || "Signup failed";
      });

    // me
    b.addCase(meThunk.fulfilled, (s, a) => {
      s.user = a.payload;
    });
  },
});

export const authAction = authSlice.actions;
export default authSlice.reducer;