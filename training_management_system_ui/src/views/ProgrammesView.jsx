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
  Chip,
  IconButton,
  Tooltip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  MenuItem,
  Grid,
  LinearProgress,
  Alert,
  FormGroup,
  FormControlLabel,
  Checkbox,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import WarningAmberIcon from '@mui/icons-material/WarningAmber';
import EventIcon from '@mui/icons-material/Event';
import DuplicateCheckDialog from '../components/DuplicateCheckDialog';
import { apiService } from '../api/apiService';

const ProgrammesView = ({ programmes, venues, trainers, departments, onRefresh, showNotification }) => {
  const [openCreate, setOpenCreate] = useState(false);
  const [duplicateModalProg, setDuplicateModalProg] = useState(null);

  // Form State
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    startDate: '',
    endDate: '',
    venueId: '',
    trainerId: '',
    maxParticipants: 15,
    targetDepartmentIds: [],
  });
  const [formError, setFormError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const handleOpenCreate = () => {
    setFormData({
      title: '',
      description: '',
      startDate: new Date().toISOString().split('T')[0],
      endDate: new Date(Date.now() + 5 * 86400000).toISOString().split('T')[0],
      venueId: venues[0]?.venueId || '',
      trainerId: trainers[0]?.trainerId || '',
      maxParticipants: 15,
      targetDepartmentIds: departments.map((d) => d.departmentId),
    });
    setFormError(null);
    setOpenCreate(true);
  };

  const handleToggleDept = (deptId) => {
    setFormData((prev) => {
      const exists = prev.targetDepartmentIds.includes(deptId);
      const updated = exists
        ? prev.targetDepartmentIds.filter((id) => id !== deptId)
        : [...prev.targetDepartmentIds, deptId];
      return { ...prev, targetDepartmentIds: updated };
    });
  };

  const handleCreateSubmit = async (e) => {
    e.preventDefault();
    if (!formData.title || !formData.startDate || !formData.endDate || !formData.maxParticipants) {
      setFormError('Please fill in all required fields.');
      return;
    }

    setSubmitting(true);
    setFormError(null);
    try {
      await apiService.createProgramme({
        ...formData,
        maxParticipants: Number(formData.maxParticipants),
        venueId: formData.venueId ? Number(formData.venueId) : null,
        trainerId: formData.trainerId ? Number(formData.trainerId) : null,
      });
      showNotification('Training Programme created successfully!', 'success');
      setOpenCreate(false);
      onRefresh();
    } catch (err) {
      setFormError(err.message || 'Failed to create programme');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this training programme?')) {
      try {
        await apiService.deleteProgramme(id);
        showNotification('Programme deleted successfully', 'info');
        onRefresh();
      } catch (err) {
        showNotification(err.message || 'Failed to delete programme', 'error');
      }
    }
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box>
          <Typography variant="h5" sx={{ fontWeight: 700 }}>
            Training Programmes Management
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Configure upcoming courses, set max capacities, assign venues/trainers, and check duplicates.
          </Typography>
        </Box>
        <Button variant="contained" startIcon={<AddIcon />} onClick={handleOpenCreate}>
          Add Training Programme
        </Button>
      </Box>

      {/* Programmes Table */}
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Title & Description</TableCell>
              <TableCell>Schedule</TableCell>
              <TableCell>Venue & Trainer</TableCell>
              <TableCell sx={{ minWidth: 160 }}>Capacity & Seats</TableCell>
              <TableCell>Target Depts</TableCell>
              <TableCell align="right">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {programmes.map((prog) => {
              const approved = prog.approvedCount || 0;
              const max = prog.maxParticipants || 1;
              const seatsLeft = prog.availableSeats ?? Math.max(0, max - approved);
              const percentage = Math.min(100, Math.round((approved / max) * 100));

              return (
                <TableRow key={prog.programmeId} hover>
                  <TableCell sx={{ maxWidth: 280 }}>
                    <Typography variant="subtitle2" sx={{ fontWeight: 700 }}>
                      {prog.title}
                    </Typography>
                    <Typography variant="caption" color="text.secondary" noWrap display="block">
                      {prog.description}
                    </Typography>
                  </TableCell>

                  <TableCell sx={{ whiteSpace: 'nowrap' }}>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                      <EventIcon style={{ fontSize: 16, color: '#64748b' }} />
                      <Typography variant="body2" sx={{ fontWeight: 600 }}>
                        {prog.startDate}
                      </Typography>
                    </Box>
                    <Typography variant="caption" color="text.secondary">
                      to {prog.endDate}
                    </Typography>
                  </TableCell>

                  <TableCell>
                    <Typography variant="body2" sx={{ fontWeight: 600 }}>
                      {prog.venue?.name || 'No Venue'}
                    </Typography>
                    <Typography variant="caption" color="text.secondary" display="block">
                      Trainer: {prog.trainer?.fullName || 'Unassigned'}
                    </Typography>
                  </TableCell>

                  <TableCell>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 0.5 }}>
                      <Typography variant="caption" sx={{ fontWeight: 700 }}>
                        {approved} / {max} Approved
                      </Typography>
                      <Chip
                        label={seatsLeft === 0 ? 'FULL' : `${seatsLeft} Left`}
                        size="small"
                        color={seatsLeft === 0 ? 'warning' : 'success'}
                        sx={{ height: 18, fontSize: '0.65rem', fontWeight: 700 }}
                      />
                    </Box>
                    <LinearProgress
                      variant="determinate"
                      value={percentage}
                      color={seatsLeft === 0 ? 'warning' : 'primary'}
                      sx={{ height: 6, borderRadius: 3 }}
                    />
                  </TableCell>

                  <TableCell>
                    <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5, maxWidth: 200 }}>
                      {prog.targetDepartments?.map((d) => (
                        <Chip
                          key={d.departmentId}
                          label={d.name}
                          size="small"
                          variant="outlined"
                          sx={{ fontSize: '0.65rem', height: 20 }}
                        />
                      ))}
                    </Box>
                  </TableCell>

                  <TableCell align="right">
                    <Tooltip title="Check Duplicate Nominations (Safety Net)">
                      <IconButton color="warning" onClick={() => setDuplicateModalProg(prog)}>
                        <WarningAmberIcon />
                      </IconButton>
                    </Tooltip>
                    <Tooltip title="Delete Programme">
                      <IconButton color="error" onClick={() => handleDelete(prog.programmeId)}>
                        <DeleteOutlineIcon />
                      </IconButton>
                    </Tooltip>
                  </TableCell>
                </TableRow>
              );
            })}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Add Programme Dialog */}
      <Dialog open={openCreate} onClose={() => setOpenCreate(false)} maxWidth="md" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>Create New Training Programme</DialogTitle>
        <form onSubmit={handleCreateSubmit}>
          <DialogContent dividers>
            {formError && (
              <Alert severity="error" sx={{ mb: 2 }}>
                {formError}
              </Alert>
            )}

            <Grid container spacing= {2}>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  required
                  label="Programme Title"
                  value={formData.title}
                  onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  multiline
                  rows={2}
                  label="Description"
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  required
                  type="date"
                  label="Start Date"
                  InputLabelProps={{ shrink: true }}
                  value={formData.startDate}
                  onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  required
                  type="date"
                  label="End Date"
                  InputLabelProps={{ shrink: true }}
                  value={formData.endDate}
                  onChange={(e) => setFormData({ ...formData, endDate: e.target.value })}
                />
              </Grid>

              <Grid item xs={12} sm={4}>
                <TextField
                  fullWidth
                  select
                  label="Venue"
                  value={formData.venueId}
                  onChange={(e) => setFormData({ ...formData, venueId: e.target.value })}
                >
                  <MenuItem value="">-- Select Venue --</MenuItem>
                  {venues.map((v) => (
                    <MenuItem key={v.venueId} value={v.venueId}>
                      {v.name} (Cap: {v.capacity})
                    </MenuItem>
                  ))}
                </TextField>
              </Grid>

              <Grid item xs={12} sm={4}>
                <TextField
                  fullWidth
                  select
                  label="Trainer"
                  value={formData.trainerId}
                  onChange={(e) => setFormData({ ...formData, trainerId: e.target.value })}
                >
                  <MenuItem value="">-- Select Trainer --</MenuItem>
                  {trainers.map((t) => (
                    <MenuItem key={t.trainerId} value={t.trainerId}>
                      {t.fullName} ({t.type})
                    </MenuItem>
                  ))}
                </TextField>
              </Grid>

              <Grid item xs={12} sm={4}>
                <TextField
                  fullWidth
                  required
                  type="number"
                  label="Max Participants"
                  inputProps={{ min: 1 }}
                  value={formData.maxParticipants}
                  onChange={(e) => setFormData({ ...formData, maxParticipants: e.target.value })}
                />
              </Grid>

              <Grid item xs={12}>
                <Typography variant="subtitle2" sx={{ fontWeight: 600, mt: 1, mb: 0.5 }}>
                  Target Eligible Departments:
                </Typography>
                <FormGroup row>
                  {departments.map((d) => (
                    <FormControlLabel
                      key={d.departmentId}
                      control={
                        <Checkbox
                          checked={formData.targetDepartmentIds.includes(d.departmentId)}
                          onChange={() => handleToggleDept(d.departmentId)}
                        />
                      }
                      label={d.name}
                    />
                  ))}
                </FormGroup>
              </Grid>
            </Grid>
          </DialogContent>

          <DialogActions sx={{ p: 2 }}>
            <Button onClick={() => setOpenCreate(false)}>Cancel</Button>
            <Button type="submit" variant="contained" disabled={submitting}>
              {submitting ? 'Creating...' : 'Create Programme'}
            </Button>
          </DialogActions>
        </form>
      </Dialog>

      {/* Duplicate Nominations Report Dialog */}
      <DuplicateCheckDialog
        open={Boolean(duplicateModalProg)}
        onClose={() => setDuplicateModalProg(null)}
        programme={duplicateModalProg}
      />
    </Box>
  );
};

export default ProgrammesView;
