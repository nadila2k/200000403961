import React from 'react';
import {
  Grid,
  Box,
  Typography,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Button,
  LinearProgress,
  Chip,
  Card,
  CardContent,
} from '@mui/material';
import SchoolIcon from '@mui/icons-material/School';
import PeopleIcon from '@mui/icons-material/People';
import AssignmentIcon from '@mui/icons-material/Assignment';
import EventAvailableIcon from '@mui/icons-material/EventAvailable';
import HourglassEmptyIcon from '@mui/icons-material/HourglassEmpty';
import AddIcon from '@mui/icons-material/Add';
import StatsCard from '../components/StatsCard';
import StatusChip from '../components/StatusChip';

const DashboardView = ({
  programmes,
  nominations,
  officers,
  departments,
  onNavigate,
  onOpenNominate,
}) => {
  // Metric counts
  const totalProgrammes = programmes.length;
  const totalOfficers = officers.length;
  const totalNominations = nominations.length;
  const approvedNominations = nominations.filter((n) => n.status === 'APPROVED').length;
  const pendingNominations = nominations.filter((n) => n.status === 'PENDING').length;
  const waitlistedNominations = nominations.filter((n) => n.status === 'WAITLISTED').length;

  const recentNominations = nominations.slice(0, 5);

  return (
    <Box>
      {/* Title & Quick Actions */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box>
          <Typography variant="h5" sx={{ fontWeight: 700, color: 'text.primary' }}>
            Training Management Overview
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Monitor training programmes, seat allocations, officer nominations, and waitlists.
          </Typography>
        </Box>
        <Box sx={{ display: 'flex', gap: 1.5 }}>
          <Button
            variant="contained"
            color="secondary"
            startIcon={<AddIcon />}
            onClick={onOpenNominate}
          >
            Submit Nomination
          </Button>
          <Button variant="outlined" onClick={() => onNavigate('programmes')}>
            View All Programmes
          </Button>
        </Box>
      </Box>

      {/* KPI Cards */}
      <Grid container spacing={2.5} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <StatsCard
            title="Total Programmes"
            value={totalProgrammes}
            subtitle={`${programmes.filter((p) => new Date(p.endDate) >= new Date()).length} Active / Upcoming`}
            icon={<SchoolIcon />}
            iconBgColor="#2563eb"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatsCard
            title="Registered Officers"
            value={totalOfficers}
            subtitle={`Across ${departments.length} Departments`}
            icon={<PeopleIcon />}
            iconBgColor="#10b981"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatsCard
            title="Total Nominations"
            value={totalNominations}
            subtitle={`${approvedNominations} Approved (${Math.round((approvedNominations / (totalNominations || 1)) * 100)}%)`}
            icon={<AssignmentIcon />}
            iconBgColor="#8b5cf6"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatsCard
            title="Pending & Waitlists"
            value={`${pendingNominations} / ${waitlistedNominations}`}
            subtitle="Pending Review / Auto-Waitlisted"
            icon={<HourglassEmptyIcon />}
            iconBgColor="#f59e0b"
          />
        </Grid>
      </Grid>

      {/* Main Dashboard Layout */}
      <Grid container spacing={3}>
        {/* Active Programmes & Seat Allocation Progress */}
        <Grid item xs={12} md={7}>
          <Paper sx={{ p: 3, height: '100%' }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
              <Typography variant="h6" sx={{ fontWeight: 700 }}>
                Programmes & Seat Capacity
              </Typography>
              <Button size="small" onClick={() => onNavigate('programmes')}>
                Manage Programmes
              </Button>
            </Box>

            <Grid container spacing={2}>
              {programmes.slice(0, 4).map((prog) => {
                const approved = prog.approvedCount || 0;
                const capacity = prog.maxParticipants || 1;
                const percentage = Math.min(100, Math.round((approved / capacity) * 100));
                const isFull = approved >= capacity;

                return (
                  <Grid item xs={12} key={prog.programmeId}>
                    <Card variant="outlined" sx={{ bgcolor: isFull ? '#fff7ed' : '#ffffff' }}>
                      <CardContent sx={{ p: 2, '&:last-child': { pb: 2 } }}>
                        <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                          <Typography variant="subtitle2" sx={{ fontWeight: 700 }}>
                            {prog.title}
                          </Typography>
                          <Chip
                            label={isFull ? 'CAPACITY FULL' : `${prog.availableSeats ?? (capacity - approved)} Seats Left`}
                            color={isFull ? 'warning' : 'success'}
                            size="small"
                            sx={{ fontWeight: 600, fontSize: '0.7rem' }}
                          />
                        </Box>
                        <Typography variant="caption" color="text.secondary" display="block" sx={{ mb: 1.5 }}>
                          {prog.startDate} to {prog.endDate} • Venue: {prog.venue?.name || 'TBA'} • Trainer: {prog.trainer?.fullName || 'TBA'}
                        </Typography>

                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                          <Box sx={{ flexGrow: 1 }}>
                            <LinearProgress
                              variant="determinate"
                              value={percentage}
                              color={isFull ? 'warning' : 'primary'}
                              sx={{ height: 8, borderRadius: 4 }}
                            />
                          </Box>
                          <Typography variant="caption" sx={{ fontWeight: 700, minWidth: 60 }}>
                            {approved} / {capacity}
                          </Typography>
                        </Box>
                      </CardContent>
                    </Card>
                  </Grid>
                );
              })}
            </Grid>
          </Paper>
        </Grid>

        {/* Recent Nominations Activity */}
        <Grid item xs={12} md={5}>
          <Paper sx={{ p: 3, height: '100%' }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
              <Typography variant="h6" sx={{ fontWeight: 700 }}>
                Recent Nominations
              </Typography>
              <Button size="small" onClick={() => onNavigate('nominations')}>
                View All
              </Button>
            </Box>

            <TableContainer>
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>Officer</TableCell>
                    <TableCell>Programme</TableCell>
                    <TableCell>Status</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {recentNominations.map((nom) => (
                    <TableRow key={nom.nominationId} hover>
                      <TableCell>
                        <Typography variant="body2" sx={{ fontWeight: 600 }}>
                          {nom.officerName}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          {nom.nominatingDepartmentName}
                        </Typography>
                      </TableCell>
                      <TableCell sx={{ maxWidth: 140 }}>
                        <Typography variant="caption" noWrap display="block" title={nom.programmeTitle}>
                          {nom.programmeTitle}
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <StatusChip status={nom.status} />
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
};

export default DashboardView;
