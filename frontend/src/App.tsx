import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import Dashboard from './pages/Dashboard';
import Entities from './pages/Entities';
import { AppBar, Toolbar, Button } from '@mui/material';

function App() {
  return (
    <Router>
      <AppBar position='static'>
        <Toolbar>
          <Button color='inherit' component={Link} to='/' >Dashboard</Button>
          <Button color='inherit' component={Link} to='/entities'>Entities</Button>
        </Toolbar>
      </AppBar>
      <Routes>
        <Route path='/' element={<Dashboard />} />
        <Route path='/entities' element={<Entities />} />
      </Routes>
    </Router>
  );
}

export default App;
