import { useNavigate } from "react-router-dom";
import { useAppSelector } from "@/redux/store";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import Navbar from "@/components/Navbar";
import { GetTutorProfile } from "@/api/tutorAPI";

const TutorDashboard = () => {
  const [tutorDetails, setTutorDetails] = useState<TutorDetails | null>(null);

  const { user } = useAppSelector((state) => state.user);
  const navigate = useNavigate();

  const fetchTutorDetails = async (id: string) => {
    try {
      if (!user?.token) {
        toast.error("No token found. Please login again.");
        navigate("/login");
        return;
      }

      const response = await GetTutorProfile(user.token, id);
      setTutorDetails(response.data);
    } catch (error: any) {
      toast.error("Failed to fetch student details");
      console.error(error);
    }
  };

  const handleEdit = () => {
    navigate("/tutor/profile");
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
    fetchTutorDetails(user.id);
  }, [user, navigate]);

  return (
    <div>
      <Navbar />
      <div className="min-h-screen bg-[#f2f2f2] p-6">
        <h1 className="font-bold text-xl mb-5 ">
          Welcome to your Dashboard !{" "}
        </h1>
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
                <h1 className="font-bold text-xl">Tutor Profile</h1>
                {tutorDetails ? (
                  <div className="mt-4 text-left">
                    <p>
                      <strong>Full Name:</strong> {user?.name}
                    </p>
                    <p>
                      <strong>Email:</strong> {user?.email}
                    </p>
                    {/* Edit Button */}
                    <div className="mt-4 text-right">
                      <button
                        onClick={() => handleEdit()} // define handleEdit function
                        className="ml-auto bg-primary text-white px-6 py-2 rounded-md hover:bg-primary/80 transition"
                      >
                        Edit
                      </button>
                    </div>
                  </div>
                ) : (
                  <p>Loading tutor details...</p>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default TutorDashboard;
