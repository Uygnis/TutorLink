import { useNavigate } from "react-router-dom";
import { useAppSelector } from "@/redux/store";
import { GetStudentByUserId } from "@/api/studentAPI";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import Navbar from "@/components/Navbar";

const StudentDashboard = () => {
  const [studentDetails, setStudentDetails] = useState<StudentDetails | null>(null);

  const { user } = useAppSelector((state) => state.user);
  const navigate = useNavigate();

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
    <div>
      <Navbar />
      <div className="min-h-screen bg-[#f2f2f2] p-6">
        <h1 className="font-bold text-xl mb-5 ">Welcome to your Dashboard ! </h1>
        {/* Two-column layout */}
        <div className="flex gap-6">
          {/* Left side (Upcoming + Past Sessions) */}
          <div className="flex flex-col w-[70%] space-y-6">
            {/* Upcoming Sessions */}
            <div className="bg-white rounded-md shadow-md p-5">
              <h2 className="font-bold text-lg mb-3">Upcoming Sessions</h2>
              <div className="h-40 flex items-center justify-center text-gray-400">
                No upcoming sessions yet.
              </div>
            </div>

            {/* Past Sessions */}
            <div className="bg-white rounded-md shadow-md p-5">
              <h2 className="font-bold text-lg mb-3">Past Sessions</h2>
              <div className="h-40 flex items-center justify-center text-gray-400">
                No past sessions yet.
              </div>
            </div>
          </div>

          {/* Right side (Student Profile Card) */}
          <div className="w-[30%]">
            <div className="bg-white rounded-md shadow-md p-5">
              <div className="text-center">
                <h1 className="font-bold text-xl">Student Profile</h1>
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
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default StudentDashboard;
