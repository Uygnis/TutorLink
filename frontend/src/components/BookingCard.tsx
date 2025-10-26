import React, { useState } from "react";

export interface BookingCardProps {
  id: string;
  tutorId?: string;
  studentId?: string;
  tutorName: string;
  studentName?: string;
  date: string;
  start: string;
  end: string;
  status: "confirmed" | "pending" | "cancelled";
  lessonType: string;
  isPastSession?: boolean;
  onClick?: (id: string) => void; // optional card click
  onCancel?: (id: string) => void; // cancel handler
  onReschedule?: (bookingId: string, tutorId: string) => void; // reschedule handler
}

const BookingCard: React.FC<BookingCardProps> = ({
  id,
  tutorId,
  tutorName,
  date,
  start,
  end,
  lessonType,
  status,
  isPastSession = false,
  onClick,
  onCancel,
  onReschedule,
}) => {
  const [isHovered, setIsHovered] = useState(false);

  const statusColor = {
    confirmed: "bg-green-100 text-green-800",
    pending: "bg-yellow-100 text-yellow-800",
    cancelled: "bg-red-100 text-red-800",
  } as const;

  const handleCancelClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    if (onCancel) onCancel(id);
  };

  const showCancelOverlay = status === "pending" && isHovered;
  const showRescheduleOverlay = status === "confirmed" && isHovered;

  const month = new Date(date).toLocaleString("default", { month: "short" });
  const day = new Date(date).getDate();
  const weekday = new Date(date).toLocaleDateString(undefined, {
    weekday: "short",
    year: "numeric",
  });

  return (
    <div
      onClick={() => onClick && onClick(id)}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
      className={`relative flex items-center justify-between border rounded-md p-4 shadow-sm transition cursor-pointer ${
        showCancelOverlay ? "bg-red-100" : "bg-white hover:shadow-md"
      }`}>
      {/* Left: Date */}
      <div className="text-center pr-4 border-r">
        <p className={`uppercase ${isPastSession ? "text-xs" : "text-sm"} text-gray-500`}>
          {month}
        </p>
        <p className={`font-bold ${isPastSession ? "text-lg" : "text-2xl"}`}>{day}</p>
        <p className={`${isPastSession ? "text-xs" : "text-sm"} text-gray-500`}>{weekday}</p>
      </div>

      {/* Middle: Main Info */}
      <div className="flex-1 px-4">
        <p className={`${isPastSession ? "text-sm" : "text-lg"} font-semibold`}>{lessonType}</p>
        <p className={`${isPastSession ? "text-xs" : "text-sm"} text-gray-600`}>
          {start} - {end} SG
        </p>
        <p className={`${isPastSession ? "text-xs" : "text-sm"} text-gray-600`}>{tutorName}</p>
      </div>

      {/* Right: Status */}
      {!isPastSession && (
        <div className="flex flex-col items-end gap-1">
          <span className={`px-2 py-1 rounded-md text-LG font-semibold ${statusColor[status]}`}>
            {status.charAt(0).toUpperCase() + status.slice(1)}
          </span>
        </div>
      )}

      {/* Cancel overlay for pending */}
      {showCancelOverlay && (
        <button
          onClick={handleCancelClick}
          className="absolute inset-0 flex items-center justify-center bg-red-500/70 text-white font-bold rounded-md backdrop-blur-sm"
          aria-label="Cancel Booking">
          Cancel
        </button>
      )}

      {/* Reschedule overlay for confirmed */}
      {showRescheduleOverlay && (
        <button
          onClick={(e) => {
            e.stopPropagation();
            if (onReschedule && tutorId) onReschedule(id, tutorId);
          }}
          className="absolute inset-0 flex items-center justify-center bg-blue-500/70 text-white font-bold rounded-md backdrop-blur-sm opacity-0 hover:opacity-100 transition"
          aria-label="Reschedule Booking">
          Reschedule
        </button>
      )}
    </div>
  );
};

export default BookingCard;
