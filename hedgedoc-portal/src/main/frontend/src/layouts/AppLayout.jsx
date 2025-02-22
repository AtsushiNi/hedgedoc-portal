import { useState } from "react";
import { Outlet, useNavigate } from 'react-router-dom';
import { Input, ConfigProvider, Layout, Menu, theme } from "antd";
import { SearchOutlined } from '@ant-design/icons';

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

const AppLayout = () => {
  const [searchWord, setSearchWord] = useState("");
  const navigate = useNavigate();

  const handleSearch = () => {
    if (searchWord.trim() !== "") {
      navigate(`/search?query=${encodeURIComponent(searchWord)}`);
    }
  }

  return (

    <ConfigProvider theme={{ algorithm: darkAlgorithm }}>
      <Layout>
        <Header style={headerStyle}>
          <div>
            <a href="/" style={{ textDecoration: "none", color: "silver" }}>
              HedgeDoc portal
            </a>
          </div>
          <Input
            value={searchWord}
            onChange={e => setSearchWord(e.target.value)}
            onPressEnter={handleSearch}
            placeholder="Search..."
            prefix={<SearchOutlined />}
            style={{ width: 300, height: 40, flex: "0 1 400px", margin: "auto", marginLeft: 200, background: "rgba(255,255,255,0.2)" }}
          />
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
}

export default AppLayout;
