import React from "react";

interface BookingModalAcceptProps {
  booking: {
    studentName: string;
    date: Date;
    slot: { start: string; end: string };
    lessonType: string;
  };
  onClose: () => void;
  onAccept: () => void;
  onReject: () => void;
}

const BookingModalAccept: React.FC<BookingModalAcceptProps> = ({
  booking,
  onClose,
  onAccept,
  onReject,
}) => {
  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40 z-50">
      <div className="bg-white rounded-xl shadow-lg p-6 w-96">
        <h2 className="text-lg font-semibold mb-4 text-gray-800">
          Accept Booking Request
        </h2>

        <p className="mb-1">👤 {booking.studentName}</p>
        <p className="mb-1">
          📅 {booking.date.toDateString()} | ⏰ {booking.slot.start} -{" "}
          {booking.slot.end}
        </p>
        <p className="mb-6">🎓 Lesson Type: {booking.lessonType}</p>

        <div className="flex justify-end gap-3 mb-3">
          <button
            onClick={onReject}
            className="px-4 py-2 rounded-md bg-red-600 text-white hover:bg-red-700 w-1/2"
          >
            Reject
          </button>
          <button
            onClick={onAccept}
            className="px-4 py-2 rounded-md bg-green-600 text-white hover:bg-green-700 w-1/2"
          >
            Accept
          </button>
        </div>

        {/* Cancel below */}
        <div className="flex justify-end">
          <button
            onClick={onClose}
            className="px-4 py-2 rounded-md bg-gray-200 hover:bg-gray-300"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  );
};

export default BookingModalAccept;
