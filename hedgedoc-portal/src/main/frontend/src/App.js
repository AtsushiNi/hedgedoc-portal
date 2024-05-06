import { Routes, Route, Link } from 'react-router-dom';
import { Layout, Menu } from 'antd';
import Home from './components/Home';
import CookieSetting from './components/CookieSetting';
const { Header, Content } = Layout;

function App() {

  const headerStyle = {
    height: 64,
    paddingInline: 48,
    lineHeight: '64px',
    color: 'white',
    fontSize: 'large',
    display: 'flex',
    justifyContent: 'space-between',
  }
  const contentStyle = {
    textAlign: 'center',
    background: '#333',
    boxShadow: 'inset 0 0 100px rgba(0,0,0,0.5)',
    padding: 'auto',
    paddingTop: 48,
    height: 'calc(100vh - 64px)',
    paddingLeft: '20%',
    paddingRight: '20%',
  }

  const menuItems = [
    {
      key: 1,
      label: (
        <a href="/cookie-setting">Cookie設定</a>
      )
    }
  ]

  return (
    <div className="App">
      <Layout>
        <Header style={headerStyle}>
          <div>
            <a href="/" style={{ textDecoration: "none", color: "white" }}>HedgeDoc portal</a>
          </div>
          <Menu
            theme="dark"
            mode="horizontal"
            items={menuItems}
            style={{ fontSize: "large" }}
          />
        </Header>
        <Content style={contentStyle}>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/cookie-setting" element={<CookieSetting />} />
          </Routes>
        </Content>
      </Layout>
    </div>
  );
}

export default App;
