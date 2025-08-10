import { useNavigate } from "react-router-dom";
import { useAppDispatch, useAppSelector } from "@/redux/store";
import { setUser } from "@/redux/userSlice";
import { GetStudentByUserId } from "@/api/studentAPI";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";

const StudentDashboard = () => {
  const [studentDetails, setStudentDetails] = useState<StudentDetails | null>(null);

  const { user } = useAppSelector((state) => state.user);
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    dispatch(setUser(null));
    navigate("/login");
  };

  const fetchStudentDetails = async (id: string) => {
    try {
      if (!user?.token) {
        toast.error("No token found. Please login again.");
        navigate("/login");
        return;
      }

      const response = await GetStudentByUserId(id, user.token);
      setStudentDetails(response.data);
    } catch (error: any) {
      toast.error("Failed to fetch student details");
      console.error(error);
    }
  };

  useEffect(() => {
    if (!user) {
      navigate("/login");
      return;
    }
    if (!user.id) {
      toast.error("User ID missing. Please login again.");
      navigate("/login");
      return;
    }
    fetchStudentDetails(user.id);
  }, [user, navigate]);

  return (
    <div className="h-screen bg-primary flex items-center justify-center p-5 overflow-hidden">
      <div className="flex flex-col items-center ">
        <div className="bg-white h-full w-[400px] rounded-md p-5">
          <div className="p-5 text-center">
            <h1 className="font-bold text-xl">Welcome, Student Dashboard</h1>
            {studentDetails ? (
              <div className="mt-4 text-left">
                <p>
                  <strong>Full Name:</strong> {user?.name}
                </p>
                <p>
                  <strong>Email:</strong> {user?.email}
                </p>
                <p>
                  <strong>Student Number:</strong> {studentDetails.studentNumber}
                </p>
                <p>
                  <strong>Grade Level:</strong> {studentDetails.gradeLevel}
                </p>
              </div>
            ) : (
              <p>Loading student details...</p>
            )}
          </div>
          <button
            onClick={handleLogout}
            className="mt-3 rounded-lg bg-primary text-white w-full px-20 py-2 transition duration-500 hover:bg-gray-200 hover:text-primary">
            Logout
          </button>
        </div>
      </div>
    </div>
  );
};

export default StudentDashboard;
