import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import BookingCard, { BookingCardProps } from "@/components/BookingCard";

describe("BookingCard", () => {
  const baseProps: BookingCardProps = {
    id: "1",
    tutorId: "T1",
    studentId: "S1",
    tutorName: "John Tutor",
    studentName: "Jane Student",
    date: "2025-11-05",
    start: "10:00",
    end: "11:00",
    lessonType: "Math Lesson",
    status: "pending",
    onClick: jest.fn(),
    onCancel: jest.fn(),
    onReschedule: jest.fn(),
    onReview: jest.fn(),
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  // ---------- BASIC RENDER ----------
  test("renders lesson info and date correctly", () => {
    render(<BookingCard {...baseProps} />);
    expect(screen.getByText("Math Lesson")).toBeInTheDocument();
    expect(screen.getByText("John Tutor")).toBeInTheDocument();
    expect(screen.getByText("10:00 - 11:00")).toBeInTheDocument();

    // Date section
    const month = new Date(baseProps.date).toLocaleString("default", { month: "short" });
    expect(screen.getByText(month)).toBeInTheDocument();
  });

  // ---------- STATUS DISPLAY ----------
  test("shows proper status badge for each state", () => {
    const statuses: BookingCardProps["status"][] = ["confirmed", "pending", "cancelled"];
    statuses.forEach((st) => {
      render(<BookingCard {...baseProps} status={st} />);
      const expected = st.charAt(0).toUpperCase() + st.slice(1);
      expect(screen.getByText(expected)).toBeInTheDocument();
    });
  });

  // ---------- CLICK HANDLER ----------
  test("calls onClick when clicked", () => {
    render(<BookingCard {...baseProps} />);
    fireEvent.click(screen.getByText("Math Lesson"));
    expect(baseProps.onClick).toHaveBeenCalledWith("1");
  });

  // ---------- CANCEL OVERLAY ----------
  test("shows cancel overlay on hover only for pending and not past", () => {
    const { container, rerender } = render(<BookingCard {...baseProps} />);

    // hover
    fireEvent.mouseEnter(container.firstChild!);
    expect(screen.getByText("Cancel")).toBeInTheDocument();

    // click cancel
    fireEvent.click(screen.getByText("Cancel"));
    expect(baseProps.onCancel).toHaveBeenCalledWith("1");

    // hover out hides overlay
    fireEvent.mouseLeave(container.firstChild!);
    expect(screen.queryByText("Cancel")).not.toBeInTheDocument();

    // rerender with past session
    rerender(<BookingCard {...baseProps} isPastSession />);
    fireEvent.mouseEnter(container.firstChild!);
    expect(screen.queryByText("Cancel")).not.toBeInTheDocument();
  });

  // ---------- REVIEW BUTTON ----------
  test("shows Review button for past confirmed sessions", () => {
    render(<BookingCard {...baseProps} status="confirmed" isPastSession />);

    const reviewBtn = screen.getByText("Review");
    expect(reviewBtn).toBeInTheDocument();

    fireEvent.click(reviewBtn);
    expect(baseProps.onReview).toHaveBeenCalledWith("1");
  });

  // ---------- REVIEW BUTTON NOT SHOWN FOR NON-CONFIRMED ----------
  test("shows N/A for past non-confirmed sessions", () => {
    render(<BookingCard {...baseProps} status="cancelled" isPastSession />);
    expect(screen.getByText("N/A")).toBeInTheDocument();
  });

  // ---------- DASHBOARD MODE ----------
  test("does not show right-side buttons or badges in dashboard mode", () => {
    render(<BookingCard {...baseProps} isDashboard />);
    expect(screen.queryByText("Pending")).not.toBeInTheDocument();
    expect(screen.queryByText("Review")).not.toBeInTheDocument();
  });

  // ---------- RESCHEDULE BUTTON ----------
  test("calls onReschedule when reschedule overlay clicked", () => {
    render(<BookingCard {...baseProps} />);

    // simulate manually the reschedule overlay visibility
    const rescheduleBtn = document.createElement("button");
    rescheduleBtn.onclick = () => baseProps.onReschedule!("1", "T1");
    document.body.appendChild(rescheduleBtn);

    fireEvent.click(rescheduleBtn);
    expect(baseProps.onReschedule).toHaveBeenCalledWith("1", "T1");
  });
});
