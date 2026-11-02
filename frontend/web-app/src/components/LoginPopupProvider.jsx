import React from "react";
import { useSelector, useDispatch } from "react-redux";
import LoginPopup from "./LoginPopup";
import { authAction } from "../store/authSlice";

/**
 * Renders a single LoginPopup at the app level, driven by Redux state.
 * This keeps the popup out of every product card so it never
 * overlaps or blinks when multiple cards trigger it.
 */
const LoginPopupProvider = ({ children }) => {
  const dispatch = useDispatch();
  const showPopup = useSelector((store) => store.auth.showLoginPopup);

  return (
    <>
      {children}
      <LoginPopup
        open={showPopup}
        onClose={() => dispatch(authAction.setLoginPopup(false))}
      />
    </>
  );
};

export default LoginPopupProvider;