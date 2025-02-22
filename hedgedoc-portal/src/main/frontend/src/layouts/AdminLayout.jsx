import { useState } from "react";
import { Outlet } from "react-router-dom";
import { Layout, Menu } from "antd";
import { DatabaseOutlined, HomeOutlined, WifiOutlined } from "@ant-design/icons";
const { Sider, Content } = Layout;

const AdminLayout = () => {
  const [collapsed, setCollapsed] = useState(false);

  const menuItems = [
    {
      key: 1,
      label: "Dashboard",
      icon: <HomeOutlined />,
    },
    {
      key: 2,
      label: "Access",
      icon: <WifiOutlined />,
    },
    {
      key: 3,
      label: "Data",
      icon: <DatabaseOutlined />,
    }
  ];

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider
        collapsible
        collapsed={collapsed}
        onCollapse={(value) => setCollapsed(value)}
      >
        <div style={{ height: 32, margin: 16, background: 'rgba(255, 255, 255, 0.2)', borderRadius: 6 }}></div>
        <Menu
          theme="dark"
          defaultSelectedKeys={["1"]}
          mode="inline"
          items={menuItems}
        />
      </Sider>
      <Content>
        <Outlet />
      </Content>
    </Layout>
  );
};

export default AdminLayout;
