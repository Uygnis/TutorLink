import { useNavigate } from "react-router-dom";
import { useAppSelector } from "@/redux/store";
import { GetStudentByUserId } from "@/api/studentAPI";
import { GetBookingsForStudent, CancelBooking } from "@/api/bookingAPI";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import Navbar from "@/components/Navbar";
import ProfilePicModal from "@/components/ProfilePicModal";
import BookingCard from "@/components/BookingCard";
import RescheduleModal from "@/components/RescheduleModal";
import defaultProfile from "../../assets/default-profile-pic.jpg";
import { BookingResponse } from "@/types/BookingType";
import { GetWalletByUserId } from "@/api/walletAPI";

const StudentDashboard = () => {
  const { user } = useAppSelector((state) => state.user);
  const navigate = useNavigate();

  const [studentDetails, setStudentDetails] = useState<StudentDetails | null>(null);
  const [bookings, setBookings] = useState<BookingResponse[]>([]);
  const [walletBalance, setWalletBalance] = useState<number>(0);
  const [loadingWallet, setLoadingWallet] = useState<boolean>(true);
  const [showOnlyConfirmed, setShowOnlyConfirmed] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [rescheduleBooking, setRescheduleBooking] = useState<{
    bookingId: string;
    tutorId: string;
  } | null>(null);

  // -------------------------
  // Fetch wallet
  // -------------------------
  const fetchWallet = async (studentId: string) => {
    if (!user?.token) return;
    try {
      const res = await GetWalletByUserId(studentId, user.token);
      setWalletBalance(res.data.balance ?? 0);
    } catch (err) {
      console.error("Failed to fetch wallet:", err);
      toast.error("Failed to fetch wallet balance");
    } finally {
      setLoadingWallet(false);
    }
  };

  // -------------------------
  // Fetch student details
  // -------------------------
  const fetchStudentDetails = async (id: string) => {
    if (!user?.token) return;
    try {
      const res = await GetStudentByUserId(id, user.token);
      setStudentDetails(res.data);
    } catch (err) {
      toast.error("Failed to fetch student details");
      console.error(err);
    }
  };

  // -------------------------
  // Fetch bookings
  // -------------------------
  const fetchBookings = async (studentId: string) => {
    if (!user?.token) return;
    try {
      const res = await GetBookingsForStudent(studentId, user.token);
      setBookings(res.data);
    } catch (err) {
      toast.error("Failed to fetch bookings");
      console.error(err);
    }
  };

  // -------------------------
  // Cancel booking
  // -------------------------
  const handleCancelBooking = async (bookingId: string) => {
    if (!user?.token) return;
    const confirmCancel = window.confirm("Are you sure you want to cancel this session?");
    if (!confirmCancel) return;

    try {
      await CancelBooking(bookingId, user.id, user.token);
      toast.success("Booking cancelled successfully");

      // 🔁 Re-fetch updated bookings and wallet
      await Promise.all([fetchBookings(user.id), fetchWallet(user.id)]);
    } catch (err: any) {
      toast.error(err?.response?.data?.message || "Failed to cancel booking");
      console.error(err);
    }
  };

  // -------------------------
  // Open reschedule modal
  // -------------------------
  const handleReschedule = (bookingId: string, tutorId: string) => {
    setRescheduleBooking({ bookingId, tutorId });
  };

  // -------------------------
  // On mount
  // -------------------------
  useEffect(() => {
    if (!user?.id || !user?.token) {
      toast.error("User not logged in");
      navigate("/login");
      return;
    }
    fetchStudentDetails(user.id);
    fetchBookings(user.id);
    fetchWallet(user.id);
  }, [user, navigate]);

  const now = new Date();

  // Filter upcoming sessions
  let upcomingBookings = bookings.filter((b) => new Date(`${b.date}T${b.start}`) >= now);
  console.log("Upcoming bookings", upcomingBookings);
  if (showOnlyConfirmed) {
    upcomingBookings = upcomingBookings.filter((b) => b.status === "confirmed");
  }

  // Past sessions
  const pastBookings = bookings.filter((b) => new Date(`${b.date}T${b.start}`) < now);

  return (
    <div>
      <Navbar />
      <div className="min-h-screen bg-[#f2f2f2] p-6">
        <div className="flex justify-between items-center mb-5">
          <div className="flex items-center gap-4">
            <h1 className="font-bold text-xl">Welcome to your Dashboard!</h1>

            {/* ✅ Wallet Balance */}
            {!loadingWallet ? (
              <div className="flex items-center bg-green-100 text-green-700 px-3 py-1 rounded-full text-md font-semibold">
                SGD {walletBalance.toFixed(2)}
              </div>
            ) : (
              <div className="text-gray-400 text-sm">Loading wallet...</div>
            )}
          </div>

          <button
            onClick={() => navigate("/student/wallet")}
            className="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 transition">
            Go to Wallet
          </button>
        </div>

        <div className="flex gap-6">
          {/* Left: Upcoming */}
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
                  {upcomingBookings.map((b) => (
                    <BookingCard
                      key={b.id}
                      {...b}
                      onCancel={handleCancelBooking}
                      onReschedule={handleReschedule}
                    />
                  ))}
                </div>
              )}
            </div>
          </div>

          {/* Right: Profile + Past */}
          <div className="w-[30%] flex flex-col gap-6">
            {/* Profile */}
            <div className="bg-white rounded-md shadow-md p-5 text-center">
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
                    <strong>Full Name:</strong> {user?.name || "N/A"}
                  </p>
                  <p>
                    <strong>Email:</strong> {user?.email || "N/A"}
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
                    refreshProfile={() => {
                      if (user?.id) fetchStudentDetails(user.id);
                    }}
                    userType="student"
                  />
                </div>
              ) : (
                <p>Loading student details...</p>
              )}
            </div>

            {/* Past Sessions */}
            <div className="bg-white rounded-md shadow-md p-5 flex-1 overflow-y-auto">
              <h2 className="font-bold text-lg mb-3">Past Sessions</h2>
              {pastBookings.length === 0 ? (
                <div className="h-40 flex items-center justify-center text-gray-400">
                  No past sessions yet.
                </div>
              ) : (
                <div className="grid grid-cols-1 gap-4">
                  {pastBookings.map((b) => (
                    <BookingCard key={b.id} {...b} isPastSession />
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* Reschedule Modal */}
      {rescheduleBooking && (
        <RescheduleModal
          bookingId={rescheduleBooking.bookingId}
          tutorId={rescheduleBooking.tutorId}
          onClose={() => setRescheduleBooking(null)}
          onRescheduleConfirmed={() => {
            setRescheduleBooking(null);
            if (user?.id) fetchBookings(user.id);
          }}
        />
      )}
    </div>
  );
};

export default StudentDashboard;
