import { apiClient } from './apiClient';
import type { ProjectDto, CreateProjectDto } from '../types/project';

const base = '/api/projects';

export default {
  getAll: async (): Promise<ProjectDto[]> => {
  const resp = await apiClient.get<ProjectDto[]>(base);
    return resp.data;
  },
  create: async (dto: CreateProjectDto): Promise<ProjectDto> => {
    const resp = await apiClient.post<ProjectDto>(base, dto);
    return resp.data;
  },
  update: async (id: number, dto: CreateProjectDto): Promise<ProjectDto> => {
    const resp = await apiClient.put<ProjectDto>(`${base}/${id}`, dto);
    return resp.data;
  },
  remove: async (id: number): Promise<void> => {
    await apiClient.delete(`${base}/${id}`);
  }
};
