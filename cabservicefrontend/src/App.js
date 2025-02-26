import React from 'react';
import "bootstrap/dist/css/bootstrap.min.css";
import { BrowserRouter as Router, Routes, Route, Link, useLocation } from 'react-router-dom';
import './landingPage.css';


import LoginPage from './pages/Login';
import SignInPage from './pages/Signup';
import AdminHome from './pages/admin/home';
import UserHome from './pages/user/home';
import Cabs from "./pages/admin/cab";
import AdminDrivers from './pages/admin/drivers';
import AddCabModal from './pages/admin/components/addcarmodel';
import AddDriverModal from './pages/admin/components/adddrivermodel';
import AdminRoutes from './pages/admin/route';
import AddRouteModal from './pages/admin/components/addroutemodel';

import Img1 from "./images/image2.jpg";

import blacklogoimage from './images/MegaCityLogo.png';
import LocationSearch from './pages/user/components/locationsearch';

const Header = () => (
  <nav className="landingpage-navbar navbar navbar-expand-lg navbar-light">
    <div className="container">
      <a className="navbar-brand" href="#">
        <img src={blacklogoimage} alt="Mega City Cab Logo" className="logo-image" />
      </a>
      <div className="collapse navbar-collapse" id="navbarNav">
        <ul className="navbar-nav ml-auto">
          <div className="nav-group">
            <li className="nav-item">
              <a className="nav-link nav-box" href="#howItWorks">How Does This Work</a>
            </li>
            <li className="nav-item">
              <a className="nav-link nav-box" href="#faq">FAQ</a>
            </li>
            <li className="nav-item">
              <a className="nav-link nav-box" href="#contactUs">Contact Us</a>
            </li>
          </div>
          <div className="nav-auth">
            <li className="nav-item">
              <Link className="nav-link auth-box" to="/login">Login</Link>
            </li>
            <li className="nav-item">
              <Link className="nav-link auth-box sign-up" to="/signin">Sign Up</Link>
            </li>
          </div>
        </ul>
      </div>
    </div>
  </nav>
);

const FeaturedCards = () => (
  <div className="container my-5">
    <div className="row">
      <div className="col-md-3">
        <div className="feature-card">
          <h3>Affordable Fares</h3>
          <p>Enjoy budget-friendly rides with transparent pricing and no hidden fees.</p>
        </div>
      </div>
      <div className="col-md-3">
        <div className="feature-card">
          <h3>24/7 Availability</h3>
          <p>We’re here for you anytime, anywhere, ensuring convenience and reliability.</p>
        </div>
      </div>
      <div className="col-md-3">
        <div className="feature-card">
          <h3>Safety First</h3>
          <p>All our drivers are professionally trained and verified for your safety.</p>
        </div>
      </div>
      <div className="col-md-3">
        <div className="feature-card">
          <h3>Easy Booking</h3>
          <p>Book a ride in just a few taps with our user-friendly platform.</p>
        </div>
      </div>
    </div>
  </div>
);

const App = () => {
  const location = useLocation(); // Track the current route

  return (
    <div>
      {/* Conditionally render Header only on the landing page */}
      {location.pathname === '/' && <Header />}

      <Routes>
        {/* Landing Page */}
        <Route path="/" element={
          <div className="content-section container-fluid d-flex align-items-center" style={{ height: "100vh" }}>
            <div className="row w-100">
              <div className="col-md-5">
                <img src= {Img1} alt="Service Image" className="img-fluid rounded" />
              </div>
              <div className="col-md-7">
                <div className="text-box">
                  <div className="customer-info text-right">
                    <h2 className="big-text">2.4M+</h2>
                    <p className="small-text">Satisfied Customers</p>
                  </div>
                  <h1 className="text-title custom-title">Reliable & Comfortable Rides Anytime, Anywhere</h1>
                  <p className="text-description">Join the thousands of satisfied customers who choose Mega City Cab for a seamless, high-quality travel experience. We are here to support you every step of the way, so you can focus on what matters most — enjoying your journey.</p>
                  <div className="button-container">
                    <Link to="/login" className="btn btn-primary">Book Now</Link>
                  </div>
                </div>
              </div>
            </div>
          </div>
        } />
        
        {/* Login Page */}
        <Route path="/login" element={<LoginPage />} />

        {/* Sign In Page */}
        <Route path="/signin" element={<SignInPage />} />
        
        {/* Sign In Page */}
        <Route path="/" element={<FeaturedCards />} />
        
        {/* admin/home */}
        <Route path="/admin/home" element={<AdminHome />}/>

        {/* admin/cabs */}
        <Route path="/admin/cab" element={<Cabs />}/>

        {/* admin/addcarmodel */}
        <Route path="/admin/addcarmodel" element={<AddCabModal />}/>

        {/* admin/cabs */}
        <Route path="/admin/drivers" element={<AdminDrivers />}/>
        
        {/* admin/addDrivermodel */}
        <Route path="/admin/adddrivermodel" element={<AddDriverModal />}/>

        {/* admin/routes */}
        <Route path="/admin/route" element={<AdminRoutes />}/>

        {/* admin/routes/addroutesmodel */}
        <Route path="/admin/addroutesmodel" element={<AddRouteModal />}/>


        {/* user/home */}
        <Route path="/user/home" element={<UserHome />}/>

        {/* user/component/locationsearch */}
        <Route path="/user/locationSearch" element={<LocationSearch />}/>

      </Routes>
    </div>
  );
};

const RootApp = () => {
  return (
    <Router>
      <App />
    </Router>
  );
};

export default RootApp;
