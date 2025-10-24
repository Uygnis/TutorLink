import { useNavigate, useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { useAppDispatch, useAppSelector } from "@/redux/store";
import defaultProfile from "../../assets/default-profile-pic.jpg";
import Navbar from "@/components/Navbar";
import { ApproveTutor, GetTutorDetails, RejectTutor } from "@/api/adminAPI";
import AvailabilityCalendar, { TimeSlot } from "@/components/AvailabilityCalendar";
import { GetBookingsForTutorRange } from "@/api/bookingAPI";
import { setLoading } from "@/redux/loaderSlice";
import { toast } from "react-toastify";
import { Tutor } from "@/types/TutorType";
import TutorApprovalModal from "@/components/TutorApprovalModal";
import TutorProfileSummary from "@/components/TutorProfileSummary";

const AdminViewTutorDetail = () => {
  const { tutorId } = useParams<{ tutorId: string }>();
  const [tutor, setTutor] = useState<any | null>(null);
  const [stagingProfile, setStagingProfile] = useState<any>(null);
  const { user } = useAppSelector((state) => state.user);
  const [bookedSlots, setBookedSlots] = useState<{ date: string; status: string }[]>([]);
  const [monthStart, setMonthStart] = useState<Date>(() => {
    const today = new Date();
    return new Date(today.getFullYear(), today.getMonth(), 1);
  });
  const [modalOpen, setModalOpen] = useState(false);
  const [modalType, setModalType] = useState<"approve" | "reject" | null>(null);

  const navigate = useNavigate();
  const { loading } = useAppSelector((state) => state.loaders);
  const dispatch = useAppDispatch();

  const fetchTutor = async () => {
    if (!tutorId || !user?.token) return;

    try {
      dispatch(setLoading(true));
      const res = await GetTutorDetails(tutorId, user.token);
      const data = res.data;

      const tutorWithDefaults = {
        ...data,
        description:
          data.description ||
          "No Description has been provided.",
        rating: data.rating ?? "N/A",
        studentsCount: data.studentsCount ?? 0,
        lessonsCount: data.lessonsCount ?? 0,
        lessonType: data.lessonType ?? ["N/A"],
        hourlyRate: data.hourlyRate ?? ["??"],
        reviews: data.reviews ?? [],
        rejectedReason: data.rejectedReason || "",
      };

      setTutor(tutorWithDefaults);
      setStagingProfile(data.stagedProfile || null);
    } catch (err) {
      console.error("Failed to fetch tutor:", err);
    } finally {
      dispatch(setLoading(false));
    }
  };

  // Fetch tutor when component mounts
  useEffect(() => {
    if (!tutorId || !user?.token) return;
    fetchTutor();
  }, [tutorId, user]);

  // Fetch bookings for the month
  useEffect(() => {
    const fetchBookingsForMonth = async () => {
      if (!tutorId || !user?.token) return;

      const year = monthStart.getFullYear();
      const month = monthStart.getMonth();

      const firstDay = new Date(year, month, 1).toISOString().split("T")[0];
      const lastDay = new Date(year, month + 1, 0).toISOString().split("T")[0];

      try {
        const res = await GetBookingsForTutorRange(tutorId, firstDay, lastDay, user.token!);

        const mappedSlots = res.data.map((b: any) => ({
          date: b.date,
          status: b.status ?? "booked", // required by AvailabilityCalendar
        }));

        setBookedSlots(mappedSlots);
        console.log("bookedSlots", mappedSlots);
      } catch (err) {
        console.error("Failed to fetch bookings:", err);
      }
    };

    fetchBookingsForMonth();
  }, [monthStart, tutorId, user]);

  const openApproveModal = () => {
    setModalType("approve");
    setModalOpen(true);
  };

  const openRejectModal = () => {
    setModalType("reject");
    setModalOpen(true);
  };

  const handleModalConfirm = async (reason?: string) => {
    if (!tutor) return;
    setModalOpen(false);

    if (modalType === "approve") {
      await handleApproveTutor(tutor);
    } else if (modalType === "reject") {
      await handleRejectTutor(tutor, reason);
    }

    setModalType(null);
  };

  const handleApproveTutor = async (tutor: Tutor) => {
    try {
      const token = user?.token;
      if (!token) return;

      await ApproveTutor(user.id, tutor.userId, token);
      toast.success("Tutor approved successfully");
      fetchTutor();
    } catch (error: any) {
      toast.error("Failed to approve tutor");
      console.error(error);
    }
  };

  const handleRejectTutor = async (tutor: Tutor, reason?: string) => {
    try {
      const token = user?.token;
      if (!token) return;

      await RejectTutor(user.id, tutor.userId, token, reason);
      toast.success("Tutor rejected successfully");
      fetchTutor();
    } catch (error: any) {
      toast.error("Failed to reject tutor");
      console.error(error);
    }
  };

  if (!loading || tutor) {
    return (
      <div>
        <Navbar />
        <div className="min-h-screen bg-[#f9f9f9] p-6">
          <div className="flex justify-between items-center mb-6">
            <button
              onClick={() => navigate("/admin/tutors")}
              className="px-4 py-2 bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300 transition"
            >
              ← Back
            </button>

            {tutor?.rejectedReason && (
              <div className="absolute left-1/2 transform -translate-x-1/2 bg-red-600 text-white px-6 py-3 rounded-lg shadow-md max-w-xl text-center">
                <p className="text-sm font-medium leading-snug">
                  Profile submission was rejected due to the following reason:
                </p>
                <p className="mt-1 text-sm font-semibold">{tutor.rejectedReason}</p>
              </div>
            )}

            {tutor?.status === "PENDING_APPROVAL" && (
              <div className="flex gap-3">
                <button
                  onClick={openApproveModal}
                  className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition"
                >
                  Approve
                </button>
                <button
                  onClick={openRejectModal}
                  className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition"
                >
                  Reject
                </button>
              </div>
            )}
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
            <div className="bg-white rounded-lg shadow-md p-6 grid grid-cols-3 text-left gap-4">
              <img
                src={tutor?.profileImageUrl || defaultProfile}
                alt={tutor?.firstName}
                className="w-32 h-32 rounded-full object-cover border shadow"
              />
              <div className="flex-1">
                <h1 className="text-3xl font-bold">
                  {tutor?.firstName} {tutor?.lastName}
                </h1>
              </div>
            </div>
            {/* Stats */}
            <div className="bg-white rounded-lg shadow-md p-6 grid grid-cols-3 text-center gap-4">
              <div className="flex flex-col items-center justify-center">
                <p className="text-2xl font-bold">{tutor?.rating} ⭐</p>
                <p className="text-gray-500">Rating</p>
              </div>
              <div className="flex flex-col items-center justify-center">
                <p className="text-2xl font-bold">{tutor?.studentsCount}</p>
                <p className="text-gray-500">Students</p>
              </div>
              <div className="flex flex-col items-center justify-center">
                <p className="text-2xl font-bold">{tutor?.lessonsCount}</p>
                <p className="text-gray-500">Lessons</p>
              </div>
            </div>
            <div className="bg-white rounded-lg shadow-md p-6 grid grid-cols-3 text-center gap-4">
              <h2 className="text-xl font-semibold mb-3">Student Reviews</h2>
              {tutor?.reviews.length > 0 ? (
                <div className="space-y-4">
                  {tutor?.reviews.map((review: any, idx: number) => (
                    <div key={idx} className="border-b pb-3">
                      <p className="font-semibold">{review.studentName}</p>
                      <p className="text-yellow-500">{review.rating} ⭐</p>
                      <p className="text-gray-700">{review.comment}</p>
                    </div>
                  ))}
                </div>
              ) : (
                <p className="text-gray-500">No reviews yet.</p>
              )}
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mt-6">
            {/* Current Active Profile */}
            <div
              className={`grid grid-cols-1 bg-white rounded-lg shadow-md p-6 ${!stagingProfile ? "md:col-span-2" : ""
                }`}
            >
              <h2 className="text-xl font-semibold text-green-700 mb-3">
                Current Live Profile
              </h2>
              <TutorProfileSummary profile={tutor} bookedSlots={bookedSlots} />
            </div>

            {/* Staging / Pending Profile */}
            {stagingProfile && (
              <div
                className={`grid grid-cols-1 rounded-lg shadow-md p-6 
                ${tutor?.rejectedReason
                    ? "bg-pink-100 border border-pink-200"
                    : "bg-yellow-50 border border-yellow-200"
                  }`}
              >
                <h2
                  className={`text-xl font-semibold mb-3 ${tutor?.rejectedReason ? "text-pink-700" : "text-yellow-700"}`}
                >
                  {tutor?.rejectedReason
                    ? "Rejected Profile"
                    : "Pending Updated Profile"}
                </h2>
                <TutorProfileSummary profile={stagingProfile} bookedSlots={bookedSlots} />
              </div>

            )}
          </div>

        </div>
        <TutorApprovalModal
          isOpen={modalOpen}
          type={modalType ?? "approve"}
          onCancel={() => setModalOpen(false)}
          onConfirm={handleModalConfirm}
        />
      </div>
    );
  }
};

export default AdminViewTutorDetail;
