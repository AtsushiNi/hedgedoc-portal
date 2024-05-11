import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, Input, Flex, Button } from 'antd';
import axios from 'axios';

export default function CookieSetting() {
  const navigate = useNavigate();
  const [cookie, setCookie] = useState('');
  const handleSubmitClick = async () => {
    try {
      await axios.post('/api/v1/cookie', { cookie: cookie });
      navigate("/");
    } catch (error) {
      console.error('Failed to set cookie:', error);
    }
  };

  return (
    <div className="container" style={{ textAlign: "left" }}>
      <Card id="cookie-card" title="set cookie" style={{ margin: "auto", width: "70%" }}>
        <Input.TextArea rows={4} value={cookie} onChange={e => setCookie(e.target.value)} autoFocus ></Input.TextArea>
        <Flex justify="flex-end" style={{ marginTop: "10px" }}>
          <Button type="primary" onClick={handleSubmitClick}>送信</Button>
        </Flex>
      </Card>
    </div>
  );
}
