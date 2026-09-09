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

const OfficersDeptView = ({ officers, departments, onRefresh, showNotification }) => {
  const [tabIndex, setTabIndex] = useState(0);

  // Officer Form State
  const [openOfficerDialog, setOpenOfficerDialog] = useState(false);
  const [officerForm, setOfficerForm] = useState({
    fullName: '',
    nic: '',
    departmentId: '',
    designation: '',
    email: '',
    phone: '',
  });

  // Dept Form State
  const [openDeptDialog, setOpenDeptDialog] = useState(false);
  const [deptForm, setDeptForm] = useState({
    name: '',
    focalPointEmail: '',
    focalPointPhone: '',
  });

  const [formError, setFormError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  // Handlers for Officers
  const handleOpenOfficerModal = () => {
    setOfficerForm({
      fullName: '',
      nic: '',
      departmentId: departments[0]?.departmentId || '',
      designation: '',
      email: '',
      phone: '',
    });
    setFormError(null);
    setOpenOfficerDialog(true);
  };

  const handleOfficerSubmit = async (e) => {
    e.preventDefault();
    if (!officerForm.fullName || !officerForm.departmentId) {
      setFormError('Full name and department are required.');
      return;
    }
    setSubmitting(true);
    setFormError(null);
    try {
      await apiService.createOfficer({
        ...officerForm,
        departmentId: Number(officerForm.departmentId),
      });
      showNotification('Officer registered successfully!', 'success');
      setOpenOfficerDialog(false);
      onRefresh();
    } catch (err) {
      setFormError(err.message || 'Failed to add officer');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeleteOfficer = async (id) => {
    if (window.confirm('Delete officer record?')) {
      try {
        await apiService.deleteOfficer(id);
        showNotification('Officer deleted', 'info');
        onRefresh();
      } catch (err) {
        showNotification(err.message || 'Failed to delete officer', 'error');
      }
    }
  };

  // Handlers for Departments
  const handleOpenDeptModal = () => {
    setDeptForm({ name: '', focalPointEmail: '', focalPointPhone: '' });
    setFormError(null);
    setOpenDeptDialog(true);
  };

  const handleDeptSubmit = async (e) => {
    e.preventDefault();
    if (!deptForm.name) {
      setFormError('Department name is required.');
      return;
    }
    setSubmitting(true);
    setFormError(null);
    try {
      await apiService.createDepartment(deptForm);
      showNotification('Department added successfully!', 'success');
      setOpenDeptDialog(false);
      onRefresh();
    } catch (err) {
      setFormError(err.message || 'Failed to add department');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeleteDept = async (id) => {
    if (window.confirm('Delete department record?')) {
      try {
        await apiService.deleteDepartment(id);
        showNotification('Department deleted', 'info');
        onRefresh();
      } catch (err) {
        showNotification(err.message || 'Failed to delete department', 'error');
      }
    }
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box>
          <Typography variant="h5" sx={{ fontWeight: 700 }}>
            Officers & Departments Directory
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Manage government officers, designation details, and nominating departments.
          </Typography>
        </Box>
        {tabIndex === 0 ? (
          <Button variant="contained" startIcon={<AddIcon />} onClick={handleOpenOfficerModal}>
            Add New Officer
          </Button>
        ) : (
          <Button variant="contained" startIcon={<AddIcon />} onClick={handleOpenDeptModal}>
            Add Department
          </Button>
        )}
      </Box>

      {/* Tabs */}
      <Paper sx={{ mb: 3 }}>
        <Tabs value={tabIndex} onChange={(e, val) => setTabIndex(val)} indicatorColor="primary" textColor="primary">
          <Tab label={`Officers (${officers.length})`} sx={{ fontWeight: 700 }} />
          <Tab label={`Departments (${departments.length})`} sx={{ fontWeight: 700 }} />
        </Tabs>
      </Paper>

      {/* TAB 0: OFFICERS TABLE */}
      {tabIndex === 0 && (
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>Officer Name</TableCell>
                <TableCell>NIC</TableCell>
                <TableCell>Department</TableCell>
                <TableCell>Designation</TableCell>
                <TableCell>Contact Info</TableCell>
                <TableCell align="right">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {officers.map((off) => (
                <TableRow key={off.officerId} hover>
                  <TableCell sx={{ fontWeight: 700, color: 'text.secondary' }}>#{off.officerId}</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>{off.fullName}</TableCell>
                  <TableCell>{off.nic || 'N/A'}</TableCell>
                  <TableCell>{off.departmentName}</TableCell>
                  <TableCell>{off.designation || 'N/A'}</TableCell>
                  <TableCell>
                    <Typography variant="body2">{off.email}</Typography>
                    <Typography variant="caption" color="text.secondary">
                      {off.phone}
                    </Typography>
                  </TableCell>
                  <TableCell align="right">
                    <IconButton color="error" onClick={() => handleDeleteOfficer(off.officerId)}>
                      <DeleteOutlineIcon />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      {/* TAB 1: DEPARTMENTS TABLE */}
      {tabIndex === 1 && (
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>Department Name</TableCell>
                <TableCell>Focal Point Email</TableCell>
                <TableCell>Focal Point Phone</TableCell>
                <TableCell align="right">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {departments.map((dept) => (
                <TableRow key={dept.departmentId} hover>
                  <TableCell sx={{ fontWeight: 700, color: 'text.secondary' }}>#{dept.departmentId}</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>{dept.name}</TableCell>
                  <TableCell>{dept.focalPointEmail || 'N/A'}</TableCell>
                  <TableCell>{dept.focalPointPhone || 'N/A'}</TableCell>
                  <TableCell align="right">
                    <IconButton color="error" onClick={() => handleDeleteDept(dept.departmentId)}>
                      <DeleteOutlineIcon />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      {/* Add Officer Modal */}
      <Dialog open={openOfficerDialog} onClose={() => setOpenOfficerDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>Add New Officer</DialogTitle>
        <form onSubmit={handleOfficerSubmit}>
          <DialogContent dividers>
            {formError && <Alert severity="error" sx={{ mb: 2 }}>{formError}</Alert>}
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  required
                  label="Full Name"
                  value={officerForm.fullName}
                  onChange={(e) => setOfficerForm({ ...officerForm, fullName: e.target.value })}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="NIC Number"
                  value={officerForm.nic}
                  onChange={(e) => setOfficerForm({ ...officerForm, nic: e.target.value })}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  select
                  required
                  label="Department"
                  value={officerForm.departmentId}
                  onChange={(e) => setOfficerForm({ ...officerForm, departmentId: e.target.value })}
                >
                  {departments.map((d) => (
                    <MenuItem key={d.departmentId} value={d.departmentId}>
                      {d.name}
                    </MenuItem>
                  ))}
                </TextField>
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Designation / Position"
                  value={officerForm.designation}
                  onChange={(e) => setOfficerForm({ ...officerForm, designation: e.target.value })}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  type="email"
                  label="Email Address"
                  value={officerForm.email}
                  onChange={(e) => setOfficerForm({ ...officerForm, email: e.target.value })}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Phone Number"
                  value={officerForm.phone}
                  onChange={(e) => setOfficerForm({ ...officerForm, phone: e.target.value })}
                />
              </Grid>
            </Grid>
          </DialogContent>
          <DialogActions sx={{ p: 2 }}>
            <Button onClick={() => setOpenOfficerDialog(false)}>Cancel</Button>
            <Button type="submit" variant="contained" disabled={submitting}>
              {submitting ? 'Saving...' : 'Save Officer'}
            </Button>
          </DialogActions>
        </form>
      </Dialog>

      {/* Add Department Modal */}
      <Dialog open={openDeptDialog} onClose={() => setOpenDeptDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>Add New Department</DialogTitle>
        <form onSubmit={handleDeptSubmit}>
          <DialogContent dividers>
            {formError && <Alert severity="error" sx={{ mb: 2 }}>{formError}</Alert>}
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  required
                  label="Department Name"
                  value={deptForm.name}
                  onChange={(e) => setDeptForm({ ...deptForm, name: e.target.value })}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  type="email"
                  label="Focal Point Email"
                  value={deptForm.focalPointEmail}
                  onChange={(e) => setDeptForm({ ...deptForm, focalPointEmail: e.target.value })}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Focal Point Phone"
                  value={deptForm.focalPointPhone}
                  onChange={(e) => setDeptForm({ ...deptForm, focalPointPhone: e.target.value })}
                />
              </Grid>
            </Grid>
          </DialogContent>
          <DialogActions sx={{ p: 2 }}>
            <Button onClick={() => setOpenDeptDialog(false)}>Cancel</Button>
            <Button type="submit" variant="contained" disabled={submitting}>
              {submitting ? 'Saving...' : 'Save Department'}
            </Button>
          </DialogActions>
        </form>
      </Dialog>
    </Box>
  );
};

export default OfficersDeptView;
