import { Routes, Route } from 'react-router-dom';
import { Line, Area } from '@ant-design/charts';
import AppLayout from './layouts/AppLayout';
import AdminLayout from './layouts/AdminLayout';
import Home from './pages/Home';
import Rules from './pages/Rules';
import Login from './pages/Login';
import FolderDetail from './pages/FolderDetail';
import Dashboard from './pages/admin/Dashboard';

function App() {


  return (
    <div className="App">
      <Routes>
        <Route element={<AppLayout />}>
          <Route path="/" element={<Home />} />
          <Route path="/rules" element={<Rules />} />
          <Route path="/login" element={<Login />} />
          <Route path="/folders/:folderId" element={<FolderDetail />} />
        </Route>
        <Route element={<AdminLayout />}>
          <Route path="/admin" element={<Dashboard />} />
        </Route>
      </Routes>
    </div>
  );
}

export default App;
