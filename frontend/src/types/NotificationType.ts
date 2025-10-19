export interface NotificationType {
  id: string;
  userId: string;
  type: string;
  bookingId: string;
  message: string;
  read: boolean;
  createdAt: string;
}
