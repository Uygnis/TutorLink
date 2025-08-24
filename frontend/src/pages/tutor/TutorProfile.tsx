import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useDropzone } from "react-dropzone";
import { UpdateTutorProfile, GetTutorProfile } from "@/api/tutorAPI";
import { toast } from "react-toastify";
import { useAppSelector } from "@/redux/store";
import Navbar from "@/components/Navbar";
import AvailabilityPicker from "./availability/AvailabilityPicker";

const ViewTutorProfile = () => {
  const navigate = useNavigate();
  const { user } = useAppSelector((state) => state.user);
  const daysOfWeek = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];

  const defaultAvailability: DayAvailability = daysOfWeek.reduce((acc, day) => {
    acc[day] = { start: "09:00", end: "17:00", enabled: false };
    return acc;
  }, {} as DayAvailability);

  const defaultProfile = {
    userId: "",
    hourlyRate: 0,
    qualifications: [],
    availability: defaultAvailability,
  };

  const [profile, setProfile] = useState<TutorDetails>(defaultProfile);

  useEffect(() => {
    // Fetch tutor profile on load
    const fetchProfile = async () => {
      console.log("called fetchProfile");
      try {
        if (!user?.token) {
          toast.error("No token found. Please login again.");
          navigate("/login");
          return;
        }

        const res = await GetTutorProfile(user.token, user.id);
        const newProfile: TutorDetails = {
          userId: res.data.userId || defaultProfile.userId,
          hourlyRate: res.data.hourlyRate || defaultProfile.hourlyRate,
          qualifications:
            res.data.qualifications || defaultProfile.qualifications, // files handled separately
          availability: res.data.availability || defaultProfile.availability,
        };
        setProfile(newProfile);
        console.log("Fetched profile:", newProfile);
      } catch (err) {
        console.error("Failed to load profile", err);
      }
    };
    fetchProfile();
  }, [user]);

  const onDrop = (acceptedFiles: File[]) => {
    setProfile((prev) => ({
      ...prev,
      qualifications: [...prev.qualifications, ...acceptedFiles],
    }));
  };

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: {
      "application/pdf": [".pdf"],
      "image/*": [".png", ".jpg", ".jpeg"],
    },
  });

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setProfile((prev) => ({
      ...prev,
      [name as keyof TutorDetails]:
        name === "hourlyRate" ? Number(value) : value,
    }));
  };

  const handleSave = async () => {
    if (!user?.token) {
      toast.error("No token found. Please login again.");
      navigate("/login");
      return;
    }
    console.log(profile);
    await UpdateTutorProfile(user.token, profile.userId, profile);
    toast.success("Profile updated successfully");
  };

  const handleCancel = () => {
    navigate(-1); // go back without saving
  };

  return (
    <div>
      <Navbar />
      <div className="min-h-screen bg-gray-100 flex justify-center p-6">
        <div className="bg-white w-[500px] rounded-2xl shadow-lg p-6">
          <h1 className="text-xl font-bold text-primary mb-4">Tutor Profile</h1>

          {/* Form */}
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium">Name</label>
              <input
                type="text"
                name="name"
                value={user?.name}
                onChange={handleChange}
                className="mt-1 w-full border rounded-lg px-3 py-2 focus:ring-2 focus:ring-primary outline-none"
                placeholder="Enter name"
              />
            </div>

            <div>
              <AvailabilityPicker
                value={profile.availability}
                onChange={(newAvailability) =>
                  setProfile((prev) => ({
                    ...prev,
                    availability: newAvailability,
                  }))
                }
              />
            </div>

            <div>
              <label className="block text-sm font-medium">
                Hourly Rate ($)
              </label>
              <input
                type="number"
                name="hourlyRate"
                value={profile.hourlyRate}
                onChange={handleChange}
                className="mt-1 w-full border rounded-lg px-3 py-2 focus:ring-2 focus:ring-primary outline-none"
                placeholder="Enter rate"
              />
            </div>

            {/* <div>
            <label className="block text-sm font-medium">Qualifications</label>
            <textarea
              name="qualifications"
              value={profile.qualifications}
              onChange={handleChange}
              rows={3}
              className="mt-1 w-full border rounded-lg px-3 py-2 focus:ring-2 focus:ring-primary outline-none"
              placeholder="E.g. MSc in Physics, 5 years experience"
            />
          </div> */}

            {/* Qualifications Dropzone */}
            <div>
              <label className="block text-sm font-medium mb-1">
                Qualifications (PDF or Images)
              </label>
              <div
                {...getRootProps()}
                className={`border-2 border-dashed rounded-lg p-6 text-center cursor-pointer transition ${
                  isDragActive
                    ? "border-primary bg-primary/10"
                    : "border-gray-300"
                }`}
              >
                <input {...getInputProps()} />
                {isDragActive ? (
                  <p className="text-primary font-medium">Drop files here...</p>
                ) : (
                  <p className="text-gray-500">
                    Drag & drop files here, or click to select
                  </p>
                )}
              </div>

              {/* Preview of uploaded files */}
              <ul className="mt-2 text-sm text-gray-700">
                {profile.qualifications.map((file, idx) => (
                  <li key={idx}>📄 {file.name}</li>
                ))}
              </ul>
            </div>

            <button
              onClick={handleSave}
              className="w-full rounded-lg bg-primary text-white py-2 transition duration-300 hover:bg-primary/90"
            >
              Save Changes
            </button>
            <button
              onClick={handleCancel}
              className="w-full rounded-lg border border-primary text-primary px-4 py-2 transition duration-300 hover:bg-primary hover:text-white"
            >
              Cancel
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ViewTutorProfile;
