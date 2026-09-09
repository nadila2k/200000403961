import React, { useState } from 'react';
import {
  Box,
  Typography,
  Paper,
  Tabs,
  Tab,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Chip,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  MenuItem,
  Grid,
  Alert,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import { apiService } from '../api/apiService';

const TrainersVenuesView = ({ trainers, venues, onRefresh, showNotification }) => {
  const [tabIndex, setTabIndex] = useState(0);

  // Trainer Form
  const [openTrainerDialog, setOpenTrainerDialog] = useState(false);
  const [trainerForm, setTrainerForm] = useState({
    fullName: '',
    type: 'INTERNAL',
    specialization: '',
    organization: '',
    email: '',
    phone: '',
  });

  // Venue Form
  const [openVenueDialog, setOpenVenueDialog] = useState(false);
  const [venueForm, setVenueForm] = useState({
    name: '',
    location: '',
    capacity: 30,
  });

  const [formError, setFormError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  // Trainer Handlers
  const handleOpenTrainerModal = () => {
    setTrainerForm({
      fullName: '',
      type: 'INTERNAL',
      specialization: '',
      organization: '',
      email: '',
      phone: '',
    });
    setFormError(null);
    setOpenTrainerDialog(true);
  };

  const handleTrainerSubmit = async (e) => {
    e.preventDefault();
    if (!trainerForm.fullName || !trainerForm.type) {
      setFormError('Trainer name and type are required.');
      return;
    }
    setSubmitting(true);
    setFormError(null);
    try {
      await apiService.createTrainer(trainerForm);
      showNotification('Trainer added successfully!', 'success');
      setOpenTrainerDialog(false);
      onRefresh();
    } catch (err) {
      setFormError(err.message || 'Failed to add trainer');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeleteTrainer = async (id) => {
    if (window.confirm('Delete trainer record?')) {
      try {
        await apiService.deleteTrainer(id);
        showNotification('Trainer deleted', 'info');
        onRefresh();
      } catch (err) {
        showNotification(err.message || 'Failed to delete trainer', 'error');
      }
    }
  };

  // Venue Handlers
  const handleOpenVenueModal = () => {
    setVenueForm({ name: '', location: '', capacity: 30 });
    setFormError(null);
    setOpenVenueDialog(true);
  };

  const handleVenueSubmit = async (e) => {
    e.preventDefault();
    if (!venueForm.name || !venueForm.capacity) {
      setFormError('Venue name and capacity are required.');
      return;
    }
    setSubmitting(true);
    setFormError(null);
    try {
      await apiService.createVenue(venueForm);
      showNotification('Venue added successfully!', 'success');
      setOpenVenueDialog(false);
      onRefresh();
    } catch (err) {
      setFormError(err.message || 'Failed to add venue');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeleteVenue = async (id) => {
    if (window.confirm('Delete venue record?')) {
      try {
        await apiService.deleteVenue(id);
        showNotification('Venue deleted', 'info');
        onRefresh();
      } catch (err) {
        showNotification(err.message || 'Failed to delete venue', 'error');
      }
    }
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box>
          <Typography variant="h5" sx={{ fontWeight: 700 }}>
            Trainers & Venues Resources
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Manage course instructors (internal/external) and training facility locations & seating capacities.
          </Typography>
        </Box>
        {tabIndex === 0 ? (
          <Button variant="contained" startIcon={<AddIcon />} onClick={handleOpenTrainerModal}>
            Add Trainer
          </Button>
        ) : (
          <Button variant="contained" startIcon={<AddIcon />} onClick={handleOpenVenueModal}>
            Add Venue
          </Button>
        )}
      </Box>

      {/* Tabs */}
      <Paper sx={{ mb: 3 }}>
        <Tabs value={tabIndex} onChange={(e, val) => setTabIndex(val)} indicatorColor="primary" textColor="primary">
          <Tab label={`Trainers (${trainers.length})`} sx={{ fontWeight: 700 }} />
          <Tab label={`Venues (${venues.length})`} sx={{ fontWeight: 700 }} />
        </Tabs>
      </Paper>

      {/* TAB 0: TRAINERS */}
      {tabIndex === 0 && (
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>Full Name</TableCell>
                <TableCell>Type</TableCell>
                <TableCell>Specialization</TableCell>
                <TableCell>Organization</TableCell>
                <TableCell>Contact Info</TableCell>
                <TableCell align="right">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {trainers.map((t) => (
                <TableRow key={t.trainerId} hover>
                  <TableCell sx={{ fontWeight: 700, color: 'text.secondary' }}>#{t.trainerId}</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>{t.fullName}</TableCell>
                  <TableCell>
                    <Chip
                      label={t.type}
                      color={t.type === 'INTERNAL' ? 'primary' : 'secondary'}
                      size="small"
                      sx={{ fontWeight: 700, fontSize: '0.7rem' }}
                    />
                  </TableCell>
                  <TableCell>{t.specialization || 'N/A'}</TableCell>
                  <TableCell>{t.organization || 'N/A'}</TableCell>
                  <TableCell>
                    <Typography variant="body2">{t.email}</Typography>
                    <Typography variant="caption" color="text.secondary">
                      {t.phone}
                    </Typography>
                  </TableCell>
                  <TableCell align="right">
                    <IconButton color="error" onClick={() => handleDeleteTrainer(t.trainerId)}>
                      <DeleteOutlineIcon />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      {/* TAB 1: VENUES */}
      {tabIndex === 1 && (
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>Venue Name</TableCell>
                <TableCell>Location / Floor</TableCell>
                <TableCell>Seating Capacity</TableCell>
                <TableCell align="right">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {venues.map((v) => (
                <TableRow key={v.venueId} hover>
                  <TableCell sx={{ fontWeight: 700, color: 'text.secondary' }}>#{v.venueId}</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>{v.name}</TableCell>
                  <TableCell>{v.location || 'N/A'}</TableCell>
                  <TableCell>
                    <Chip label={`${v.capacity} Seats`} size="small" variant="outlined" sx={{ fontWeight: 700 }} />
                  </TableCell>
                  <TableCell align="right">
                    <IconButton color="error" onClick={() => handleDeleteVenue(v.venueId)}>
                      <DeleteOutlineIcon />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      {/* Add Trainer Dialog */}
      <Dialog open={openTrainerDialog} onClose={() => setOpenTrainerDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>Add New Trainer</DialogTitle>
        <form onSubmit={handleTrainerSubmit}>
          <DialogContent dividers>
            {formError && <Alert severity="error" sx={{ mb: 2 }}>{formError}</Alert>}
            <Grid container spacing={2}>
              <Grid item xs={12} sm={8}>
                <TextField
                  fullWidth
                  required
                  label="Trainer Full Name"
                  value={trainerForm.fullName}
                  onChange={(e) => setTrainerForm({ ...trainerForm, fullName: e.target.value })}
                />
              </Grid>
              <Grid item xs={12} sm={4}>
                <TextField
                  fullWidth
                  select
                  required
                  label="Trainer Type"
                  value={trainerForm.type}
                  onChange={(e) => setTrainerForm({ ...trainerForm, type: e.target.value })}
                >
                  <MenuItem value="INTERNAL">INTERNAL</MenuItem>
                  <MenuItem value="EXTERNAL">EXTERNAL</MenuItem>
                </TextField>
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Specialization / Expertise"
                  value={trainerForm.specialization}
                  onChange={(e) => setTrainerForm({ ...trainerForm, specialization: e.target.value })}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Organization / Department"
                  value={trainerForm.organization}
                  onChange={(e) => setTrainerForm({ ...trainerForm, organization: e.target.value })}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  type="email"
                  label="Email"
                  value={trainerForm.email}
                  onChange={(e) => setTrainerForm({ ...trainerForm, email: e.target.value })}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Phone"
                  value={trainerForm.phone}
                  onChange={(e) => setTrainerForm({ ...trainerForm, phone: e.target.value })}
                />
              </Grid>
            </Grid>
          </DialogContent>
          <DialogActions sx={{ p: 2 }}>
            <Button onClick={() => setOpenTrainerDialog(false)}>Cancel</Button>
            <Button type="submit" variant="contained" disabled={submitting}>
              {submitting ? 'Saving...' : 'Save Trainer'}
            </Button>
          </DialogActions>
        </form>
      </Dialog>

      {/* Add Venue Dialog */}
      <Dialog open={openVenueDialog} onClose={() => setOpenVenueDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>Add New Venue</DialogTitle>
        <form onSubmit={handleVenueSubmit}>
          <DialogContent dividers>
            {formError && <Alert severity="error" sx={{ mb: 2 }}>{formError}</Alert>}
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  required
                  label="Venue Name"
                  value={venueForm.name}
                  onChange={(e) => setVenueForm({ ...venueForm, name: e.target.value })}
                />
              </Grid>
              <Grid item xs={12} sm={8}>
                <TextField
                  fullWidth
                  label="Location / Floor / Building"
                  value={venueForm.location}
                  onChange={(e) => setVenueForm({ ...venueForm, location: e.target.value })}
                />
              </Grid>
              <Grid item xs={12} sm={4}>
                <TextField
                  fullWidth
                  required
                  type="number"
                  label="Seating Capacity"
                  inputProps={{ min: 1 }}
                  value={venueForm.capacity}
                  onChange={(e) => setVenueForm({ ...venueForm, capacity: e.target.value })}
                />
              </Grid>
            </Grid>
          </DialogContent>
          <DialogActions sx={{ p: 2 }}>
            <Button onClick={() => setOpenVenueDialog(false)}>Cancel</Button>
            <Button type="submit" variant="contained" disabled={submitting}>
              {submitting ? 'Saving...' : 'Save Venue'}
            </Button>
          </DialogActions>
        </form>
      </Dialog>
    </Box>
  );
};

export default TrainersVenuesView;
