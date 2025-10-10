import React from "react";

interface BookingModalViewProps {
  booking: {
    studentName: string;
    tutorName: string;
    date: Date;
    slot: { start: string; end: string };
    lessonType: string;
  };
  onClose: () => void;
}

const BookingModalView: React.FC<BookingModalViewProps> = ({
  booking,
  onClose,
}) => {
  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40 z-50">
      <div className="bg-white rounded-xl shadow-lg p-6 w-96">
        <h2 className="text-lg font-semibold mb-4 text-gray-800">
          Booking Details
        </h2>

        {booking.studentName && (
          <p className="mb-1">👤 Student: {booking.studentName}</p>
        )}
        {booking.tutorName && (
          <p className="mb-1">👨‍🏫 Tutor: {booking.tutorName}</p>
        )}

        <p className="mb-1">
          📅 {booking.date.toDateString()} | ⏰ {booking.slot.start} -{" "}
          {booking.slot.end}
        </p>
        <p className="mb-1">🎓 Lesson Type: {booking.lessonType}</p>
        <div className="flex justify-end mt-4">
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

export default BookingModalView;
