import React from 'react';
import { Chip } from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import HourglassEmptyIcon from '@mui/icons-material/HourglassEmpty';
import CancelIcon from '@mui/icons-material/Cancel';
import RemoveCircleOutlineIcon from '@mui/icons-material/RemoveCircleOutline';
import FormatListNumberedIcon from '@mui/icons-material/FormatListNumbered';

const StatusChip = ({ status, size = 'small' }) => {
  let color = 'default';
  let icon = null;
  let label = status;

  switch (status) {
    case 'APPROVED':
      color = 'success';
      icon = <CheckCircleIcon style={{ fontSize: 16 }} />;
      break;
    case 'PENDING':
      color = 'warning';
      icon = <HourglassEmptyIcon style={{ fontSize: 16 }} />;
      break;
    case 'WAITLISTED':
      color = 'info';
      icon = <FormatListNumberedIcon style={{ fontSize: 16 }} />;
      break;
    case 'REJECTED':
      color = 'error';
      icon = <CancelIcon style={{ fontSize: 16 }} />;
      break;
    case 'WITHDRAWN':
      color = 'default';
      icon = <RemoveCircleOutlineIcon style={{ fontSize: 16 }} />;
      break;
    default:
      color = 'default';
  }

  return (
    <Chip
      icon={icon}
      label={label}
      color={color}
      size={size}
      variant="soft"
      sx={{
        fontWeight: 600,
        fontSize: size === 'small' ? '0.75rem' : '0.875rem',
      }}
    />
  );
};

export default StatusChip;
