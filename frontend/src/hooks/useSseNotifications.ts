import { useEffect, useState } from "react";
import { NotificationType } from "@/types/NotificationType";
import { fetchNotifications } from "@/api/notificationAPI";

export const useSseNotifications = (userId: string, token: string) => {
  const [notifications, setNotifications] = useState<NotificationType[]>([]);

  useEffect(() => {
    if (!userId) return;

    // 1️⃣ Fetch existing notifications initially
    fetchNotifications(userId, token).then((res) => setNotifications(res.data));

    // 2️⃣ SSE for real-time updates
    const eventSource = new EventSource(
      `http://localhost:8080/api/v1/notifications/stream/${userId}`
    );
    eventSource.onmessage = (event) => {
      const newNotification: NotificationType = JSON.parse(event.data);
      setNotifications((prev) => [newNotification, ...prev]);
    };

    eventSource.onerror = (err) => {
      console.error("SSE error:", err);
      eventSource.close();
    };

    return () => eventSource.close();
  }, [userId, token]);

  return { notifications, setNotifications };
};
