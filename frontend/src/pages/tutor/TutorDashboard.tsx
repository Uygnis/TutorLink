import { useNavigate } from "react-router-dom";
import { useAppSelector } from "@/redux/store";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import Navbar from "@/components/Navbar";
import { GetTutorProfile } from "@/api/tutorAPI";
import Calendar from "@/components/calendar/Calendar";
import ProfilePicModal from "@/components/ProfilePicModal";
import defaultProfile from "../../assets/default-profile-pic.jpg";
import { Tutor } from "@/types/TutorType";

const TutorDashboard = () => {
  const [tutorDetails, setTutorDetails] = useState<Tutor | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

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
        <h1 className="font-bold text-xl mb-5 ">Welcome to your Dashboard ! </h1>
        {/* Two-column layout */}
        <div className="flex gap-6">
          {/* Left side (Upcoming + Past Sessions) */}
          <div className="flex flex-col w-[70%] space-y-6">
            {/* Upcoming Sessions */}
            <div className="bg-white rounded-md shadow-md p-5">
              <h2 className="font-bold text-lg mb-3">Upcoming Sessions</h2>
              <div>
                <Calendar />
              </div>
              {/* <div className="h-40 flex items-center justify-center text-gray-400">
                No upcoming sessions yet.
              </div> */}
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
                    {/* Profile Picture */}
                    <div className="flex justify-center mb-3">
                      <img
                        src={tutorDetails.profileImageUrl || defaultProfile}
                        alt="Profile"
                        className="w-24 h-24 rounded-full object-cover border"
                      />
                    </div>
                    <p>
                      <strong>Full Name:</strong> {user?.name}
                    </p>
                    <p>
                      <strong>Email:</strong> {user?.email}
                    </p>
                    <p>
                      <strong>Specialization:</strong> {tutorDetails.subject}
                    </p>
                    {/* Edit Button */}
                    <div className="mt-4 flex justify-center">
                      <button
                        onClick={() => handleEdit()} // define handleEdit function
                        className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition">
                        Edit
                      </button>
                      <button
                        onClick={() => setIsModalOpen(true)}
                        className="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition">
                        Change Profile Pic
                      </button>
                    </div>
                    {/* Modal */}
                    <ProfilePicModal
                      isOpen={isModalOpen}
                      onClose={() => setIsModalOpen(false)}
                      refreshProfile={() => fetchTutorDetails(user!.id)}
                      userType="tutor"
                    />
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
