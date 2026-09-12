import { Route, Routes, useLocation } from "react-router-dom";
import { useEffect } from "react";

import Landing from "./pages/Landing";
import Home from "./pages/Home";
import VehicleDetails from "./pages/VehicleDetails";

import Login from "./pages/Login";
import Register from "./pages/Register";
import ForgotPassword from "./pages/ForgotPassword";
import ResetPassword from "./pages/ResetPassword";
import Account from "./pages/Account";
import AdminVehicles from "./admin/pages/AdminVehicles";
import AdminLayout from "./admin/layouts/AdminLayout";
import AdminDashboard from "./admin/pages/AdminDashboard";
import ProtectedRoute from "./routes/ProtectedRoute";
import AdminVehicleForm from "./admin/pages/AdminVehicleForm";
import AdminVehicleMedia from "./admin/pages/AdminVehicleMedia";
import AdminCategories from "./admin/pages/AdminCategories";
import AdminDealerships from "./admin/pages/AdminDealerships";
import AdminOrders from "./admin/pages/AdminOrders";
import AdminCustomers from "./admin/pages/AdminCustomers";
import AdminCoupons from "./admin/pages/AdminCoupons";
import AdminTestDrives from "./admin/pages/AdminTestDrives";
import AdminPayments from "./admin/pages/AdminPayments";
import AdminProfile from "./admin/pages/AdminProfile";
function App() {
  const location = useLocation();

  useEffect(() => {
    if (!location.hash) {
      window.scrollTo({ top: 0, behavior: "smooth" });
      return;
    }

    const target = document.getElementById(location.hash.slice(1));
    target?.scrollIntoView({ behavior: "smooth", block: "start" });
  }, [location.pathname, location.hash]);

  return (
    <Routes>
      <Route path="/" element={<Landing />} />

      <Route path="/home" element={<Home />} />

      <Route path="/vehicles/:id" element={<VehicleDetails />} />

      <Route path="/login" element={<Login />} />

      <Route path="/register" element={<Register />} />
      <Route path="/forgot-password" element={<ForgotPassword />} />
      <Route path="/reset-password" element={<ResetPassword />} />
      <Route
        path="/account"
        element={
          <ProtectedRoute>
            <Account />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin"
        element={
          <ProtectedRoute adminOnly>
            <AdminLayout />
          </ProtectedRoute>
        }
      >
        <Route path="profile" element={<AdminProfile />} />
        <Route path="payments" element={<AdminPayments />} />
        <Route path="coupons" element={<AdminCoupons />} />

        <Route path="test-drives" element={<AdminTestDrives />} />
        <Route path="vehicles/new" element={<AdminVehicleForm />} />
        <Route path="orders" element={<AdminOrders />} />

        <Route path="customers" element={<AdminCustomers />} />
        <Route path="vehicles/:id/media" element={<AdminVehicleMedia />} />
        <Route path="categories" element={<AdminCategories />} />

        <Route path="dealerships" element={<AdminDealerships />} />
        <Route path="vehicles/:id/edit" element={<AdminVehicleForm />} />
        <Route index element={<AdminDashboard />} />
        <Route path="vehicles" element={<AdminVehicles />} />
      </Route>
    </Routes>
  );
}

export default App;
