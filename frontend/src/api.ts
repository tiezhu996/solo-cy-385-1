import type { Baby, VaccineRecord } from './types';

interface ErrorBody {
  success?: boolean;
  message?: string;
}

async function get<T>(url: string): Promise<T> {
  const res = await fetch(url);
  if (!res.ok) {
    throw new Error(`请求失败: ${res.status}`);
  }
  const body = await res.json();
  // 后端统一异常处理器会以 200 返回 { success: false, message }，需要识别为错误
  if (body && typeof body === 'object' && !Array.isArray(body) && (body as ErrorBody).success === false) {
    throw new Error((body as ErrorBody).message || '服务器内部错误');
  }
  return body as T;
}

export async function fetchBabies(): Promise<Baby[]> {
  const body = await get<Baby[]>('/api/babies');
  return Array.isArray(body) ? body : [];
}

export async function fetchVaccines(babyId?: number): Promise<VaccineRecord[]> {
  const url = babyId == null ? '/api/vaccines' : `/api/vaccines?babyId=${babyId}`;
  const body = await get<VaccineRecord[]>(url);
  return Array.isArray(body) ? body : [];
}
