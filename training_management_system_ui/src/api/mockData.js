export const initialDepartments = [
  { departmentId: 1, name: 'Information Technology', focalPointEmail: 'it.focal@gov.lk', focalPointPhone: '+94 11 234 5678' },
  { departmentId: 2, name: 'Human Resources', focalPointEmail: 'hr.focal@gov.lk', focalPointPhone: '+94 11 234 5679' },
  { departmentId: 3, name: 'Finance & Treasury', focalPointEmail: 'finance.focal@gov.lk', focalPointPhone: '+94 11 234 5680' },
  { departmentId: 4, name: 'Operations & Logistics', focalPointEmail: 'ops.focal@gov.lk', focalPointPhone: '+94 11 234 5681' },
  { departmentId: 5, name: 'Public Relations & Communication', focalPointEmail: 'pr.focal@gov.lk', focalPointPhone: '+94 11 234 5682' },
];

export const initialOfficers = [
  { officerId: 1, fullName: 'John Silva', nic: '198512345678', departmentId: 1, departmentName: 'Information Technology', designation: 'Senior Systems Analyst', email: 'john.silva@gov.lk', phone: '+94 77 123 4567' },
  { officerId: 2, fullName: 'Sarah Perera', nic: '199087654321', departmentId: 2, departmentName: 'Human Resources', designation: 'Assistant HR Manager', email: 'sarah.perera@gov.lk', phone: '+94 77 234 5678' },
  { officerId: 3, fullName: 'Kamal Gunaratne', nic: '198245678901', departmentId: 3, departmentName: 'Finance & Treasury', designation: 'Chief Accountant', email: 'kamal.g@gov.lk', phone: '+94 77 345 6789' },
  { officerId: 4, fullName: 'Nimal Fernando', nic: '197934567890', departmentId: 4, departmentName: 'Operations & Logistics', designation: 'Director Operations', email: 'nimal.f@gov.lk', phone: '+94 77 456 7890' },
  { officerId: 5, fullName: 'Anusha Peiris', nic: '199256789012', departmentId: 5, departmentName: 'Public Relations & Communication', designation: 'Media Relations Officer', email: 'anusha.p@gov.lk', phone: '+94 77 567 8901' },
];

export const initialVenues = [
  { venueId: 1, name: 'Main Auditorium', location: 'Building A, 3rd Floor', capacity: 150 },
  { venueId: 2, name: 'Executive Conference Room B', location: 'Building B, 1st Floor', capacity: 30 },
  { venueId: 3, name: 'Tech Training Lab 101', location: 'IT Center, Ground Floor', capacity: 25 },
];

export const initialTrainers = [
  { trainerId: 1, fullName: 'Dr. Roy Fernando', type: 'INTERNAL', specialization: 'Public Leadership & Governance', organization: 'National Institute of Administration', email: 'roy.fernando@nia.gov.lk', phone: '+94 71 111 2233' },
  { trainerId: 2, fullName: 'Prof. Anura Jayawardena', type: 'EXTERNAL', specialization: 'Cybersecurity & Data Privacy', organization: 'Cyber Resilience Center', email: 'anura.j@cybercenter.lk', phone: '+94 71 444 5566' },
  { trainerId: 3, fullName: 'Ms. Chandi Cooray', type: 'INTERNAL', specialization: 'Public Sector Financial Management', organization: 'Ministry of Finance', email: 'chandi.c@treasury.gov.lk', phone: '+94 71 777 8899' },
];

export const initialProgrammes = [
  {
    programmeId: 1,
    title: 'Public Sector Leadership & Strategic Management',
    description: 'Executive training on public governance, ethics, and transformational leadership.',
    startDate: '2026-10-01',
    endDate: '2026-10-05',
    venue: initialVenues[0],
    trainer: initialTrainers[0],
    maxParticipants: 15,
    approvedCount: 1,
    availableSeats: 14,
    targetDepartments: [initialDepartments[0], initialDepartments[1], initialDepartments[2]],
  },
  {
    programmeId: 2,
    title: 'Advanced Cybersecurity & Data Protection',
    description: 'Hands-on workshop on securing government IT infrastructure and GDPR/Personal Data Protection compliance.',
    startDate: '2026-10-10',
    endDate: '2026-10-12',
    venue: initialVenues[2],
    trainer: initialTrainers[1],
    maxParticipants: 2, // Intentionally set to 2 to test capacity auto-waitlisting!
    approvedCount: 2,
    availableSeats: 0,
    targetDepartments: [initialDepartments[0], initialDepartments[3]],
  },
  {
    programmeId: 3,
    title: 'Financial Governance & Auditing for Managers',
    description: 'Comprehensive course on public finance management, procurement rules, and audit readiness.',
    startDate: '2026-11-01',
    endDate: '2026-11-03',
    venue: initialVenues[1],
    trainer: initialTrainers[2],
    maxParticipants: 20,
    approvedCount: 0,
    availableSeats: 20,
    targetDepartments: [initialDepartments[2], initialDepartments[3]],
  },
];

export const initialNominations = [
  {
    nominationId: 1,
    programmeId: 1,
    programmeTitle: 'Public Sector Leadership & Strategic Management',
    officerId: 1,
    officerName: 'John Silva',
    nominatingDepartmentId: 1,
    nominatingDepartmentName: 'Information Technology',
    status: 'APPROVED',
    nominatedAt: '2026-09-01T09:30:00',
  },
  {
    nominationId: 2,
    programmeId: 1,
    programmeTitle: 'Public Sector Leadership & Strategic Management',
    officerId: 2,
    officerName: 'Sarah Perera',
    nominatingDepartmentId: 2,
    nominatingDepartmentName: 'Human Resources',
    status: 'PENDING',
    nominatedAt: '2026-09-02T11:15:00',
  },
  {
    nominationId: 3,
    programmeId: 2,
    programmeTitle: 'Advanced Cybersecurity & Data Protection',
    officerId: 3,
    officerName: 'Kamal Gunaratne',
    nominatingDepartmentId: 3,
    nominatingDepartmentName: 'Finance & Treasury',
    status: 'APPROVED',
    nominatedAt: '2026-09-03T14:20:00',
  },
  {
    nominationId: 4,
    programmeId: 2,
    programmeTitle: 'Advanced Cybersecurity & Data Protection',
    officerId: 4,
    officerName: 'Nimal Fernando',
    nominatingDepartmentId: 4,
    nominatingDepartmentName: 'Operations & Logistics',
    status: 'APPROVED',
    nominatedAt: '2026-09-04T10:00:00',
  },
  {
    nominationId: 5,
    programmeId: 2,
    programmeTitle: 'Advanced Cybersecurity & Data Protection',
    officerId: 5,
    officerName: 'Anusha Peiris',
    nominatingDepartmentId: 5,
    nominatingDepartmentName: 'Public Relations & Communication',
    status: 'WAITLISTED',
    nominatedAt: '2026-09-05T16:45:00',
  },
];
