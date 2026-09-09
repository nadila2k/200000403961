const API_BASE_URL = '/api';

const handleResponse = async (res) => {
  const json = await res.json();
  if (!res.ok) {
    const errorMsg = json.message || `Request failed with status ${res.status}`;
    const err = new Error(errorMsg);
    err.status = res.status;
    err.data = json;
    throw err;
  }
  return json;
};

export const apiService = {
  // --- DEPARTMENTS ---
  getDepartments: async () => {
    const res = await fetch(`${API_BASE_URL}/departments`);
    return handleResponse(res);
  },
  createDepartment: async (dept) => {
    const res = await fetch(`${API_BASE_URL}/departments`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(dept),
    });
    return handleResponse(res);
  },
  deleteDepartment: async (id) => {
    const res = await fetch(`${API_BASE_URL}/departments/${id}`, { method: 'DELETE' });
    return handleResponse(res);
  },

  // --- OFFICERS ---
  getOfficers: async (departmentId = null) => {
    const url = departmentId ? `${API_BASE_URL}/officers?departmentId=${departmentId}` : `${API_BASE_URL}/officers`;
    const res = await fetch(url);
    return handleResponse(res);
  },
  createOfficer: async (officer) => {
    const res = await fetch(`${API_BASE_URL}/officers`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(officer),
    });
    return handleResponse(res);
  },
  deleteOfficer: async (id) => {
    const res = await fetch(`${API_BASE_URL}/officers/${id}`, { method: 'DELETE' });
    return handleResponse(res);
  },

  // --- VENUES ---
  getVenues: async () => {
    const res = await fetch(`${API_BASE_URL}/venues`);
    return handleResponse(res);
  },
  createVenue: async (venue) => {
    const res = await fetch(`${API_BASE_URL}/venues`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(venue),
    });
    return handleResponse(res);
  },
  deleteVenue: async (id) => {
    const res = await fetch(`${API_BASE_URL}/venues/${id}`, { method: 'DELETE' });
    return handleResponse(res);
  },

  // --- TRAINERS ---
  getTrainers: async () => {
    const res = await fetch(`${API_BASE_URL}/trainers`);
    return handleResponse(res);
  },
  createTrainer: async (trainer) => {
    const res = await fetch(`${API_BASE_URL}/trainers`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(trainer),
    });
    return handleResponse(res);
  },
  deleteTrainer: async (id) => {
    const res = await fetch(`${API_BASE_URL}/trainers/${id}`, { method: 'DELETE' });
    return handleResponse(res);
  },

  // --- PROGRAMMES ---
  getProgrammes: async () => {
    const res = await fetch(`${API_BASE_URL}/programmes`);
    return handleResponse(res);
  },
  createProgramme: async (progReq) => {
    const res = await fetch(`${API_BASE_URL}/programmes`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(progReq),
    });
    return handleResponse(res);
  },
  deleteProgramme: async (id) => {
    const res = await fetch(`${API_BASE_URL}/programmes/${id}`, { method: 'DELETE' });
    return handleResponse(res);
  },
  checkEligibility: async (programmeId, officerId) => {
    const res = await fetch(`${API_BASE_URL}/programmes/${programmeId}/check-eligibility?officerId=${officerId}`);
    return handleResponse(res);
  },

  // --- NOMINATIONS ---
  getNominations: async () => {
    const res = await fetch(`${API_BASE_URL}/nominations`);
    return handleResponse(res);
  },

  nominate: async (nomReq) => {
    const res = await fetch(`${API_BASE_URL}/nominations`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(nomReq),
    });
    return handleResponse(res);
  },

  updateNominationStatus: async (nominationId, status) => {
    const res = await fetch(`${API_BASE_URL}/nominations/${nominationId}/status?status=${status}`, {
      method: 'PATCH',
    });
    return handleResponse(res);
  },

  withdrawNomination: async (nominationId) => {
    const res = await fetch(`${API_BASE_URL}/nominations/${nominationId}/withdraw`, { method: 'POST' });
    return handleResponse(res);
  },

  findDuplicatesForProgramme: async (programmeId) => {
    const res = await fetch(`${API_BASE_URL}/nominations/programme/${programmeId}/duplicates`);
    return handleResponse(res);
  },

  findClashesForOfficer: async (officerId, excludeProgrammeId) => {
    const res = await fetch(
      `${API_BASE_URL}/nominations/officer/${officerId}/clashes?excludeProgrammeId=${excludeProgrammeId}`
    );
    return handleResponse(res);
  },
};
