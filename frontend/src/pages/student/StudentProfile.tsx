import { useAppSelector } from "@/redux/store";
import { useState } from "react";
import Navbar from "@/components/Navbar";

const StudentProfile = () => {
  return (
    <div>
      <Navbar />
      <div className="min-h-screen bg-[#f2f2f2] p-6 flex justify-center">
        <div className="bg-white rounded-md shadow-md p-6 w-[500px]">// to do</div>
      </div>
    </div>
  );
};

export default StudentProfile;
