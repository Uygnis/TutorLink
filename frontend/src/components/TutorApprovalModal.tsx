import { useEffect, useState } from "react";

type Props = {
  isOpen: boolean;
  type: "approve" | "reject";
  onConfirm: (reason?: string) => void;
  onCancel: () => void;
};

const TutorApprovalModal = ({ isOpen, type, onConfirm, onCancel }: Props) => {
  const [reason, setReason] = useState("");

  // Reset reason when modal type changes or closes
  useEffect(() => {
    if (!isOpen) setReason("");
  }, [isOpen, type]);

  if (!isOpen) return null;

  const isReject = type === "reject";

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
      <div className="bg-white rounded-2xl shadow-lg w-[90%] max-w-md p-6 animate-fadeIn">
        <h2 className="text-xl font-semibold mb-4 text-gray-800">
          {isReject ? "Reject Tutor" : "Approve Tutor"}
        </h2>

        <p className="text-gray-600 mb-4">
          {isReject
            ? "Please provide a reason for rejecting this tutor."
            : "Are you sure you want to approve this tutor?"}
        </p>

        {isReject && (
          <textarea
            value={reason}
            onChange={(e) => setReason(e.target.value)}
            placeholder="Enter rejection reason..."
            required
            className="w-full border border-gray-300 rounded-lg p-3 mb-4 resize-none focus:ring-2 focus:ring-red-400 focus:outline-none"
            rows={3}
          />
        )}

        <div className="flex justify-end gap-3">
          <button
            onClick={onCancel}
            className="px-4 py-2 rounded-lg bg-gray-200 hover:bg-gray-300 text-gray-700"
          >
            Cancel
          </button>
          <button
            onClick={() => onConfirm(isReject ? reason : undefined)}
            disabled={isReject && reason.trim() === ""}
            className={`px-4 py-2 rounded-lg text-white ${
              isReject
                ? "bg-red-600 hover:bg-red-700 disabled:opacity-50"
                : "bg-green-600 hover:bg-green-700"
            }`}
          >
            {isReject ? "Reject" : "Approve"}
          </button>
        </div>
      </div>
    </div>
  );
};


export default TutorApprovalModal;
