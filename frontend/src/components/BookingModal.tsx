// BookingModal.tsx
import React, { useState } from "react";

export interface BookingModalProps {
  lessonTypes: string[];
  slot: { date: Date; slot: { start: string; end: string } };
  onClose: () => void;
  onConfirm: (lessonType: string) => void | Promise<void>;
}

const BookingModal: React.FC<BookingModalProps> = ({ lessonTypes, slot, onClose, onConfirm }) => {
  const [selectedLessonType, setSelectedLessonType] = useState<string>(lessonTypes[0] || "");

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
