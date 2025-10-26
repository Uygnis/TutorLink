import { useNavigate, useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { useAppDispatch, useAppSelector } from "@/redux/store";
import defaultProfile from "../../assets/default-profile-pic.jpg";
import Navbar from "@/components/Navbar";
import { GetStudentDetails } from "@/api/adminAPI";
import { setLoading } from "@/redux/loaderSlice";

const AdminViewStudentDetails = () => {
  const { studentId } = useParams<{ studentId: string }>();
  const [student, setStudent] = useState<any | null>(null);
  const { user } = useAppSelector((state) => state.user);
  const navigate = useNavigate();
  const { loading } = useAppSelector((state) => state.loaders);
  const dispatch = useAppDispatch();

  useEffect(() => {
    const fetchStudent = async () => {
      if (!studentId || !user?.token) return;

      try {
        dispatch(setLoading(true));
        const res = await GetStudentDetails(studentId, user.token);
        const data = res.data;

        const studentWithDefaults = {
          ...data
        };

        console.log("data", studentWithDefaults);
        setStudent(studentWithDefaults);
      } catch (err) {
        console.error("Failed to fetch student:", err);
      } finally {
        dispatch(setLoading(false));
      }
    };

    fetchStudent();
  }, [studentId, user]);

  if (!loading || student) {
    return (
      <div>
        <Navbar />
        <div className="min-h-screen bg-[#f9f9f9] p-6">
          <button
            onClick={() => navigate(-1)}
            className="mb-4 px-4 py-2 bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300 transition">
            ← Back
          </button>

          {/* Student Profile */}
          <div className="grid grid-cols-1 md:grid-cols-5 gap-6 mb-6">
            <div className="bg-white rounded-lg shadow-md p-6 flex flex-col md:flex-row gap-6 md:col-span-3 max-h-[320px]">
              <img
                src={student?.profileImageUrl || defaultProfile}
                alt={student?.firstName}
                className="w-32 h-32 rounded-full object-cover border shadow"
              />
              <div className="flex-1">
                <h1 className="text-3xl font-bold">
                  {student?.firstName} {student?.lastName}
                </h1>
                <div className="mt-3 flex flex-wrap items-center gap-2">
                  <span className="font-semibold text-gray-700">Status:</span>
                  <span
                    className={`px-3 py-1 rounded-full text-sm font-medium ${
                      student?.status === "ACTIVE"
                        ? "bg-green-100 text-green-800"
                        : "bg-red-100 text-red-800"
                    }`}>
                    {student?.status}</span>
                </div>
                <div className="mt-3 flex flex-wrap items-center gap-2">
                  <span className="font-semibold text-gray-700">Email:</span>
                  <span>
                    {student?.email}</span>
                </div>
              </div>
            </div>

            {/* Qualifications (40%) */}
            <div className="bg-white rounded-lg shadow-md p-6 md:col-span-2 max-h-[380px] overflow-y-auto">
              <h2 className="text-xl font-semibold mb-3">Education Info</h2>
              <div className="mt-3 flex flex-wrap items-center gap-2">
                  <span className="font-semibold text-gray-700">Student Number:</span>
                  <span>
                    {student?.studentNumber}</span>
                </div>
                <div className="mt-3 flex flex-wrap items-center gap-2">
                  <span className="font-semibold text-gray-700">Grade Level:</span>
                  <span>
                    {student?.gradeLevel}</span>
                </div>
            </div>
          </div>

          {/* <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">

            <div className="bg-white rounded-lg shadow-md p-6">
              <h2 className="text-xl font-semibold mb-3">Lesson Types</h2>
              
            </div>

            <div className="bg-white rounded-lg shadow-md p-6 grid grid-cols-3 text-center gap-4">
            </div>
          </div>*/}

          <div className="bg-white rounded-lg shadow-md p-6 mt-6">
            
          </div> 
        </div>
      </div>
    );
  };
}

export default AdminViewStudentDetails;