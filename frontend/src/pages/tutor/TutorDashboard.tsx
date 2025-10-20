import { useNavigate } from "react-router-dom";
import { useAppSelector } from "@/redux/store";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import Navbar from "@/components/Navbar";
import { GetTutorProfile } from "@/api/tutorAPI";
import ProfilePicModal from "@/components/ProfilePicModal";
import defaultProfile from "../../assets/default-profile-pic.jpg";
import { Tutor } from "@/types/TutorType";
import AvailabilityCalendar, { TimeSlot } from "@/components/AvailabilityCalendar";
import { AcceptBooking, CancelBooking, GetBookingsForTutorRange } from "@/api/bookingAPI";
import { BookingRequest } from "@/types/BookingType";
import BookingModalAccept from "@/components/BookingModalAccept";
import BookingModalView from "@/components/BookingModalView";

const TutorDashboard = () => {
  const [tutorDetails, setTutorDetails] = useState<Tutor | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const [selectedSlot, setSelectedSlot] = useState<{
    date: Date;
    slot: TimeSlot;
  } | null>(null);

  const [showModal, setShowModal] = useState(false);
  const [bookedSlots, setBookedSlots] = useState<
    {
      date: string;
      status: string;
      id: string;
      tutorId: string;
      studentId: string;
      lessonType: string;
      start: string;
    }[]
  >([]);
  const [monthStart, setMonthStart] = useState<Date>(() => {
    const today = new Date();
    return new Date(today.getFullYear(), today.getMonth(), 1);
  });

  const { user } = useAppSelector((state) => state.user);
  const navigate = useNavigate();

  const fetchTutorDetails = async (id: string): Promise<Tutor | null> => {
    try {
      if (!user?.token) {
        toast.error("No token found. Please login again.");
        navigate("/login");
        return null;
      }

      const response = await GetTutorProfile(user.token, id);
      if (response.data) {
        setTutorDetails(response.data);
        return response.data;
      } else {
        return null;
      }
    } catch (error: any) {
      toast.error("Failed to fetch student details");
      console.error(error);
      return null;
    }
  };
  const fetchBookingsForMonth = async (id: string) => {
    if (!id || !user?.token) return;

    const year = monthStart.getFullYear();
    const month = monthStart.getMonth();

    // first and last day as YYYY-MM-DD strings
    const firstDay = `${year}-${String(month + 1).padStart(2, "0")}-01`;
    const lastDay = `${year}-${String(month + 1).padStart(2, "0")}-${String(
      new Date(year, month + 1, 0).getDate()
    ).padStart(2, "0")}`;

    try {
      const res = await GetBookingsForTutorRange(user.id, firstDay, lastDay, user.token!);
      setBookedSlots(
        res.data.map((b: any) => ({
          date: b.date,
          status: b.status,
          lessonType: b.lessonType,
          id: b.id,
          studentId: b.studentId,
          tutorId: b.tutorId,
          start: b.start,
        }))
      );
      console.log("dates", res.data);
    } catch (err) {
      console.error("Failed to fetch bookings:", err);
    }
  };

  const handleEdit = () => {
    navigate("/tutor/profile");
  };
  const handleSlotClick = (date: Date, slot: TimeSlot) => {
    setSelectedSlot({ date, slot });
    console.log({ date, slot });
    setShowModal(true);
  };

  const modal = (data: { date: Date; slot: TimeSlot }) => {
    const booking = bookedSlots.filter(
      (item) => item.date === data.date.toLocaleDateString("en-CA") && item.status !== "cancelled"
    )[0];
    return booking.status === "pending" ? (
      <BookingModalAccept
        booking={{
          studentName: booking.studentId,
          date: data.date,
          slot: data.slot,
          lessonType: booking.lessonType,
        }}
        onClose={() => setShowModal(false)}
        onAccept={() => confirmBooking(booking.id)}
        onReject={() => cancelBooking(booking.id)}
      />
    ) : (
      <BookingModalView
        booking={{
          studentName: booking.studentId,
          tutorName: booking.tutorId,
          date: data.date,
          slot: data.slot,
          lessonType: booking.lessonType,
        }}
        onClose={() => setShowModal(false)}
      />
    );
  };

  const cancelBooking = async (bookingId: string) => {
    if (!user?.token || !user?.id || !selectedSlot) {
      alert("You must be logged in to book a lesson.");
      return;
    }

    const dateStr = selectedSlot.date.toLocaleDateString("en-CA"); // YYYY-MM-DD

    try {
      await CancelBooking(bookingId, user.id, user.token);
      setBookedSlots((prev) =>
        prev.map((b) => (b.id === bookingId ? { ...b, status: "cancelled" } : b))
      );
      alert(
        `✅ Booking rejected on ${dateStr} | ${selectedSlot.slot.start} - ${selectedSlot.slot.end}`
      );
    } catch (err) {
      console.error("Booking failed:", err);
      alert("❌ Failed to reject booking. Please try again.");
    } finally {
      setShowModal(false);
      setSelectedSlot(null);
    }
  };

  const confirmBooking = async (bookingId: string) => {
    if (!user?.token || !user?.id || !selectedSlot) {
      alert("You must be logged in to book a lesson.");
      return;
    }

    const dateStr = selectedSlot.date.toLocaleDateString("en-CA"); // YYYY-MM-DD

    try {
      await AcceptBooking(bookingId, user.token);
      setBookedSlots((prev) =>
        prev.map((b) => (b.id === bookingId ? { ...b, status: "confirmed" } : b))
      );
      alert(
        `✅ Booking accepted on ${dateStr} | ${selectedSlot.slot.start} - ${selectedSlot.slot.end}`
      );
    } catch (err) {
      console.error("Booking failed:", err);
      alert("❌ Failed to create booking. Please try again.");
    } finally {
      setShowModal(false);
      setSelectedSlot(null);
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
    fetchTutorDetails(user.id).then((data) => {
      if (data) {
        return fetchBookingsForMonth(data.id);
      } else {
        toast.error("Failed to load tutor data.");
      }
    });
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
                {tutorDetails !== null ? (
                  <>
                    {/* Monthly Availability Calendar */}
                    <AvailabilityCalendar
                      role="tutor"
                      availability={tutorDetails.availability}
                      bookedSlots={bookedSlots}
                      initialMonth={monthStart}
                      onSlotClick={handleSlotClick}
                      onMonthChange={(newMonth) => setMonthStart(newMonth)}
                    />

                    {showModal && selectedSlot && modal(selectedSlot)}
                  </>
                ) : (
                  <></>
                )}
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
