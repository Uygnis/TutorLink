import { useLocation, useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { Tutor } from "@/types/TutorType";
import { toast } from "react-toastify";
import { useAppSelector } from "@/redux/store";
import Navbar from "@/components/Navbar";
import { GetTutorDetails } from "@/api/adminAPI";

const AdminViewTutorDetail = () => {
    const { tutorId } = useParams<{ tutorId: string }>();
    const [tutor, setTutor] = useState<Tutor | null>(null);
    const { user } = useAppSelector((state) => state.user);
  
    useEffect(() => {
      if (!tutorId || !user?.token) return;
  
      const fetchTutor = async () => {
        try {
          const token = user?.token;
          if (!token) return;
          const response = await GetTutorDetails(tutorId, token);
          setTutor(response.data);
          console.log(response.data);
        } catch (err) {
          toast.error("Failed to load tutor details");
          console.error(err);
        }
      };
  
      fetchTutor();
    }, [tutorId, user]);
  
    if (!tutor) return <p>Loading tutor details...</p>;
  
    return (
      <div>
        <Navbar />
        <div className="p-6">
          <h1 className="text-2xl font-bold">
            {tutor.firstName} {tutor.lastName}
          </h1>
          <p>Email: {tutor.email}</p>
          <p>Status: {tutor.status}</p>
          <p>Subject: {tutor.subject}</p>
          <p>Hourly Rate: ${tutor.hourlyRate}</p>
          <button
            onClick={() => window.history.back()}
            className="bg-blue-100 text-blue-700 px-3 py-1 rounded-md hover:bg-blue-200"
          >
            Back
          </button>
        </div>
      </div>
    );
  };
  
  export default AdminViewTutorDetail;