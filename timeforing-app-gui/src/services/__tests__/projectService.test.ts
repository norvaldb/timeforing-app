import { describe, it, expect, vi, beforeEach } from 'vitest';

// Mock the apiClient module used by projectService
vi.mock('../apiClient', () => ({
  apiClient: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

import { apiClient } from '../apiClient';
import projectService from '../projectService';

describe('projectService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('getAll should call apiClient.get and return data', async () => {
    const fake = [{ id: 1, navn: 'P1', beskrivelse: 'B1' }];
    (apiClient.get as any).mockResolvedValueOnce({ data: fake });

    const res = await projectService.getAll();

    expect(apiClient.get).toHaveBeenCalledWith('/api/projects');
    expect(res).toEqual(fake);
  });

  it('create should call apiClient.post and return created project', async () => {
    const dto = { navn: 'Ny', beskrivelse: 'Desc' };
    const created = { id: 5, ...dto };
    (apiClient.post as any).mockResolvedValueOnce({ data: created });

    const res = await projectService.create(dto as any);

    expect(apiClient.post).toHaveBeenCalledWith('/api/projects', dto);
    expect(res).toEqual(created);
  });

  it('update should call apiClient.put and return updated project', async () => {
    const dto = { navn: 'Oppdatert', beskrivelse: 'X' };
    const updated = { id: 2, ...dto };
    (apiClient.put as any).mockResolvedValueOnce({ data: updated });

    const res = await projectService.update(2, dto as any);

    expect(apiClient.put).toHaveBeenCalledWith('/api/projects/2', dto);
    expect(res).toEqual(updated);
  });

  it('remove should call apiClient.delete', async () => {
    (apiClient.delete as any).mockResolvedValueOnce({});

    await projectService.remove(3);

    expect(apiClient.delete).toHaveBeenCalledWith('/api/projects/3');
  });
});
