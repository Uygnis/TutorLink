import { Tutor } from "./TutorType";

export interface AdminDashboardType {
    totalUsers: number,
    activeUsers: number,
    suspendedUsers: number,

    totalTutors: number,
    activeTutors: number,
    suspendedTutors: number,
    rejectedTutors: number,
    
    totalStudents: number,
    activeStudents: number,
    suspendedStudents: number,
    pendingTutors: Tutor[]
}