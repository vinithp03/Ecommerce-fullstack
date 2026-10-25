import React, { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Link, useNavigate } from "react-router-dom";
import { loginThunk, signupThunk } from "../store/authSlice";

const LoginSignup = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const auth = useSelector((store) => store.auth);
  const { status, error } = auth;

  const [mode, setMode] = useState("login"); // "login" | "signup"
  const [form, setForm] = useState({
    name: "",
    email: "",
    password: "",
    confirmPassword: "",
  });

  const handleChange = (key) => (e) => {
    setForm({ ...form, [key]: e.target.value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    if (mode === "login") {
      if (!form.email || !form.password) {
        // error handled by thunk below
      }
      dispatch(
        loginThunk({ email: form.email, password: form.password })
      ).then((action) => {
        if (action.payload) {
          navigate("/");
        }
      });
    } else {
      if (form.password !== form.confirmPassword) {
        return;
      }
      dispatch(
        signupThunk({
          name: form.name,
          email: form.email,
          password: form.password,
        })
      ).then((action) => {
        if (action.payload) {
          navigate("/");
        }
      });
    }
  };

  return (
    <main className="auth-page">
      <div className="auth-card">
        <div className="auth-header">
          <h1>{mode === "login" ? "Welcome Back" : "Create Account"}</h1>
          <p className="auth-subtitle">
            {mode === "login"
              ? "Sign in to continue shopping"
              : "Join us to save your orders and bag"}
          </p>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">
          {mode === "signup" && (
            <div className="form-group">
              <label htmlFor="name">Full name</label>
              <input
                id="name"
                name="name"
                type="text"
                value={form.name}
                onChange={handleChange("name")}
                placeholder="John Doe"
                disabled={status === "loading"}
              />
            </div>
          )}

          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              id="email"
              name="email"
              type="email"
              value={form.email}
              onChange={handleChange("email")}
              placeholder="you@example.com"
              disabled={status === "loading"}
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              id="password"
              name="password"
              type="password"
              value={form.password}
              onChange={handleChange("password")}
              placeholder="••••••••"
              disabled={status === "loading"}
            />
          </div>

          {mode === "signup" && (
            <div className="form-group">
              <label htmlFor="confirmPassword">Confirm password</label>
              <input
                id="confirmPassword"
                name="confirmPassword"
                type="password"
                value={form.confirmPassword}
                onChange={handleChange("confirmPassword")}
                placeholder="••••••••"
                disabled={status === "loading"}
              />
            </div>
          )}

          {error && (
            <div className="auth-error">{error}</div>
          )}

          <button
            type="submit"
            className="auth-btn"
            disabled={status === "loading"}
          >
            {status === "loading" ? "Please wait..." : mode === "login" ? "Sign in" : "Create account"}
          </button>
        </form>

        <div className="auth-footer">
          {mode === "login"
            ? `Don't have an account? `
            : "Already have an account? "}
          <button
            type="button"
            className="auth-toggle"
            onClick={() => setMode(mode === "login" ? "signup" : "login")}
          >
            {mode === "login" ? "Sign up" : "Sign in"}
          </button>
        </div>

        <div className="auth-links">
          <Link to="/" className="back-link">
            ← Back to shopping
          </Link>
        </div>
      </div>
    </main>
  );
};

export default LoginSignup;