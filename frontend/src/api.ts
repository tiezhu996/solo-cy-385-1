import type { Baby, VaccineRecord } from './types';

async function get<T>(url: string): Promise<T> {
  const res = await fetch(url);
  if (!res.ok) {
    throw new Error(`请求失败: ${res.status}`);
  }
  return res.json() as Promise<T>;
}

export function fetchBabies(): Promise<Baby[]> {
  return get<Baby[]>('/api/babies');
}

export function fetchVaccines(babyId: number): Promise<VaccineRecord[]> {
  return get<VaccineRecord[]>(`/api/vaccines?babyId=${babyId}`);
}
