// src/components/ThemeProvider.jsx
import { useEffect } from "react";
import { useSelector } from "react-redux";

/**
 * Syncs store.theme.mode to document.body.dataset.theme so the CSS
 * variables in index.css (body[data-theme="dark"]) take effect.
 * Renders nothing.
 */
const ThemeProvider = ({ children }) => {
  const mode = useSelector((store) => store.theme.mode);

  useEffect(() => {
    document.body.setAttribute("data-theme", mode);
  }, [mode]);

  return <>{children}</>;
};

export default ThemeProvider;