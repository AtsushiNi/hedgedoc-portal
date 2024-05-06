import { Routes, Route, Link } from 'react-router-dom';
import Home from './components/Home';
import CookieSetting from './components/CookieSetting';

function App() {

  return (
    <div className="App">
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/cookie-setting" element={<CookieSetting />} />
      </Routes>
    </div>
  );
}

export default App;
