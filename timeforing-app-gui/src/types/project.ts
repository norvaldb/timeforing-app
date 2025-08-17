export type ProjectDto = {
  id: number;
  navn: string;
  beskrivelse?: string | null;
};

export type CreateProjectDto = {
  navn: string;
  beskrivelse?: string;
};
