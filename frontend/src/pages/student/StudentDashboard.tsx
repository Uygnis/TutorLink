import { useNavigate } from "react-router-dom";
import { useAppSelector } from "@/redux/store";
import { GetStudentByUserId } from "@/api/studentAPI";
import { GetBookingsForStudent, CancelBooking } from "@/api/bookingAPI";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import Navbar from "@/components/Navbar";
import ProfilePicModal from "@/components/ProfilePicModal";
import BookingCard, { BookingCardProps } from "@/components/BookingCard";
import defaultProfile from "../../assets/default-profile-pic.jpg";
import { BookingResponse } from "@/types/BookingType";

const StudentDashboard = () => {
  const [studentDetails, setStudentDetails] = useState<StudentDetails | null>(null);
  const [bookings, setBookings] = useState<BookingResponse[]>([]);
  const [showOnlyConfirmed, setShowOnlyConfirmed] = useState(false); // NEW STATE
  const [isModalOpen, setIsModalOpen] = useState(false);

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

  const fetchBookings = async (studentId: string) => {
    try {
      if (!user?.token) return;
      const response = await GetBookingsForStudent(studentId, user.token);
      setBookings(response.data);
    } catch (error) {
      toast.error("Failed to fetch bookings");
      console.error(error);
    }
  };

  const handleCancelBooking = async (bookingId: string) => {
    const confirmCancel = window.confirm("Are you sure you want to cancel this session?");
    if (!confirmCancel) return;

    if (!user?.token) return;

    try {
      await CancelBooking(bookingId, user.id, user.token);
      toast.success("Booking cancelled successfully");

      // Update local state to reflect cancelled status
      setBookings((prev) =>
        prev.map((b) => (b.id === bookingId ? { ...b, status: "cancelled" } : b))
      );
    } catch (error: any) {
      toast.error(error?.response?.data?.message || "Failed to cancel the booking");
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
    fetchBookings(user.id);
  }, [user, navigate]);

  const now = new Date();

  // Filter upcoming sessions
  let upcomingBookings = bookings.filter((b) => new Date(`${b.date}T${b.start}`) >= now);

  // Apply filter toggle
  if (showOnlyConfirmed) {
    upcomingBookings = upcomingBookings.filter(
      (b) => b.status !== "pending" && b.status !== "cancelled"
    );
  }

  // Past sessions
  const pastBookings = bookings.filter((b) => new Date(`${b.date}T${b.start}`) < now);

  return (
    <div>
      <Navbar />
      <div className="min-h-screen bg-[#f2f2f2] p-6">
        <h1 className="font-bold text-xl mb-5">Welcome to your Dashboard!</h1>
        <div className="flex gap-6">
          {/* Left side: Upcoming Sessions */}
          <div className="flex flex-col w-[70%] h-[calc(100vh-96px)]">
            <div className="bg-white rounded-md shadow-md p-5 flex-1 overflow-y-auto">
              <div className="flex justify-between items-center mb-3">
                <h2 className="font-bold text-lg">Upcoming Sessions</h2>
                <button
                  onClick={() => setShowOnlyConfirmed((prev) => !prev)}
                  className="px-3 py-1 border rounded-md text-sm bg-primary text-white hover:bg-gray-200 hover:text-black transition">
                  {showOnlyConfirmed ? "Show All" : "Hide Pending and Cancelled"}
                </button>
              </div>

              {upcomingBookings.length === 0 ? (
                <div className="h-40 flex items-center justify-center text-gray-400">
                  No upcoming sessions yet.
                </div>
              ) : (
                <div className="grid grid-cols-1 gap-y-4">
                  {upcomingBookings.map((booking) => (
                    <BookingCard key={booking.id} {...booking} onCancel={handleCancelBooking} />
                  ))}
                </div>
              )}
            </div>
          </div>

          {/* Right side: Profile + Past Sessions */}
          <div className="w-[30%] flex flex-col gap-6">
            <div className="bg-white rounded-md shadow-md p-5">
              <div className="text-center">
                <h1 className="font-bold text-xl">Student Profile</h1>
                {studentDetails ? (
                  <div className="mt-4 text-left">
                    <div className="flex justify-center mb-3">
                      <img
                        src={studentDetails.profileImageUrl || defaultProfile}
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
                      <strong>Student Number:</strong> {studentDetails.studentNumber}
                    </p>
                    <p>
                      <strong>Grade Level:</strong> {studentDetails.gradeLevel}
                    </p>

                    <div className="mt-4 flex justify-center gap-2">
                      <button
                        onClick={() => navigate("/student/profile")}
                        className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition">
                        Edit Profile
                      </button>
                      <button
                        onClick={() => setIsModalOpen(true)}
                        className="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition">
                        Change Profile Pic
                      </button>
                    </div>

                    <ProfilePicModal
                      isOpen={isModalOpen}
                      onClose={() => setIsModalOpen(false)}
                      refreshProfile={() => fetchStudentDetails(user!.id)}
                      userType="student"
                    />
                  </div>
                ) : (
                  <p>Loading student details...</p>
                )}
              </div>
            </div>

            <div className="bg-white rounded-md shadow-md p-5 flex-1 overflow-y-auto">
              <h2 className="font-bold text-lg mb-3">Past Sessions</h2>
              {pastBookings.length === 0 ? (
                <div className="h-40 flex items-center justify-center text-gray-400">
                  No past sessions yet.
                </div>
              ) : (
                <div className="grid grid-cols-1 gap-4">
                  {pastBookings.map((booking) => (
                    <BookingCard key={booking.id} {...booking} isPastSession={true} />
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default StudentDashboard;
