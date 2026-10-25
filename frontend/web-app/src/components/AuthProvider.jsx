// src/components/AuthProvider.jsx
import { useEffect } from "react";
import { useDispatch } from "react-redux";
import { meThunk } from "../store/authSlice";

/**
 * Restores the user's session on app load by decoding the stored JWT.
 * Renders nothing — just dispatches auth/me once.
 */
const AuthProvider = ({ children }) => {
  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(meThunk());
  }, [dispatch]);

  return <>{children}</>;
};

export default AuthProvider;