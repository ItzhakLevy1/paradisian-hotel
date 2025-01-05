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
    <nav className="navbar navbar-expand-lg navbar-dark">
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
              <NavLink 
                to="/home" 
                className="nav-link" 
                style={({ isActive }) => ({ color: isActive ? 'white' : 'inherit' })}
              >
                Home
              </NavLink>
            </li>
            <li className="nav-item">
              <NavLink 
                to="/rooms" 
                className="nav-link" 
                style={({ isActive }) => ({ color: isActive ? 'white' : 'inherit' })}
              >
                Rooms
              </NavLink>
            </li>
            <li className="nav-item">
              <NavLink 
                to="/find-booking" 
                className="nav-link" 
                style={({ isActive }) => ({ color: isActive ? 'white' : 'inherit' })}
              >
                Find my Booking
              </NavLink>
            </li>
            {isUser && (
              <li className="nav-item">
                <NavLink 
                  to="/profile" 
                  className="nav-link" 
                  style={({ isActive }) => ({ color: isActive ? 'white' : 'inherit' })}
                >
                  Profile
                </NavLink>
              </li>
            )}
            {isAdmin && (
              <li className="nav-item">
                <NavLink 
                  to="/admin" 
                  className="nav-link" 
                  style={({ isActive }) => ({ color: isActive ? 'white' : 'inherit' })}
                >
                  Admin
                </NavLink>
              </li>
            )}
            {!isAuthenticated && (
              <li className="nav-item">
                <NavLink 
                  to="/login" 
                  className="nav-link" 
                  style={({ isActive }) => ({ color: isActive ? 'white' : 'inherit' })}
                >
                  Login
                </NavLink>
              </li>
            )}
            {!isAuthenticated && (
              <li className="nav-item">
                <NavLink 
                  to="/register" 
                  className="nav-link" 
                  style={({ isActive }) => ({ color: isActive ? 'white' : 'inherit' })}
                >
                  Register
                </NavLink>
              </li>
            )}
            {isAuthenticated && (
              <li className="nav-item" onClick={handleLogout}>
                <span className="nav-link active" id="logout">Logout</span>
              </li>
            )}
          </ul>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
