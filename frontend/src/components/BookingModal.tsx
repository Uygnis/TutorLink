import React, { useState, useEffect } from "react";

export interface BookingModalProps {
  lessonTypes: string[];
  slot: { date: Date; slot: { start: string; end: string } };
  hourlyRate?: number; // ✅ new prop
  onClose: () => void;
  onConfirm: (lessonType: string) => void | Promise<void>;
}

const BookingModal: React.FC<BookingModalProps> = ({
  lessonTypes,
  slot,
  hourlyRate,
  onClose,
  onConfirm,
}) => {
  const [selectedLessonType, setSelectedLessonType] = useState<string>(lessonTypes[0] || "");
  const [estimatedCost, setEstimatedCost] = useState<number | null>(null);

  useEffect(() => {
    if (hourlyRate && slot.slot.start && slot.slot.end) {
      // Calculate hours
      const startTime = new Date(`1970-01-01T${slot.slot.start}:00`);
      const endTime = new Date(`1970-01-01T${slot.slot.end}:00`);
      const diffHours = (endTime.getTime() - startTime.getTime()) / (1000 * 60 * 60);
      if (diffHours > 0) {
        setEstimatedCost(diffHours * hourlyRate);
      } else {
        setEstimatedCost(null);
      }
    }
  }, [hourlyRate, slot]);

  const handleConfirm = () => {
    if (!selectedLessonType) return;
    onConfirm(selectedLessonType);
  };

  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40 z-50">
      <div className="bg-white rounded-xl shadow-lg p-6 w-96">
        <h2 className="text-lg font-semibold mb-4">Confirm Booking</h2>

        <p className="mb-2">
          📅 {slot.date.toDateString()} | ⏰ {slot.slot.start} - {slot.slot.end}
        </p>

        {/* Lesson Type Selector */}
        <label className="block mb-2 text-sm font-medium">Lesson Type</label>
        <select
          className="w-full border rounded-md p-2 mb-4"
          value={selectedLessonType}
          onChange={(e) => setSelectedLessonType(e.target.value)}>
          {lessonTypes.map((lt, idx) => (
            <option key={idx} value={lt}>
              {lt}
            </option>
          ))}
        </select>

        {/* Optional estimated cost */}
        {hourlyRate && estimatedCost !== null && (
          <div className="mb-4 p-3 bg-blue-50 rounded-md text-sm text-gray-700">
            <p>
              💰 Estimated Cost:{" "}
              <span className="font-bold text-blue-600">SGD {estimatedCost.toFixed(2)}</span>
            </p>
            <p className="text-xs text-gray-500">({hourlyRate} SGD/hr × duration)</p>
          </div>
        )}

        <div className="flex justify-end gap-2">
          <button onClick={onClose} className="px-4 py-2 rounded-md bg-gray-200 hover:bg-gray-300">
            Cancel
          </button>
          <button
            onClick={handleConfirm}
            className="px-4 py-2 rounded-md bg-blue-600 text-white hover:bg-blue-700">
            Confirm
          </button>
        </div>
      </div>
    </div>
  );
};

export default BookingModal;
