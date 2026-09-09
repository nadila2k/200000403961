import React from 'react';
import { Card, CardContent, Typography, Box, Avatar } from '@mui/material';

const StatsCard = ({ title, value, subtitle, icon, iconBgColor = '#2563eb', color = '#1e293b' }) => {
  return (
    <Card sx={{ height: '100%', position: 'relative', overflow: 'hidden' }}>
      <CardContent sx={{ p: 2.5 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 2 }}>
          <Typography variant="body2" color="text.secondary" sx={{ fontWeight: 600, textTransform: 'uppercase', letterSpacing: '0.05em' }}>
            {title}
          </Typography>
          <Avatar
            sx={{
              bgcolor: iconBgColor,
              width: 44,
              height: 44,
              boxShadow: '0 4px 10px rgba(0,0,0,0.1)',
            }}
          >
            {icon}
          </Avatar>
        </Box>
        <Typography variant="h4" sx={{ fontWeight: 700, color, mb: 0.5 }}>
          {value}
        </Typography>
        {subtitle && (
          <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 500 }}>
            {subtitle}
          </Typography>
        )}
      </CardContent>
    </Card>
  );
};

export default StatsCard;
