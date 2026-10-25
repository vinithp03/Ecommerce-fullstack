import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import App from "./routes/App.jsx";
import "./index.css";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import Bag from "./components/Bag.jsx";
import Home from "./routes/Home.jsx";
import { Provider } from "react-redux";
import myntraStore from "./store/Index.js";
import "bootstrap/dist/css/bootstrap.min.css";

import MenSection from "./components/MenSection.jsx";
import WomenSection from "./components/WomenSection.jsx";
import ElectronicSection from "./components/ElectronicSection.jsx";
import HomeLivingSection from "./components/HomeLivingSection.jsx";
import KidSection from "./components/KidSection.jsx";
import Payment from "./components/Payment.jsx";

import LoginSignup from "./routes/LoginSignup.jsx";
import Profile from "./routes/Profile.jsx";
import AuthProvider from "./components/AuthProvider.jsx";
import ThemeProvider from "./components/ThemeProvider.jsx";

const router = createBrowserRouter([
  {
    path: "/",
    element: <App />,
    children: [
      { path: "/", element: <Home /> },
      { path: "/bag", element: <Bag /> },
      { path: "/men", element: <MenSection /> },
      { path: "/women", element: <WomenSection /> },
      { path: "/electronics", element: <ElectronicSection /> },
      { path: "/home&Living", element: <HomeLivingSection /> },
      { path: "/kids", element: <KidSection /> },
      { path: "/payment", element: <Payment /> },
    ],
  },
  { path: "/login", element: <LoginSignup /> },
  { path: "/profile", element: <Profile /> },
]);

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <Provider store={myntraStore}>
      <AuthProvider>
        <ThemeProvider>
          <RouterProvider router={router} />
        </ThemeProvider>
      </AuthProvider>
    </Provider>
  </StrictMode>
);
