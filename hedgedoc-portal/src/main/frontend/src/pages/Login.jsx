import { useNavigate } from 'react-router-dom';
import { Card, Input, Button, Form, notification } from 'antd';
import { LockOutlined, UserOutlined } from '@ant-design/icons';
import axios from 'axios';
import Typography from 'antd/es/typography/Typography';
import { useCookies } from "react-cookie";

const Login = () => {
  const navigate = useNavigate();
  const [api, contextHolder] = notification.useNotification();
  const [cookies, setCookie] = useCookies();

  const handleSubmit = async (values) => {
    try {
      const response = await axios.post('/api/v1/login', values);
      const token = response.headers["x-auth-token"];
      setCookie("token", token);

      navigate("/");
    } catch (error) {
      console.error('Sign in failed.', error);
      api['error']({
        message: "Sign in failed",
        description: "User ID or password is wrong. Please retry with correct ID and password."
      })
    }
  };

  return (
    <div className="container" style={{ textAlign: "center" }}>
      {contextHolder}
      <Card style={{ maxWidth: 430, margin: "auto", padding: 20 }}>
        <h1 style={{ color: "white" }}>Sign in</h1>
        <Typography style={{ color: "silver", marginBottom: 30 }}>
          Welcome to HedgeDoc Portal!<br/>
          Please input your ID and password.
        </Typography>

        <Form layout="vertical" onFinish={handleSubmit}>
          <Form.Item name="userId" label="user ID">
            <Input prefix={<UserOutlined/>} />
          </Form.Item>
          <Form.Item name="password" label="password">
            <Input.Password prefix={<LockOutlined/>} />
          </Form.Item>
          <Form.Item style={{ marginTop: 60 }}>
            <Button block type="primary" htmlType='submit'>
              Sign in
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
}

export default Login;