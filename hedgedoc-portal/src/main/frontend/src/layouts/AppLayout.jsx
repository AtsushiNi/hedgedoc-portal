import { Outlet } from 'react-router-dom';
import { ConfigProvider, Layout, Menu, theme } from "antd";
const { Header, Content } = Layout;
const { darkAlgorithm } = theme;

const headerStyle = {
  height: 64,
  paddingInline: 48,
  lineHeight: "64px",
  color: "white",
  fontSize: "large",
  display: "flex",
  justifyContent: "space-between",
};
const contentStyle = {
  textAlign: "center",
  background: "#333",
  boxShadow: "inset 0 0 100px rgba(0,0,0,0.5)",
  padding: "auto",
  paddingTop: 48,
  height: "calc(100vh - 64px)",
  paddingLeft: "20%",
  paddingRight: "20%",
};

const menuItems = [
  {
    key: 1,
    label: <a href="/rules">振り分けルール</a>,
  },
  {
    key: 2,
    label: <a href="/login">サインイン</a>,
  },
];

const AppLayout = () => (
  <ConfigProvider theme={{ algorithm: darkAlgorithm }}>
    <Layout>
      <Header style={headerStyle}>
        <div>
          <a href="/" style={{ textDecoration: "none", color: "silver" }}>
            HedgeDoc portal
          </a>
        </div>
        <Menu
          theme="dark"
          mode="horizontal"
          items={menuItems}
          style={{ flex: 1, justifyContent: "end" }}
        />
      </Header>
      <Content style={contentStyle}>
        <Outlet />
      </Content>
    </Layout>
  </ConfigProvider>
);

export default AppLayout;
