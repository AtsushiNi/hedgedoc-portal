import { Routes, Route, Link } from 'react-router-dom';
import { ConfigProvider, theme, Layout, Menu } from 'antd';
import Home from './pages/Home';
import CookieSetting from './pages/CookieSetting';
import FolderDetail from './pages/FolderDetail';
const { darkAlgorithm } = theme;
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
      <ConfigProvider theme={{ algorithm: darkAlgorithm }}>
        <Layout>
          <Header style={headerStyle}>
            <div>
              <a href="/" style={{ textDecoration: "none", color: "silver" }}>HedgeDoc portal</a>
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
              <Route path="/folders/:folderId" element={<FolderDetail />} />
            </Routes>
          </Content>
        </Layout>
      </ConfigProvider>
    </div>
  );
}

export default App;
