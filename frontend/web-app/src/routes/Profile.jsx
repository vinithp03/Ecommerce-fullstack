import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Link, useNavigate } from "react-router-dom";
import { authAction } from "../store/authSlice";

const Profile = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const user = useSelector((store) => store.auth.user);

  useEffect(() => {
    if (!user) {
      navigate("/login");
    }
  }, [user, navigate]);

  if (!user) return null;

  return (
    <main className="profile-page">
      <div className="profile-card">
        <div className="profile-header">
          <div className="profile-avatar">
            {user.name ? user.name.charAt(0).toUpperCase() : "U"}
          </div>
          <div>
            <h1>{user.name}</h1>
            <p>{user.email}</p>
          </div>
        </div>

        <div className="profile-divider"></div>

        <div className="profile-actions">
          <Link to="/bag" className="profile-link">
            🛒 My Bag
          </Link>
          <Link to="/" className="profile-link">
            🛍️ Continue Shopping
          </Link>
          <button
            type="button"
            className="profile-link logout-btn"
            onClick={() => {
              dispatch(authAction.logout());
              navigate("/");
            }}
          >
            🚪 Logout
          </button>
        </div>
      </div>
    </main>
  );
};

export default Profile;