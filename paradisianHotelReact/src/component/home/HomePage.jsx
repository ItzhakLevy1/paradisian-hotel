import React, { useState } from "react";
import RoomSearch from "../common/RoomSearch";
import RoomResult from "../common/RoomResult";

const HomePage = () => {
  const [roomSearchResults, setRoomSearchResults] = useState([]);

  // Function to handle search results
  const handleSearchResult = (results) => {
    setRoomSearchResults(results);
  };

  return (
    <div className="home">
      <img src="./assets/front.jpg" alt="Front Image" className="home-image" loading="lazy"/>
      {/* HEADER / BANNER ROOM SECTION */}
      <section>
        <header className="header-banner">
          <div className="overlay"></div>
          <div className="animated-texts overlay-content">
            <h1>Welcome to</h1>
            <h1>Paradisian Hotel</h1>
            <br />
            <hr></hr>
            <h3>
              Step into a heaven <br></br>of comfort and care
            </h3>
          </div>
        </header>
      </section>

      {/* SEARCH/FIND AVAILABLE ROOM SECTION */}
      <RoomSearch handleSearchResult={handleSearchResult} />
      <RoomResult roomSearchResults={roomSearchResults} />

      {/* <h4>
        <a className="view-rooms-home" href="/rooms">
          All Rooms
        </a>
      </h4> */}

      {/* SERVICES SECTION */}
      <h2 className="home-services">
        <span className="paradisian-color">Services at Paradisian Hotel</span>
      </h2>
      <section className="service-section">
        <div className="service-card">
          {/* <img src="./assets/images/ac.png" alt="Air Conditioning" /> */}
          <img src="./assets/ac.png" alt="Air Conditioning" loading="lazy"/>
          <div className="service-details">
            <h3 className="service-title">Air Conditioning</h3>
            <p className="service-description">
              Stay cool and comfortable throughout your stay with our
              individually controlled in-room air conditioning.
            </p>
          </div>
        </div>
        <div className="service-card">
          <img src="./assets/mini-bar.png" alt="Mini Bar" loading="lazy"/>
          <div className="service-details">
            <h3 className="service-title">Mini Bar</h3>
            <p className="service-description">
              Enjoy a convenient selection of beverages and snacks stocked in
              your room's mini bar with no additional cost.
            </p>
          </div>
        </div>
        <div className="service-card">
          <img src="./assets/parking.png" alt="Parking" loading="lazy"/>
          <div className="service-details">
            <h3 className="service-title">Parking</h3>
            <p className="service-description">
              We offer on-site parking for your convenience . Please inquire
              about valet parking options if available.
            </p>
          </div>
        </div>
        <div className="service-card">
          <img src="./assets/wifi.png" alt="WiFi" loading="lazy"/>
          <div className="service-details">
            <h3 className="service-title">WiFi</h3>
            <p className="service-description">
              Stay connected throughout your stay with complimentary high-speed
              Wi-Fi access available in all guest rooms and public areas.
            </p>
          </div>
        </div>
      </section>
      {/* AVAILABLE ROOMS SECTION */}
      <section></section>
    </div>
  );
};

export default HomePage;
