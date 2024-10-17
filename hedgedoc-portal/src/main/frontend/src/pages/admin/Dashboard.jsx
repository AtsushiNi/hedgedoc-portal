import { Row, Col, Card } from "antd";
import { Area, Pie } from "@ant-design/charts";
import { useState, useEffect } from "react";
import axios from "axios";

const Dashboard = () => {
  const [users, setUsers] = useState([]);
  const [statusCodes, setStatusCodes] = useState([]);
  const [requestUrls, setRequestUrls] = useState([]);
  const [requestMethods, setRequestMethods] = useState([]);

  useEffect(() => {
    fetchUsers();
    fetchStatusCodes();
    fetchRequestUrls();
    fetchRequestMethods();
  }, []);

  const fetchUsers = async () => {
    try {
      const response = await axios.get("/api/v1/admin/dashboard/users");
      setUsers(response.data);
    } catch (error) {
      console.log(error);
    }
  };

  const fetchStatusCodes = async () => {
    try {
      const response = await axios.get("/api/v1/admin/dashboard/status-codes");
      setStatusCodes(response.data);
    } catch (error) {
      console.log(error);
    }
  };

  const fetchRequestUrls = async () => {
    try {
      const response = await axios.get("/api/v1/admin/dashboard/request-urls");
      setRequestUrls(response.data);
    } catch (error) {
      console.log(error);
    }
  };

  const fetchRequestMethods = async () => {
    try {
      const response = await axios.get(
        "/api/v1/admin/dashboard/request-methods"
      );
      setRequestMethods(response.data);
    } catch (error) {
      console.log(error);
    }
  };

  const usersData = users.map((value) => ({ date: value[0], users: value[1] }));
  const statusData = statusCodes.map((value) => ({
    key: value[0].toString(),
    count: value[1],
  }));
  const urlData = requestUrls
    .map((value) => ({ key: value[0], count: value[1] }))
    .sort((a, b) => a.count > b.count);
  const methodData = requestMethods.map((value) => ({
    key: value[0],
    count: value[1],
  }));

  const usersConfig = {
    // autoFit: true, // コンポーネントのサイズに自動フィット
    data: usersData,
    smooth: true, // 滑らかな曲線にするオプション
    xField: "date",
    yField: "users",
    style: {
      fill: "linear-gradient(-90deg, white 0%, darkblue 100%)",
      fillOpacity: 0.6,
    },
    xAxis: {
      type: "time", // 日付を適切に表示するための設定
    },
  };

  const pieConfig = {
    angleField: "count",
    colorField: "key",
    innerRadius: 0.6,
    label: {
      text: "key",
      style: {
        fontWeight: "bold",
      },
      position: "spider",
    },
    legend: {
      color: {
        title: false,
        position: "right",
        rowPadding: 5,
      },
    },
  };
  const statusConfig = { ...pieConfig, data: statusData };
  const urlConfig = {
    ...pieConfig,
    data: urlData,
    label: { style: { fontSize: 0 } },
  };
  const methodConfig = { ...pieConfig, data: methodData };

  return (
    <>
      <Row>
        <Col span={12} style={{ minWidth: "100%" }}>
          <Card title="ユーザー数" style={{ margin: 30 }}>
            <Area {...usersConfig} />
          </Card>
        </Col>
      </Row>
      <Row>
        <Col span={6} style={{ minWidth: "calc(100% / 3)" }}>
          <Card title="ステータスコード" style={{ margin: 30 }}>
            <Pie {...statusConfig} />
          </Card>
        </Col>
        <Col span={6} style={{ minWidth: "calc(100% / 3)" }}>
          <Card title="リクエストURL" style={{ margin: 30 }}>
            <Pie {...urlConfig} />
          </Card>
        </Col>
        <Col span={6} style={{ minWidth: "calc(100% / 3)" }}>
          <Card title="リクエストMethod" style={{ margin: 30 }}>
            <Pie {...methodConfig} />
          </Card>
        </Col>
      </Row>
    </>
  );
};

export default Dashboard;
