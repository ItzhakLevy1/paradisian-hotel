import React from "react";
import { NavLink, useNavigate } from "react-router-dom";
import ApiService from "../../service/ApiService";

const Navbar = () => {
  const isAuthenticated = ApiService.isAuthenticated();
  const isAdmin = ApiService.isAdmin();
  const isUser = ApiService.isUser();
  const navigate = useNavigate();
  const handleLogout = () => {
    const isLogout = window.confirm(
      "Are you sure you want to logout this user?"
    );
    if (isLogout) {
      ApiService.logout();
      navigate("/home");
    }
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-light bg-light">
      <div className="container-fluid">
        <a className="navbar-brand" href="#">
          Paradisian Hotel
        </a>
        <button
          className="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarNav"
          aria-controls="navbarNav"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span className="navbar-toggler-icon"></span>
        </button>
        <div className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav ms-auto">
            <li className="nav-item">
              <NavLink to="/home" className="nav-link active">
                Home
              </NavLink>
            </li>
            <li>
              <NavLink to="/rooms" className="nav-link active">
                Rooms
              </NavLink>
            </li>
            <li>
              <NavLink to="/find-booking" className="nav-link active">
                Find my Booking
              </NavLink>
            </li>

            {isUser && (
              <li>
                <NavLink to="/profile" className="nav-link active">
                  Profile
                </NavLink>
              </li>
            )}
            {isAdmin && (
              <li>
                <NavLink to="/admin" className="nav-link active">
                  Admin
                </NavLink>
              </li>
            )}

            {!isAuthenticated && (
              <li>
                <NavLink to="/login" className="nav-link active">
                  Login
                </NavLink>
              </li>
            )}
            {!isAuthenticated && (
              <li>
                <NavLink to="/register" className="nav-link active">
                  Register
                </NavLink>
              </li>
            )}
            {isAuthenticated && <li onClick={handleLogout}>Logout</li>}
          </ul>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
