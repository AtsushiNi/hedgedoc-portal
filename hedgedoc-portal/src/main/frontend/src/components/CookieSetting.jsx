import { useState } from 'react';
import { Card, Input, Flex, Button } from 'antd';
import axios from 'axios';
import '../css/CookieSetting.css';

export default function CookieSetting() {
  const [cookie, setCookie] = useState('');
  const handleSubmitClick = async () => {
    try {
      await axios.post('/api/v1/cookie', { cookie: cookie });
    } catch (error) {
      console.error('Failed to set cookie:', error);
    }
  };

  return (
    <div className="container" style={{ alignItems: "center" }}>
      <Card title="set cookie">
        <Input.TextArea rows={4} value={cookie} onChange={e => setCookie(e.target.value)}></Input.TextArea>
        <Flex justify="flex-end" style={{ marginTop: "10px" }}>
          <Button type="primary" onClick={handleSubmitClick}>送信</Button>
        </Flex>
      </Card>
    </div>
  );
}
