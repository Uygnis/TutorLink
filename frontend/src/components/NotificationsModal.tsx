import React from "react";
import { NotificationType } from "@/types/NotificationType";

interface NotificationsModalProps {
  isOpen: boolean;
  onClose: () => void;
  notifications: (NotificationType & { onClick?: () => void })[]; // allow passing click handler
}

const NotificationsModal: React.FC<NotificationsModalProps> = ({
  isOpen,
  onClose,
  notifications,
}) => {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
      <div className="bg-white rounded-md shadow-lg w-96 p-6 relative">
        <h2 className="text-lg font-bold mb-4">Notifications</h2>
        <button
          onClick={onClose}
          className="absolute top-2 right-2 text-gray-500 hover:text-gray-700">
          ✕
        </button>

        {notifications.length === 0 ? (
          <p className="text-gray-500">No notifications</p>
        ) : (
          <ul className="space-y-2 max-h-64 overflow-y-auto">
            {notifications.map((note) => (
              <li
                key={note.id}
                onClick={note.onClick}
                className={`border-b pb-1 text-sm cursor-pointer px-2 py-1 rounded transition ${
                  note.read ? "bg-gray-100 text-gray-500" : "bg-white text-black hover:bg-gray-100"
                }`}>
                {note.message}
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
};

export default NotificationsModal;
