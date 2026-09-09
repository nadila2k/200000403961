import React from 'react';
import {
  Box,
  Drawer,
  AppBar,
  Toolbar,
  List,
  Typography,
  Divider,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  FormControlLabel,
  Switch,
  Chip,
  Snackbar,
  Alert,
  Avatar,
} from '@mui/material';
import DashboardIcon from '@mui/icons-material/Dashboard';
import SchoolIcon from '@mui/icons-material/School';
import AssignmentIcon from '@mui/icons-material/Assignment';
import PeopleIcon from '@mui/icons-material/People';
import MeetingRoomIcon from '@mui/icons-material/MeetingRoom';
import LocalOfferIcon from '@mui/icons-material/LocalOffer';

const drawerWidth = 260;

const menuItems = [
  { id: 'dashboard', label: 'Dashboard Overview', icon: <DashboardIcon /> },
  { id: 'programmes', label: 'Training Programmes', icon: <SchoolIcon /> },
  { id: 'nominations', label: 'Officer Nominations', icon: <AssignmentIcon /> },
  { id: 'officers-depts', label: 'Officers & Departments', icon: <PeopleIcon /> },
  { id: 'trainers-venues', label: 'Trainers & Venues', icon: <MeetingRoomIcon /> },
];

const Layout = ({
  activeTab,
  onNavigate,
  isMockMode,
  onToggleMockMode,
  snackbar,
  onCloseSnackbar,
  children,
}) => {
  return (
    <Box sx={{ display: 'flex', minHeight: '100vh', bgcolor: '#f8fafc' }}>
      {/* Top App Bar */}
      <AppBar
        position="fixed"
        sx={{
          width: { sm: `calc(100% - ${drawerWidth}px)` },
          ml: { sm: `${drawerWidth}px` },
          bgcolor: '#ffffff',
          color: '#0f172a',
          boxShadow: '0 1px 3px 0 rgba(0, 0, 0, 0.05)',
          borderBottom: '1px solid #e2e8f0',
        }}
      >
        <Toolbar sx={{ justifyContent: 'space-between' }}>
          <Typography variant="h6" noWrap component="div" sx={{ fontWeight: 700 }}>
            {menuItems.find((m) => m.id === activeTab)?.label || 'Dashboard'}
          </Typography>

          {/* Connection Status & Mode Switcher */}
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
            <Chip
              label={isMockMode ? 'STANDALONE DEMO MODE' : 'CONNECTED TO SPRING BACKEND'}
              color={isMockMode ? 'info' : 'success'}
              size="small"
              sx={{ fontWeight: 700, fontSize: '0.7rem' }}
            />
            <FormControlLabel
              control={
                <Switch
                  checked={isMockMode}
                  onChange={(e) => onToggleMockMode(e.target.checked)}
                  color="primary"
                  size="small"
                />
              }
              label={
                <Typography variant="caption" sx={{ fontWeight: 600, color: 'text.secondary' }}>
                  Demo Mode
                </Typography>
              }
            />
          </Box>
        </Toolbar>
      </AppBar>

      {/* Side Navigation Drawer */}
      <Drawer
        variant="permanent"
        sx={{
          width: drawerWidth,
          flexShrink: 0,
          '& .MuiDrawer-paper': {
            width: drawerWidth,
            boxSizing: 'border-box',
            bgcolor: '#0f172a',
            color: '#f8fafc',
            borderRight: 'none',
          },
        }}
      >
        {/* Brand Header */}
        <Box sx={{ p: 2.5, display: 'flex', alignItems: 'center', gap: 1.5 }}>
          <Avatar sx={{ bgcolor: '#2563eb', width: 36, height: 36 }}>
            <LocalOfferIcon fontSize="small" />
          </Avatar>
          <Box>
            <Typography variant="subtitle1" sx={{ fontWeight: 700, color: '#ffffff', leading: 1.2 }}>
              TMS Manager
            </Typography>
            <Typography variant="caption" sx={{ color: '#94a3b8', fontSize: '0.7rem' }}>
              Training Management System
            </Typography>
          </Box>
        </Box>

        <Divider sx={{ borderColor: '#334155' }} />

        {/* Navigation Menu Links */}
        <List sx={{ px: 1.5, py: 2 }}>
          {menuItems.map((item) => {
            const isSelected = activeTab === item.id;
            return (
              <ListItem key={item.id} disablePadding sx={{ mb: 0.5 }}>
                <ListItemButton
                  selected={isSelected}
                  onClick={() => onNavigate(item.id)}
                  sx={{
                    borderRadius: 2,
                    color: isSelected ? '#ffffff' : '#94a3b8',
                    bgcolor: isSelected ? '#2563eb !important' : 'transparent',
                    '&:hover': {
                      bgcolor: isSelected ? '#2563eb' : '#1e293b',
                      color: '#ffffff',
                    },
                  }}
                >
                  <ListItemIcon sx={{ color: 'inherit', minWidth: 40 }}>
                    {item.icon}
                  </ListItemIcon>
                  <ListItemText
                    primary={item.label}
                    primaryTypographyProps={{ fontWeight: isSelected ? 700 : 500, fontSize: '0.875rem' }}
                  />
                </ListItemButton>
              </ListItem>
            );
          })}
        </List>

        <Box sx={{ flexGrow: 1 }} />

        {/* Footer info inside Drawer */}
        <Box sx={{ p: 2, bgcolor: '#1e293b', m: 1.5, borderRadius: 2 }}>
          <Typography variant="caption" display="block" sx={{ color: '#94a3b8', fontWeight: 600 }}>
            Spring Boot API Status:
          </Typography>
          <Typography variant="caption" display="block" sx={{ color: '#e2e8f0', fontSize: '0.7rem' }}>
            Base URL: http://localhost:8080/api
          </Typography>
        </Box>
      </Drawer>

      {/* Main Content View Container */}
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          p: 3,
          width: { sm: `calc(100% - ${drawerWidth}px)` },
          mt: 8,
        }}
      >
        {children}
      </Box>
    </Box>
  );
};

export default Layout;
