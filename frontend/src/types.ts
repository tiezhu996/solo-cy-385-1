export interface Baby {
  id: number;
  name: string;
  birthday: string;
  bloodType?: string;
  initialHeight?: number;
  initialWeight?: number;
}

export type VaccineStatus = '已接种' | '待接种' | '已逾期';

export interface VaccineRecord {
  id: number;
  babyId: number;
  vaccineName: string;
  plannedDate: string;
  completed: boolean;
  status: VaccineStatus;
}
