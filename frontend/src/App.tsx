import { useEffect } from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { useAppDispatch, useAppSelector } from "@/redux/store";
import { setUser } from "@/redux/userSlice";

// Pages
import Home from "@/pages/Home";
import Login from "@/pages/Login";
import Register from "./pages/Register";
import AdminDashboard from "@/pages/admin/AdminDashboard";
import UserDashboard from "@/pages/user/UserDashboard";
import StudentDashboard from "@/pages/student/StudentDashboard";
import Doctors from "@/pages/admin/Doctors";
import TutorDashboard from "@/pages/tutor/TutorDashboard";
import FindTutor from "@/pages/student/FindTutor";

//Component
import Loading from "@/components/Loading";
import AdminRoute from "@/components/routes/AdminRoute";
import UserRoute from "@/components/routes/UserRoute";
import StudentRoute from "@/components/routes/StudentRoute";
import ViewTutorProfile from "./pages/tutor/TutorProfile";

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
            <Route path="/admin/doctors" element={<Doctors />} />
          </Route>

          {/* User Protected Route */}
          <Route element={<UserRoute />}>
            <Route path="/user/dashboard" element={<UserDashboard />} />
          </Route>

          <Route element={<UserRoute />}>
            <Route path="/tutor/dashboard" element={<TutorDashboard />} />
            <Route path="/tutor/profile" element={<ViewTutorProfile />} />
          </Route>

          {/* Student Protected Route */}
          <Route element={<StudentRoute />}>
            <Route path="/student/dashboard" element={<StudentDashboard />} />
            <Route path="/student/find-tutor" element={<FindTutor />} />
          </Route>
        </Routes>
      </Router>
    </>
  );
}

export default App;
