import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Typography,
  Alert,
  Box,
  CircularProgress,
} from '@mui/material';
import EventBusyIcon from '@mui/icons-material/EventBusy';
import StatusChip from './StatusChip';
import { apiService } from '../api/apiService';

const ScheduleClashDialog = ({ open, onClose, officer, currentProgrammeId }) => {
  const [loading, setLoading] = useState(false);
  const [clashes, setClashes] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (open && officer) {
      fetchClashes();
    }
  }, [open, officer, currentProgrammeId]);

  const fetchClashes = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await apiService.findClashesForOfficer(
        officer.officerId,
        currentProgrammeId || 0
      );
      setClashes(res.data || []);
    } catch (err) {
      setError(err.message || 'Failed to fetch active nominations for schedule clash verification');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
      <DialogTitle sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
        <EventBusyIcon color="info" />
        <Box>
          <Typography variant="h6" sx={{ fontWeight: 700 }}>
            Officer Active Nominations & Schedule Check
          </Typography>
          <Typography variant="caption" color="text.secondary">
            Officer: {officer?.fullName} ({officer?.departmentName || 'Dept'})
          </Typography>
        </Box>
      </DialogTitle>

      <DialogContent dividers>
        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
            <CircularProgress />
          </Box>
        ) : error ? (
          <Alert severity="error">{error}</Alert>
        ) : clashes.length === 0 ? (
          <Alert severity="success" icon={false}>
            No other active training programme nominations found for this officer. No date clashes detected!
          </Alert>
        ) : (
          <Box>
            <Alert severity="info" sx={{ mb: 2 }}>
              Found <strong>{clashes.length}</strong> other active nomination(s) for this officer. Please check programme start & end dates below to prevent schedule overlaps.
            </Alert>
            <TableContainer component={Paper} variant="outlined">
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>Nomination ID</TableCell>
                    <TableCell>Programme Title</TableCell>
                    <TableCell>Nominating Dept</TableCell>
                    <TableCell>Status</TableCell>
                    <TableCell>Nominated Date</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {clashes.map((nom) => (
                    <TableRow key={nom.nominationId}>
                      <TableCell>#{nom.nominationId}</TableCell>
                      <TableCell sx={{ fontWeight: 600 }}>{nom.programmeTitle}</TableCell>
                      <TableCell>{nom.nominatingDepartmentName}</TableCell>
                      <TableCell>
                        <StatusChip status={nom.status} />
                      </TableCell>
                      <TableCell>{new Date(nom.nominatedAt).toLocaleDateString()}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </Box>
        )}
      </DialogContent>

      <DialogActions sx={{ p: 2 }}>
        <Button onClick={onClose} variant="contained">
          Close Check
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default ScheduleClashDialog;
