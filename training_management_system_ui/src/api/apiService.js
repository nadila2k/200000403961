import {
  initialDepartments,
  initialOfficers,
  initialVenues,
  initialTrainers,
  initialProgrammes,
  initialNominations,
} from './mockData';

// State for mock mode
let mockDepartments = [...initialDepartments];
let mockOfficers = [...initialOfficers];
let mockVenues = [...initialVenues];
let mockTrainers = [...initialTrainers];
let mockProgrammes = [...initialProgrammes];
let mockNominations = [...initialNominations];

// Mode flag (default: true for instant interactive preview, can toggle to false for real Spring Boot backend)
let isMockMode = true;

export const setMockMode = (enabled) => {
  isMockMode = enabled;
};

export const getMockMode = () => isMockMode;

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
    if (isMockMode) return { responseStatus: 'SUCCESS', message: 'Departments fetched', data: mockDepartments };
    const res = await fetch(`${API_BASE_URL}/departments`);
    return handleResponse(res);
  },
  createDepartment: async (dept) => {
    if (isMockMode) {
      const newDept = { departmentId: Date.now(), ...dept };
      mockDepartments.push(newDept);
      return { responseStatus: 'SUCCESS', message: 'Department created', data: newDept };
    }
    const res = await fetch(`${API_BASE_URL}/departments`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(dept),
    });
    return handleResponse(res);
  },
  deleteDepartment: async (id) => {
    if (isMockMode) {
      mockDepartments = mockDepartments.filter((d) => d.departmentId !== id);
      return { responseStatus: 'SUCCESS', message: 'Department deleted', data: null };
    }
    const res = await fetch(`${API_BASE_URL}/departments/${id}`, { method: 'DELETE' });
    return handleResponse(res);
  },

  // --- OFFICERS ---
  getOfficers: async (departmentId = null) => {
    if (isMockMode) {
      let filtered = mockOfficers;
      if (departmentId) {
        filtered = mockOfficers.filter((o) => o.departmentId === Number(departmentId));
      }
      return { responseStatus: 'SUCCESS', message: 'Officers fetched', data: filtered };
    }
    const url = departmentId ? `${API_BASE_URL}/officers?departmentId=${departmentId}` : `${API_BASE_URL}/officers`;
    const res = await fetch(url);
    return handleResponse(res);
  },
  createOfficer: async (officer) => {
    if (isMockMode) {
      const dept = mockDepartments.find((d) => d.departmentId === Number(officer.departmentId));
      const newOfficer = {
        officerId: Date.now(),
        ...officer,
        departmentId: Number(officer.departmentId),
        departmentName: dept ? dept.name : 'Unknown',
      };
      mockOfficers.push(newOfficer);
      return { responseStatus: 'SUCCESS', message: 'Officer created', data: newOfficer };
    }
    const res = await fetch(`${API_BASE_URL}/officers`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(officer),
    });
    return handleResponse(res);
  },
  deleteOfficer: async (id) => {
    if (isMockMode) {
      mockOfficers = mockOfficers.filter((o) => o.officerId !== id);
      return { responseStatus: 'SUCCESS', message: 'Officer deleted', data: null };
    }
    const res = await fetch(`${API_BASE_URL}/officers/${id}`, { method: 'DELETE' });
    return handleResponse(res);
  },

  // --- VENUES ---
  getVenues: async () => {
    if (isMockMode) return { responseStatus: 'SUCCESS', message: 'Venues fetched', data: mockVenues };
    const res = await fetch(`${API_BASE_URL}/venues`);
    return handleResponse(res);
  },
  createVenue: async (venue) => {
    if (isMockMode) {
      const newVenue = { venueId: Date.now(), ...venue, capacity: Number(venue.capacity) };
      mockVenues.push(newVenue);
      return { responseStatus: 'SUCCESS', message: 'Venue created', data: newVenue };
    }
    const res = await fetch(`${API_BASE_URL}/venues`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(venue),
    });
    return handleResponse(res);
  },
  deleteVenue: async (id) => {
    if (isMockMode) {
      mockVenues = mockVenues.filter((v) => v.venueId !== id);
      return { responseStatus: 'SUCCESS', message: 'Venue deleted', data: null };
    }
    const res = await fetch(`${API_BASE_URL}/venues/${id}`, { method: 'DELETE' });
    return handleResponse(res);
  },

  // --- TRAINERS ---
  getTrainers: async () => {
    if (isMockMode) return { responseStatus: 'SUCCESS', message: 'Trainers fetched', data: mockTrainers };
    const res = await fetch(`${API_BASE_URL}/trainers`);
    return handleResponse(res);
  },
  createTrainer: async (trainer) => {
    if (isMockMode) {
      const newTrainer = { trainerId: Date.now(), ...trainer };
      mockTrainers.push(newTrainer);
      return { responseStatus: 'SUCCESS', message: 'Trainer created', data: newTrainer };
    }
    const res = await fetch(`${API_BASE_URL}/trainers`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(trainer),
    });
    return handleResponse(res);
  },
  deleteTrainer: async (id) => {
    if (isMockMode) {
      mockTrainers = mockTrainers.filter((t) => t.trainerId !== id);
      return { responseStatus: 'SUCCESS', message: 'Trainer deleted', data: null };
    }
    const res = await fetch(`${API_BASE_URL}/trainers/${id}`, { method: 'DELETE' });
    return handleResponse(res);
  },

  // --- PROGRAMMES ---
  getProgrammes: async () => {
    if (isMockMode) {
      // Re-calculate approved counts and available seats for accuracy
      const updated = mockProgrammes.map((prog) => {
        const approvedCount = mockNominations.filter(
          (n) => n.programmeId === prog.programmeId && n.status === 'APPROVED'
        ).length;
        const availableSeats = Math.max(0, prog.maxParticipants - approvedCount);
        return { ...prog, approvedCount, availableSeats };
      });
      mockProgrammes = updated;
      return { responseStatus: 'SUCCESS', message: 'Programmes fetched', data: updated };
    }
    const res = await fetch(`${API_BASE_URL}/programmes`);
    return handleResponse(res);
  },
  createProgramme: async (progReq) => {
    if (isMockMode) {
      const venue = mockVenues.find((v) => v.venueId === Number(progReq.venueId)) || null;
      const trainer = mockTrainers.find((t) => t.trainerId === Number(progReq.trainerId)) || null;
      const targetDepts = mockDepartments.filter((d) =>
        progReq.targetDepartmentIds?.includes(d.departmentId)
      );

      const newProg = {
        programmeId: Date.now(),
        title: progReq.title,
        description: progReq.description,
        startDate: progReq.startDate,
        endDate: progReq.endDate,
        venue,
        trainer,
        maxParticipants: Number(progReq.maxParticipants),
        approvedCount: 0,
        availableSeats: Number(progReq.maxParticipants),
        targetDepartments: targetDepts,
      };
      mockProgrammes.push(newProg);
      return { responseStatus: 'SUCCESS', message: 'Programme created', data: newProg };
    }
    const res = await fetch(`${API_BASE_URL}/programmes`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(progReq),
    });
    return handleResponse(res);
  },
  deleteProgramme: async (id) => {
    if (isMockMode) {
      mockProgrammes = mockProgrammes.filter((p) => p.programmeId !== id);
      mockNominations = mockNominations.filter((n) => n.programmeId !== id);
      return { responseStatus: 'SUCCESS', message: 'Programme deleted', data: null };
    }
    const res = await fetch(`${API_BASE_URL}/programmes/${id}`, { method: 'DELETE' });
    return handleResponse(res);
  },

  // --- NOMINATIONS ---
  getNominations: async () => {
    if (isMockMode) return { responseStatus: 'SUCCESS', message: 'Nominations fetched', data: mockNominations };
    // Fetch via programme or all
    const res = await fetch(`${API_BASE_URL}/nominations`);
    return handleResponse(res);
  },

  nominate: async (nomReq) => {
    if (isMockMode) {
      const programmeId = Number(nomReq.programmeId);
      const officerId = Number(nomReq.officerId);
      const deptId = Number(nomReq.nominatingDepartmentId);

      // Check active duplicate (PENDING, APPROVED, WAITLISTED)
      const activeDuplicate = mockNominations.find(
        (n) =>
          n.programmeId === programmeId &&
          n.officerId === officerId &&
          ['PENDING', 'APPROVED', 'WAITLISTED'].includes(n.status)
      );

      if (activeDuplicate) {
        const err = new Error(
          `Officer is already actively nominated for this programme by ${activeDuplicate.nominatingDepartmentName} (Status: ${activeDuplicate.status}) on ${new Date(
            activeDuplicate.nominatedAt
          ).toLocaleDateString()}`
        );
        err.status = 409;
        throw err;
      }

      // Find programme and check capacity
      const prog = mockProgrammes.find((p) => p.programmeId === programmeId);
      const officer = mockOfficers.find((o) => o.officerId === officerId);
      const dept = mockDepartments.find((d) => d.departmentId === deptId);

      // Count active approved nominations
      const approvedCount = mockNominations.filter(
        (n) => n.programmeId === programmeId && n.status === 'APPROVED'
      ).length;

      // Auto-set status to WAITLISTED if full, else PENDING
      const isFull = prog ? approvedCount >= prog.maxParticipants : false;
      const status = isFull ? 'WAITLISTED' : 'PENDING';

      const newNom = {
        nominationId: Date.now(),
        programmeId,
        programmeTitle: prog ? prog.title : 'Programme #' + programmeId,
        officerId,
        officerName: officer ? officer.fullName : 'Officer #' + officerId,
        nominatingDepartmentId: deptId,
        nominatingDepartmentName: dept ? dept.name : 'Department #' + deptId,
        status,
        nominatedAt: new Date().toISOString(),
      };

      mockNominations.unshift(newNom);

      return {
        responseStatus: 'SUCCESS',
        message: isFull
          ? 'Programme capacity reached. Officer auto-waitlisted successfully.'
          : 'Nomination submitted successfully.',
        data: newNom,
      };
    }

    const res = await fetch(`${API_BASE_URL}/nominations`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(nomReq),
    });
    return handleResponse(res);
  },

  updateNominationStatus: async (nominationId, status) => {
    if (isMockMode) {
      const nom = mockNominations.find((n) => n.nominationId === Number(nominationId));
      if (nom) {
        nom.status = status;
      }
      return { responseStatus: 'SUCCESS', message: 'Status updated', data: nom };
    }
    const res = await fetch(`${API_BASE_URL}/nominations/${nominationId}/status?status=${status}`, {
      method: 'PATCH',
    });
    return handleResponse(res);
  },

  withdrawNomination: async (nominationId) => {
    if (isMockMode) {
      const nom = mockNominations.find((n) => n.nominationId === Number(nominationId));
      if (nom) {
        nom.status = 'WITHDRAWN';
      }
      return { responseStatus: 'SUCCESS', message: 'Nomination withdrawn', data: null };
    }
    const res = await fetch(`${API_BASE_URL}/nominations/${nominationId}/withdraw`, { method: 'POST' });
    return handleResponse(res);
  },

  findDuplicatesForProgramme: async (programmeId) => {
    if (isMockMode) {
      const activeNoms = mockNominations.filter(
        (n) => n.programmeId === Number(programmeId) && ['PENDING', 'APPROVED', 'WAITLISTED'].includes(n.status)
      );

      // Group by officerId
      const grouped = {};
      activeNoms.forEach((n) => {
        if (!grouped[n.officerId]) grouped[n.officerId] = [];
        grouped[n.officerId].push(n);
      });

      const duplicates = Object.values(grouped).filter((list) => list.length > 1).flat();
      return { responseStatus: 'SUCCESS', message: 'Duplicates report fetched', data: duplicates };
    }
    const res = await fetch(`${API_BASE_URL}/nominations/programme/${programmeId}/duplicates`);
    return handleResponse(res);
  },

  findClashesForOfficer: async (officerId, excludeProgrammeId) => {
    if (isMockMode) {
      const activeOtherNoms = mockNominations.filter(
        (n) =>
          n.officerId === Number(officerId) &&
          n.programmeId !== Number(excludeProgrammeId) &&
          ['PENDING', 'APPROVED', 'WAITLISTED'].includes(n.status)
      );
      return { responseStatus: 'SUCCESS', message: 'Schedule clashes fetched', data: activeOtherNoms };
    }
    const res = await fetch(
      `${API_BASE_URL}/nominations/officer/${officerId}/clashes?excludeProgrammeId=${excludeProgrammeId}`
    );
    return handleResponse(res);
  },
};
