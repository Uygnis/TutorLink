import React, { useEffect } from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { useAppDispatch, useAppSelector } from "@/redux/store";
import { setUser } from "@/redux/userSlice";

// Pages
import Home from "@/pages/Home";
import Login from "@/pages/Login";
import AdminDashboard from "@/pages/admin/dashboard/AdminDashboard";
import UserDashboard from "@/pages/user/UserDashboard";
import Patients from "@/pages/admin/Patients";
import Wards from "@/pages/admin/Wards";
import Transactions from "@/pages/admin/Transactions";

//Component
import Loading from "@/components/Loading";
import AdminRoute from "@/components/routes/AdminRoute";
import UserRoute from "@/components/routes/UserRoute";
import Doctors from "./pages/admin/dashboard/Doctors";
import Admins from "./pages/admin/dashboard/Admins";

function App() {
  // Redux
  const dispatch = useAppDispatch();
  const { loading } = useAppSelector((state) => state.loaders);

  // Persist user data in redux
  useEffect(() => {
    const storedUserData = localStorage.getItem("user");
    if (storedUserData) {
      const user = JSON.parse(storedUserData);
      dispatch(setUser(user));
    }
  }, [dispatch]);

  return (
    <>
      {loading && <Loading />}
      <ToastContainer />
      <Router>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          {/* Admin Protected Route */}
          <Route element={<AdminRoute />}>
            <Route path="/admin/dashboard" element={<AdminDashboard />}>
              {/* Nested routes */}
              <Route index element={<Doctors />} /> {/* Default to doctors */}
              <Route path="doctors" element={<Doctors />} />
              <Route path="admins" element={<Admins />} />
            </Route>

            {/* Other admin routes */}
            <Route path="/admin/patients" element={<Patients />} />
            <Route path="/admin/wards" element={<Wards />} />
            <Route path="/admin/transactions" element={<Transactions />} />
          </Route>

          {/* User Protected Route */}
          <Route element={<UserRoute />}>
            <Route path="/user/dashboard" element={<UserDashboard />} />
          </Route>
        </Routes>
      </Router>
    </>
  );
}

export default App;
