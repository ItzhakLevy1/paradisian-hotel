import "./App.css";
import React from "react";
import {
  BrowserRouter,
  Routes,
  Route,
  Navigate,
  useLocation,
} from "react-router-dom";
import Navbar from "./component/common/Navbar";
import FooterComponent from "./component/common/Footer";
import HomePage from "./component/home/HomePage";
import AllRoomsPage from "./component/booking_rooms/AllRoomsPage";
import FindBookingPage from "./component/booking_rooms/FindBookingPage";
import RoomDetailsPage from "./component/booking_rooms/RoomDetailsPage";
import LoginPage from "./component/auth/LoginPage";
import RegisterPage from "./component/auth/RegisterPage";
import ProfilePage from "./component/profile/ProfilePage";
import EditRoomPage from "./component/admin/EditRoomPage";
import AdminPage from "./component/admin/AdminPage";
import ManageRoomPage from "./component/admin/ManageRoomPage";
import ManageBookingsPage from "./component/admin/ManageBookingsPage"; // Import ManageBookingsPage
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle.min.js";
import toastr from "toastr";
import "toastr/build/toastr.min.css";

// Configure toastr settings
toastr.options = {
  positionClass: "toast-top-center",
  preventDuplicates: true,
  closeButton: true,
  progressBar: true,
  timeOut: "5000",
  extendedTimeOut: "1000",
  toastClass: "toast toast-error", // Add custom class for error toasts
};

function App() {
  return (
    <BrowserRouter>
      <AppContent />
    </BrowserRouter>
  );
}

function AppContent() {
  const location = useLocation();
  const isHomePage = location.pathname === "/home";

  return (
    <div className={`App ${!isHomePage ? "newBackground" : ""}`}>
      <Navbar />
      <div className="content">
        <Routes>
          <Route exact path="/home" element={<HomePage />} />
          <Route exact path="/rooms" element={<AllRoomsPage />} />
          <Route path="/find-booking" element={<FindBookingPage/>}></Route>
          <Route path="/room-details-book/:roomId" element={<RoomDetailsPage/>}></Route>
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/profile" element={<ProfilePage />} />
          <Route path="/admin/edit-room/:roomId" element={<EditRoomPage />} />
          <Route path="/admin" element={<AdminPage />} />
          <Route path="/admin/manage-rooms" element={<ManageRoomPage />} /> {/* Add route for ManageRoomsPage */}
          <Route path="/admin/manage-bookings" element={<ManageBookingsPage />} /> {/* Add route for ManageBookingsPage */}
          <Route path="*" element={<Navigate to="/home" />} /> {/* Default route so that on first load the user sees the home page*/}
        </Routes>
      </div>
      <FooterComponent />
    </div>
  );
}

export default App;
