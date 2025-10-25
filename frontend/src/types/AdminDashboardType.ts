import { Tutor } from "./TutorType";

export interface AdminDashboardType {
    totalUsers: number,
    activeUsers: number,
    suspendedUsers: number,

    totalTutors: number,
    activeTutors: number,
    suspendedTutors: number,
    unverifiedTutors: number,
    
    totalStudents: number,
    activeStudents: number,
    suspendedStudents: number,

    totalAdmins: number,
    activeAdmins: number,
    suspendedAdmins: number,
    pendingTutors: Tutor[]
}