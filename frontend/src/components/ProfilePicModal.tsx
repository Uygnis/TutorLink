import { useState } from "react";
import { useAppSelector } from "@/redux/store";
import { toast } from "react-toastify";
import { UploadProfilePicture } from "@/api/studentAPI";
import { UploadTutorProfilePicture } from "@/api/tutorAPI";

interface ProfilePicModalProps {
  isOpen: boolean;
  onClose: () => void;
  refreshProfile: () => void;
  userType: "student" | "tutor";
}

const ProfilePicModal = ({ isOpen, onClose, refreshProfile, userType }: ProfilePicModalProps) => {
  const { user } = useAppSelector((state) => state.user);
  const [file, setFile] = useState<File | null>(null);
  const [preview, setPreview] = useState<string | null>(null);

  if (!isOpen) return null;

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      setFile(e.target.files[0]);
      setPreview(URL.createObjectURL(e.target.files[0]));
    }
  };

  const handleUpload = async () => {
    if (!file || !user?.id || !user?.token) {
      toast.error("Select a file or login first");
      return;
    }

    try {
      if (userType === "student") {
        await UploadProfilePicture(user.id, file, user.token);
      } else {
        await UploadTutorProfilePicture(user.id, file, user.token);
      }

      toast.success("Profile picture updated!");
      onClose();
      refreshProfile();
    } catch (err) {
      console.error(err);
      toast.error("Failed to upload profile picture");
    }
  };
  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-md shadow-md p-6 w-[400px] text-center relative">
        <button
          onClick={onClose}
          className="absolute top-2 right-2 text-gray-500 hover:text-gray-700">
          ✕
        </button>

        <h2 className="text-lg font-bold mb-4">Update Profile Picture</h2>

        {preview ? (
          <img
            src={preview}
            alt="Preview"
            className="w-32 h-32 rounded-full object-cover mb-4 border mx-auto"
          />
        ) : (
          <div className="w-32 h-32 rounded-full bg-gray-200 mb-4 flex items-center justify-center border mx-auto">
            No Preview
          </div>
        )}

        <input type="file" accept="image/*" onChange={handleFileChange} className="mb-4" />

        <div className="flex justify-center gap-2">
          <button
            onClick={handleUpload}
            className="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition">
            Upload
          </button>
          <button
            onClick={onClose}
            className="px-4 py-2 bg-gray-400 text-white rounded-md hover:bg-gray-500 transition">
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
};

export default ProfilePicModal;
