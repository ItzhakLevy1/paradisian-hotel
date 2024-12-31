import React from "react";
import { NavLink, useLocation } from "react-router-dom";
import ApiService from "./ApiService";

// If the user is not authenticated he will be reffered to the login page,
// Once he is logged in he will be automatically reffered back to the page he was initially trying to access.

export const ProtectedRoute = ({ element: Component }) => {
  // This component is responsible for protecting routes that require the user to
  // be authenticated before accessing them.
  const location = useLocation(); // useLocation(): This React Router hook gives the current location object,
  // which contains information about the current URL.
  // It is used here to store the page the user initially tried to access.

  return ApiService.isAuthenticated() ? ( // This function checks whether the user is authenticated.
    // You would define this function in your ApiService to return true if the user is logged in and false if not.
    Component // If the user is authenticated (ApiService.isAuthenticated() returns true), the Component is rendered.
  ) : (
    <NavLink to="/login" replace state={{ from: location }} /> // If the user is not authenticated, they are redirected to the /login page using NavLink,
  ); // The state={{ from: location }} passes the current location to the login page,
  // so after logging in, the user can be redirected back to the page they initially wanted to access.
};

export const AdminRoute = ({ element: Component }) => {
  // This component is similar to ProtectedRoute,
  // but it adds another layer of authorization by checking if the user has admin privileges.

  const location = useLocation();

  return ApiService.isAdmin() ? ( // ApiService.isAdmin(): This function checks if the user has admin privileges.
    // You would define this in your ApiService, likely by verifying the user's role.
    Component // If ApiService.isAdmin() returns true, the Component is rendered.
  ) : (
    <NavLink to="/login" replace state={{ from: location }} /> // If not, the user is redirected to the /login page, similar to ProtectedRoute.
  );
};
