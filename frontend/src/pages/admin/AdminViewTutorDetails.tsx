import { useNavigate, useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { useAppDispatch, useAppSelector } from "@/redux/store";
import defaultProfile from "../../assets/default-profile-pic.jpg";
import Navbar from "@/components/Navbar";
import { GetTutorDetails } from "@/api/adminAPI";
import AvailabilityCalendar, { TimeSlot } from "@/components/AvailabilityCalendar";
import { GetBookingsForTutorRange } from "@/api/bookingAPI";
import { setLoading } from "@/redux/loaderSlice";

const AdminViewTutorDetail = () => {
  const { tutorId } = useParams<{ tutorId: string }>();
  const [tutor, setTutor] = useState<any | null>(null);
  const { user } = useAppSelector((state) => state.user);
  const [bookedSlots, setBookedSlots] = useState<{ date: string; status: string }[]>([]);
  const [monthStart, setMonthStart] = useState<Date>(() => {
    const today = new Date();
    return new Date(today.getFullYear(), today.getMonth(), 1);
  });
  const navigate = useNavigate();
  const { loading } = useAppSelector((state) => state.loaders);
  const dispatch = useAppDispatch();

  useEffect(() => {
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
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
          rating: data.rating ?? "N/A",
          studentsCount: data.studentsCount ?? 0,
          lessonsCount: data.lessonsCount ?? 0,
          lessonType: data.lessonType ?? ["N/A"],
          reviews: data.reviews ?? [],
        };

        console.log("data", tutorWithDefaults);
        setTutor(tutorWithDefaults);
      } catch (err) {
        console.error("Failed to fetch tutor:", err);
      } finally {
        dispatch(setLoading(false));
      }
    };

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

  if (!loading || tutor) {
    return (
      <div>
        <Navbar />
        <div className="min-h-screen bg-[#f9f9f9] p-6">
          <button
            onClick={() => navigate(-1)}
            className="mb-4 px-4 py-2 bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300 transition">
            ← Back
          </button>

          {/* Tutor Profile + Qualifications */}
          <div className="grid grid-cols-1 md:grid-cols-5 gap-6 mb-6">
            {/* Tutor Profile (60%) */}
            <div className="bg-white rounded-lg shadow-md p-6 flex flex-col md:flex-row gap-6 md:col-span-3 max-h-[380px]">
              <img
                src={tutor?.profileImageUrl || defaultProfile}
                alt={tutor?.firstName}
                className="w-32 h-32 rounded-full object-cover border shadow"
              />
              <div className="flex-1">
                <h1 className="text-3xl font-bold">
                  {tutor?.firstName} {tutor?.lastName}
                </h1>
                {/* Truncate description with ellipsis */}
                <p className="text-gray-600 mt-3 line-clamp-6">{tutor?.description}</p>

                {/* Subjects with Badge Style */}
                <div className="mt-3 flex flex-wrap items-start gap-2 overflow-hidden">
                  <span className="font-semibold text-gray-700 whitespace-nowrap">Subject:</span>
                  <div className="flex flex-wrap gap-2 break-words max-w-full">
                    {tutor?.subject?.split(",").map((sub: string, idx: number) => (
                      <span
                        key={idx}
                        className="bg-blue-100 text-blue-800 text-sm font-semibold px-2 py-1 rounded-full break-words whitespace-normal max-w-[100%]">
                        {sub.trim()}
                      </span>
                    ))}
                  </div>
                </div>

                <div className="mt-3 flex flex-wrap items-center gap-2">
                  <span className="font-semibold text-gray-700">Status:</span>
                  <span
                    className={`px-3 py-1 rounded-full text-sm font-medium ${
                      tutor?.status === "ACTIVE"
                        ? "bg-green-100 text-green-800"
                        : "bg-red-100 text-red-800"
                    }`}>
                    {tutor?.status}
                  </span>
                </div>
                <p className="mt-3 mb-4 text-primary font-bold text-xl">
                  SGD {tutor?.hourlyRate}/hr
                </p>
              </div>
            </div>

            {/* Qualifications (40%) */}
            <div className="bg-white rounded-lg shadow-md p-6 md:col-span-2 max-h-[380px] overflow-y-auto">
              <h2 className="text-xl font-semibold mb-3">Qualifications</h2>
              {tutor?.qualifications && tutor?.qualifications.length > 0 ? (
                <ul className="space-y-3">
                  {tutor?.qualifications.map((q: any, idx: number) => (
                    <li
                      key={idx}
                      className="border rounded-lg p-3 flex justify-between items-center">
                      <div>
                        <p className="font-semibold">{q.name}</p>
                        <p className="text-gray-500 text-sm">{q.type}</p>
                        {q.uploadedAt && (
                          <p className="text-xs text-gray-400">
                            Uploaded: {new Date(q.uploadedAt).toLocaleDateString()}
                          </p>
                        )}
                      </div>
                      <a
                        href={q.path}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="text-blue-600 hover:underline text-sm">
                        View
                      </a>
                    </li>
                  ))}
                </ul>
              ) : (
                <p className="text-gray-500">No qualifications uploaded.</p>
              )}
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
            {/* Lesson Types */}
            <div className="bg-white rounded-lg shadow-md p-6">
              <h2 className="text-xl font-semibold mb-3">Lesson Types</h2>
              <ul className="list-disc pl-5 text-gray-700">
                {tutor?.lessonType.map((type: string, idx: number) => (
                  <li key={idx}>{type}</li>
                ))}
              </ul>
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
          </div>

          {/* Monthly Availability Calendar */}
          <div className="bg-white rounded-lg shadow-md mb-6 p-6">
            <h2 className="text-xl font-semibold mb-3">Tutor Availability</h2>
            <AvailabilityCalendar
              role="student"
              availability={tutor?.availability}
              bookedSlots={bookedSlots}
              initialMonth={monthStart}
              onMonthChange={(newMonth) => setMonthStart(newMonth)}
            />
          </div>

          {/* Reviews */}
          <div className="bg-white rounded-lg shadow-md p-6 mt-6">
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
      </div>
    );
  }
};

export default AdminViewTutorDetail;
