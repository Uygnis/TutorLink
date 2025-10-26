import { useEffect } from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { useAppDispatch, useAppSelector } from "@/redux/store";
import { setUser } from "@/redux/userSlice";

// Pages
import Home from "@/pages/Home";
import Login from "@/pages/Login";
import Register from "@/pages/Register";
import AdminDashboard from "@/pages/admin/AdminDashboard";
import UserDashboard from "@/pages/user/UserDashboard";
import StudentDashboard from "@/pages/student/StudentDashboard";
import TutorDashboard from "@/pages/tutor/TutorDashboard";
import FindTutor from "@/pages/student/FindTutor";
import ManageTutors from "@/pages/admin/ManageTutors";
import ManageStudents from "@/pages/admin/ManageStudents";
import ViewTutorProfile from "@/pages/tutor/TutorProfile";
import ManageAdmins from "@/pages/admin/ManageAdmins";
import ViewTutorDetails from "@/pages/student/ViewTutorDetails";
import StudentProfile from "@/pages/student/StudentProfile";

//Component
import Loading from "@/components/Loading";
import AdminRoute from "@/components/routes/AdminRoute";
import UserRoute from "@/components/routes/UserRoute";
import StudentRoute from "@/components/routes/StudentRoute";
import AdminViewTutorDetails from "./pages/admin/AdminViewTutorDetails";
import AdminViewAdminDetails from "./pages/admin/AdminViewAdminDetails";
import AdminViewStudentDetails from "./pages/admin/AdminViewStudentDetails";
import Wallet from "./pages/student/Wallet";
import WalletSuccessPage from "./pages/student/WalletSuccessPage";
import WalletCancelPage from "./pages/student/WalletCancelPage";
import TutorWalletPage from "./pages/tutor/TutorWalletPage";

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
          <Route path="/register" element={<Register />} />

          {/* Admin Protected Route */}
          <Route element={<AdminRoute />}>
            <Route path="/admin/dashboard" element={<AdminDashboard />} />
            <Route path="/admin/tutors" element={<ManageTutors />} />
            <Route path="/admin/tutors/:tutorId" element={<AdminViewTutorDetails />} />
            <Route path="/admin/admins/:adminId" element={<AdminViewAdminDetails />} />
            <Route path="/admin/students/:studentId" element={<AdminViewStudentDetails />} />
            <Route path="/admin/students" element={<ManageStudents />} />
            <Route path="/admin/admins" element={<ManageAdmins />} />
          </Route>

          {/* User Protected Route */}
          <Route element={<UserRoute />}>
            <Route path="/user/dashboard" element={<UserDashboard />} />
          </Route>

          <Route element={<UserRoute />}>
            <Route path="/tutor/dashboard" element={<TutorDashboard />} />
            <Route path="/tutor/profile" element={<ViewTutorProfile />} />
            <Route path="/tutor/wallet" element={<TutorWalletPage />} />
          </Route>

          {/* Student Protected Route */}
          <Route element={<StudentRoute />}>
            <Route path="/student/dashboard" element={<StudentDashboard />} />
            <Route path="/student/find-tutor" element={<FindTutor />} />
            <Route path="/student/view-tutor/:id" element={<ViewTutorDetails />} />
            <Route path="/student/profile" element={<StudentProfile />} />
            <Route path="/student/wallet" element={<Wallet />} />
            <Route path="/wallet/success" element={<WalletSuccessPage />} />
            <Route path="/wallet/cancel" element={<WalletCancelPage />} />
          </Route>
        </Routes>
      </Router>
    </>
  );
}

export default App;
