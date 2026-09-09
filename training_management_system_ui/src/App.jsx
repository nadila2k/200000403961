import React, { useState, useEffect } from 'react';
import { ThemeProvider, CssBaseline, Box, CircularProgress } from '@mui/material';
import theme from './theme';
import Layout from './components/Layout';
import DashboardView from './views/DashboardView';
import ProgrammesView from './views/ProgrammesView';
import NominationsView from './views/NominationsView';
import OfficersDeptView from './views/OfficersDeptView';
import TrainersVenuesView from './views/TrainersVenuesView';
import { apiService, getMockMode, setMockMode } from './api/apiService';
import toast, { Toaster } from 'react-hot-toast';

function App() {
  const [activeTab, setActiveTab] = useState('dashboard');
  const [isMock, setIsMock] = useState(getMockMode());
  const [loading, setLoading] = useState(true);

  // Entities Data State
  const [departments, setDepartments] = useState([]);
  const [officers, setOfficers] = useState([]);
  const [venues, setVenues] = useState([]);
  const [trainers, setTrainers] = useState([]);
  const [programmes, setProgrammes] = useState([]);
  const [nominations, setNominations] = useState([]);

  // Submit Nomination Dialog global state
  const [openNominateDialog, setOpenNominateDialog] = useState(false);

  const showNotification = (message, type = 'success') => {
    if (type === 'success') {
      toast.success(message, {
        style: { borderRadius: '8px', background: '#0f172a', color: '#fff', fontWeight: 600 },
      });
    } else if (type === 'error') {
      toast.error(message, {
        style: { borderRadius: '8px', background: '#991b1b', color: '#fff', fontWeight: 600 },
      });
    } else if (type === 'warning') {
      toast(message, {
        icon: '⚠️',
        style: { borderRadius: '8px', background: '#9a3412', color: '#fff', fontWeight: 600 },
      });
    } else {
      toast(message, {
        style: { borderRadius: '8px', background: '#1e293b', color: '#fff', fontWeight: 600 },
      });
    }
  };

  const fetchAllData = async () => {
    setLoading(true);
    try {
      const [deptRes, offRes, venRes, trnRes, prgRes, nomRes] = await Promise.all([
        apiService.getDepartments(),
        apiService.getOfficers(),
        apiService.getVenues(),
        apiService.getTrainers(),
        apiService.getProgrammes(),
        apiService.getNominations(),
      ]);

      setDepartments(deptRes.data || []);
      setOfficers(offRes.data || []);
      setVenues(venRes.data || []);
      setTrainers(trnRes.data || []);
      setProgrammes(prgRes.data || []);
      setNominations(nomRes.data || []);
    } catch (err) {
      showNotification(`API Error: ${err.message}. Switched to Standalone Demo Mode.`, 'warning');
      setMockMode(true);
      setIsMock(true);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAllData();
  }, [isMock]);

  const handleToggleMockMode = (enabled) => {
    setMockMode(enabled);
    setIsMock(enabled);
    showNotification(
      enabled
        ? 'Switched to Standalone Interactive Demo Mode.'
        : 'Switched to Live Spring Boot API (http://localhost:8080/api).',
      'info'
    );
  };

  const handleOpenNominateGlobal = () => {
    setActiveTab('nominations');
    setOpenNominateDialog(true);
  };

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Toaster position="top-right" reverseOrder={false} />
      <Layout
        activeTab={activeTab}
        onNavigate={(tab) => setActiveTab(tab)}
        isMockMode={isMock}
        onToggleMockMode={handleToggleMockMode}
      >
        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '60vh' }}>
            <CircularProgress size={48} />
          </Box>
        ) : (
          <>
            {activeTab === 'dashboard' && (
              <DashboardView
                programmes={programmes}
                nominations={nominations}
                officers={officers}
                departments={departments}
                onNavigate={(tab) => setActiveTab(tab)}
                onOpenNominate={handleOpenNominateGlobal}
              />
            )}

            {activeTab === 'programmes' && (
              <ProgrammesView
                programmes={programmes}
                venues={venues}
                trainers={trainers}
                departments={departments}
                onRefresh={fetchAllData}
                showNotification={showNotification}
              />
            )}

            {activeTab === 'nominations' && (
              <NominationsView
                nominations={nominations}
                programmes={programmes}
                officers={officers}
                departments={departments}
                onRefresh={fetchAllData}
                showNotification={showNotification}
                openNominateDialog={openNominateDialog}
                setOpenNominateDialog={setOpenNominateDialog}
              />
            )}

            {activeTab === 'officers-depts' && (
              <OfficersDeptView
                officers={officers}
                departments={departments}
                onRefresh={fetchAllData}
                showNotification={showNotification}
              />
            )}

            {activeTab === 'trainers-venues' && (
              <TrainersVenuesView
                trainers={trainers}
                venues={venues}
                onRefresh={fetchAllData}
                showNotification={showNotification}
              />
            )}
          </>
        )}
      </Layout>
    </ThemeProvider>
  );
}

export default App;
