// src/components/AuthGuard.jsx
import { useEffect } from "react";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";

/**
 * Wraps components that require authentication.
 * If not logged in, redirects to the login/signup page.
 */
const AuthGuard = ({ children }) => {
  const isAuthenticated = useSelector((store) => store.auth.user !== null);
  const navigate = useNavigate();

  useEffect(() => {
    if (!isAuthenticated) {
      navigate("/login");
    }
  }, [isAuthenticated, navigate]);

  return <>{children}</>;
};

export default AuthGuard;