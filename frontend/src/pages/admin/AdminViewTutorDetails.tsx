import { useLocation, useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { Tutor } from "@/types/TutorType";
import { toast } from "react-toastify";
import { useAppSelector } from "@/redux/store";
import Navbar from "@/components/Navbar";

const AdminViewTutorDetail = () => {
    const selectedTutor: Tutor | null = useAppSelector(
        (state) => state.tutor.selectedTutor
    );

    console.log("Selected Tutor from Redux:", selectedTutor);

    if (!selectedTutor) {
        return <p>No tutor selected.</p>; // fallback if someone navigates directly
    }

    return (
        <div>
            <Navbar />
            <div className="p-6">
                <h1 className="text-2xl font-bold">{selectedTutor.firstName} {selectedTutor.lastName}</h1>
                <p>Email: {selectedTutor.email}</p>
                <p>Status: {selectedTutor.status}</p>
                <p>Subject: {selectedTutor.subject}</p>
                <p>Hourly Rate: ${selectedTutor.hourlyRate}</p>
                {/* Render any other fields */}
            </div>
        </div>

    );
};

export default AdminViewTutorDetail;
