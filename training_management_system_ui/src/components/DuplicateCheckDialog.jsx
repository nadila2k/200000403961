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
import WarningAmberIcon from '@mui/icons-material/WarningAmber';
import StatusChip from './StatusChip';
import { apiService } from '../api/apiService';

const DuplicateCheckDialog = ({ open, onClose, programme }) => {
  const [loading, setLoading] = useState(false);
  const [duplicates, setDuplicates] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (open && programme) {
      fetchDuplicates();
    }
  }, [open, programme]);

  const fetchDuplicates = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await apiService.findDuplicatesForProgramme(programme.programmeId);
      setDuplicates(res.data || []);
    } catch (err) {
      setError(err.message || 'Failed to fetch duplicate nominations report');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
      <DialogTitle sx={{ display: 'flex', alignItems: 'center', gap: 1.5, bg: '#fff7ed' }}>
        <WarningAmberIcon color="warning" />
        <Box>
          <Typography variant="h6" sx={{ fontWeight: 700 }}>
            Duplicate Nominations Safety Net Report
          </Typography>
          <Typography variant="caption" color="text.secondary">
            {programme?.title}
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
        ) : duplicates.length === 0 ? (
          <Alert severity="success" icon={false}>
            No duplicate active nominations found for this programme. All officer nominations are unique across departments.
          </Alert>
        ) : (
          <Box>
            <Alert severity="warning" sx={{ mb: 2 }}>
              Found <strong>{duplicates.length}</strong> active nomination record(s) where an officer has multiple entries for this programme.
            </Alert>
            <TableContainer component={Paper} variant="outlined">
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>Nomination ID</TableCell>
                    <TableCell>Officer</TableCell>
                    <TableCell>Nominating Dept</TableCell>
                    <TableCell>Status</TableCell>
                    <TableCell>Nominated Date</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {duplicates.map((nom) => (
                    <TableRow key={nom.nominationId}>
                      <TableCell>#{nom.nominationId}</TableCell>
                      <TableCell sx={{ fontWeight: 600 }}>{nom.officerName}</TableCell>
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
          Close Report
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default DuplicateCheckDialog;
