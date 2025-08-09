export interface Doctor {
  id: number;
  name: string;
  docId: string;
  email: string;
  status: "Active" | "Inactive";
}
