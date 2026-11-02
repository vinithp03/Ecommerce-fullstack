import { Outlet } from "react-router-dom";
import Footer from "../components/Footer";
import Header from "../components/Header";
import FetchItems from "../components/FetchItems";
import { useSelector } from "react-redux";
import LoadingSpinner from "../components/LoadingSpinner";
import LoginPopupProvider from "../components/LoginPopupProvider";

function App() {
  const fetchStatus = useSelector((store) => store.fetchStatus);

  return (
    <LoginPopupProvider>
      <Header />
      <Outlet />

      {fetchStatus.currentlyFetching ? (
        <LoadingSpinner />
      ) : null}

      <Footer />
    </LoginPopupProvider>
  );
}

export default App;