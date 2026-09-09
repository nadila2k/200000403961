import React, { useState } from 'react';
import {
  Box,
  Typography,
  Paper,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Menu,
  MenuItem,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Grid,
  Alert,
  Tooltip,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import EventBusyIcon from '@mui/icons-material/EventBusy';
import StatusChip from '../components/StatusChip';
import ScheduleClashDialog from '../components/ScheduleClashDialog';
import { apiService } from '../api/apiService';

const NominationsView = ({
  nominations,
  programmes,
  officers,
  departments,
  onRefresh,
  showNotification,
  openNominateDialog,
  setOpenNominateDialog,
}) => {
  // Status Menu Anchor State
  const [anchorEl, setAnchorEl] = useState(null);
  const [selectedNomination, setSelectedNomination] = useState(null);

  // Schedule Clash Modal State
  const [clashModalOfficer, setClashModalOfficer] = useState(null);
  const [clashProgrammeId, setClashProgrammeId] = useState(null);

  // Form state
  const [formData, setFormData] = useState({
    programmeId: '',
    officerId: '',
    nominatingDepartmentId: '',
  });
  const [duplicateError, setDuplicateError] = useState(null);
  const [formError, setFormError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const handleOpenNominate = () => {
    setFormData({
      programmeId: programmes[0]?.programmeId || '',
      officerId: officers[0]?.officerId || '',
      nominatingDepartmentId: officers[0]?.departmentId || departments[0]?.departmentId || '',
    });
    setDuplicateError(null);
    setFormError(null);
    setOpenNominateDialog(true);
  };

  const handleOfficerChange = (officerId) => {
    const selectedOff = officers.find((o) => o.officerId === Number(officerId));
    setFormData((prev) => ({
      ...prev,
      officerId,
      nominatingDepartmentId: selectedOff ? selectedOff.departmentId : prev.nominatingDepartmentId,
    }));
  };

  const handleNominateSubmit = async (e) => {
    e.preventDefault();
    if (!formData.programmeId || !formData.officerId || !formData.nominatingDepartmentId) {
      setFormError('Please select programme, officer, and nominating department.');
      return;
    }

    setSubmitting(true);
    setDuplicateError(null);
    setFormError(null);

    try {
      const res = await apiService.nominate({
        programmeId: Number(formData.programmeId),
        officerId: Number(formData.officerId),
        nominatingDepartmentId: Number(formData.nominatingDepartmentId),
      });

      showNotification(res.message || 'Nomination submitted successfully!', 'success');
      setOpenNominateDialog(false);
      onRefresh();
    } catch (err) {
      if (err.status === 409 || err.message?.includes('already actively nominated')) {
        setDuplicateError(err.message);
      } else {
        setFormError(err.message || 'Failed to submit nomination');
      }
    } finally {
      setSubmitting(false);
    }
  };

  const handleStatusMenuOpen = (event, nomination) => {
    setAnchorEl(event.currentTarget);
    setSelectedNomination(nomination);
  };

  const handleStatusMenuClose = () => {
    setAnchorEl(null);
    setSelectedNomination(null);
  };

  const handleUpdateStatus = async (newStatus) => {
    if (!selectedNomination) return;
    try {
      await apiService.updateNominationStatus(selectedNomination.nominationId, newStatus);
      showNotification(`Nomination status updated to ${newStatus}`, 'success');
      onRefresh();
    } catch (err) {
      showNotification(err.message || 'Failed to update status', 'error');
    } finally {
      handleStatusMenuClose();
    }
  };

  const handleWithdraw = async (nominationId) => {
    try {
      await apiService.withdrawNomination(nominationId);
      showNotification('Nomination withdrawn successfully', 'info');
      onRefresh();
    } catch (err) {
      showNotification(err.message || 'Failed to withdraw nomination', 'error');
    }
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box>
          <Typography variant="h5" sx={{ fontWeight: 700 }}>
            Officer Nominations Management
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Submit officer nominations, resolve duplicate nomination conflicts, manage waitlists & approvals.
          </Typography>
        </Box>
        <Button variant="contained" startIcon={<AddIcon />} onClick={handleOpenNominate}>
          Submit New Nomination
        </Button>
      </Box>

      {/* Nominations Data Table */}
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Officer</TableCell>
              <TableCell>Nominating Department</TableCell>
              <TableCell>Training Programme</TableCell>
              <TableCell>Nominated Date</TableCell>
              <TableCell>Status</TableCell>
              <TableCell align="right">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {nominations.map((nom) => {
              const officerObj = officers.find((o) => o.officerId === nom.officerId);

              return (
                <TableRow key={nom.nominationId} hover>
                  <TableCell sx={{ fontWeight: 700, color: 'text.secondary' }}>#{nom.nominationId}</TableCell>

                  <TableCell>
                    <Typography variant="subtitle2" sx={{ fontWeight: 700 }}>
                      {nom.officerName}
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      Officer ID: #{nom.officerId}
                    </Typography>
                  </TableCell>

                  <TableCell>
                    <Typography variant="body2" sx={{ fontWeight: 600 }}>
                      {nom.nominatingDepartmentName}
                    </Typography>
                  </TableCell>

                  <TableCell sx={{ maxWidth: 260 }}>
                    <Typography variant="body2" sx={{ fontWeight: 600 }}>
                      {nom.programmeTitle}
                    </Typography>
                  </TableCell>

                  <TableCell sx={{ whiteSpace: 'nowrap' }}>
                    <Typography variant="body2" color="text.secondary">
                      {new Date(nom.nominatedAt).toLocaleString(undefined, {
                        dateStyle: 'medium',
                        timeStyle: 'short',
                      })}
                    </Typography>
                  </TableCell>

                  <TableCell>
                    <StatusChip status={nom.status} />
                  </TableCell>

                  <TableCell align="right">
                    <Tooltip title="Inspect Schedule Clashes">
                      <IconButton
                        color="info"
                        onClick={() => {
                          setClashModalOfficer(
                            officerObj || { officerId: nom.officerId, fullName: nom.officerName }
                          );
                          setClashProgrammeId(nom.programmeId);
                        }}
                      >
                        <EventBusyIcon />
                      </IconButton>
                    </Tooltip>
                    <IconButton onClick={(e) => handleStatusMenuOpen(e, nom)}>
                      <MoreVertIcon />
                    </IconButton>
                  </TableCell>
                </TableRow>
              );
            })}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Status Menu Dropdown */}
      <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={handleStatusMenuClose}>
        <MenuItem onClick={() => handleUpdateStatus('APPROVED')} sx={{ color: 'success.main', fontWeight: 600 }}>
          Approve Nomination
        </MenuItem>
        <MenuItem onClick={() => handleUpdateStatus('REJECTED')} sx={{ color: 'error.main' }}>
          Reject Nomination
        </MenuItem>
        <MenuItem onClick={() => handleUpdateStatus('WAITLISTED')} sx={{ color: 'info.main' }}>
          Place on Waitlist
        </MenuItem>
        <MenuItem onClick={() => handleWithdraw(selectedNomination?.nominationId)} sx={{ color: 'text.secondary' }}>
          Withdraw Nomination
        </MenuItem>
      </Menu>

      {/* Submit Nomination Dialog */}
      <Dialog open={openNominateDialog} onClose={() => setOpenNominateDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>Submit Officer Nomination</DialogTitle>
        <form onSubmit={handleNominateSubmit}>
          <DialogContent dividers>
            {/* Duplicate 409 Error Alert */}
            {duplicateError && (
              <Alert severity="error" sx={{ mb: 2, fontWeight: 500 }}>
                <strong>DUPLICATE NOMINATION DETECTED (409 Conflict):</strong>
                <br />
                {duplicateError}
              </Alert>
            )}

            {/* General Error Alert */}
            {formError && (
              <Alert severity="error" sx={{ mb: 2 }}>
                {formError}
              </Alert>
            )}

            <Grid container spacing={2.5}>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  select
                  required
                  label="Select Training Programme"
                  value={formData.programmeId}
                  onChange={(e) => setFormData({ ...formData, programmeId: e.target.value })}
                >
                  {programmes.map((p) => (
                    <MenuItem key={p.programmeId} value={p.programmeId}>
                      {p.title} (Available Seats: {p.availableSeats ?? p.maxParticipants})
                    </MenuItem>
                  ))}
                </TextField>
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  select
                  required
                  label="Select Officer"
                  value={formData.officerId}
                  onChange={(e) => handleOfficerChange(e.target.value)}
                >
                  {officers.map((o) => (
                    <MenuItem key={o.officerId} value={o.officerId}>
                      {o.fullName} — {o.designation} ({o.departmentName})
                    </MenuItem>
                  ))}
                </TextField>
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  select
                  required
                  label="Nominating Department"
                  value={formData.nominatingDepartmentId}
                  onChange={(e) => setFormData({ ...formData, nominatingDepartmentId: e.target.value })}
                >
                  {departments.map((d) => (
                    <MenuItem key={d.departmentId} value={d.departmentId}>
                      {d.name}
                    </MenuItem>
                  ))}
                </TextField>
              </Grid>
            </Grid>
          </DialogContent>

          <DialogActions sx={{ p: 2 }}>
            <Button onClick={() => setOpenNominateDialog(false)}>Cancel</Button>
            <Button type="submit" variant="contained" color="secondary" disabled={submitting}>
              {submitting ? 'Submitting...' : 'Submit Nomination'}
            </Button>
          </DialogActions>
        </form>
      </Dialog>

      {/* Schedule Clash Inspection Dialog */}
      <ScheduleClashDialog
        open={Boolean(clashModalOfficer)}
        onClose={() => setClashModalOfficer(null)}
        officer={clashModalOfficer}
        currentProgrammeId={clashProgrammeId}
      />
    </Box>
  );
};

export default NominationsView;
