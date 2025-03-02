import { Flex, Form, Input, Button, message, Typography, Card } from "antd";
import { useState } from "react";
import axios from "axios";
import { useCookies } from "react-cookie";

const { Title } = Typography;

const AiChat = () => {
  const [loading, setLoading] = useState(false);
  const [keywords, setKeywords] = useState([]);
  const [searchResults, setSearchResults] = useState([]);
  const [answer, setAnswer] = useState("");
  const [ cookies ] = useCookies();

  const handleSubmit = async (values) => {
    setLoading(true);
    try {
      // Step 1: キーワード抽出
      const keywordRes = await axios.post("/api/v1/chat/extract-keywords", {
        question: values.question
      }, { headers: { 'x-auth-token': `Bearer ${cookies.accessToken}`}});
      setKeywords(keywordRes.data.keywords);

      // Step 2: キーワード検索
      const searchRes = await axios.post("/api/v1/chat/search", {
        keywords: keywordRes.data.keywords
      }, { headers: { 'x-auth-token': `Bearer ${cookies.accessToken}`}});
      setSearchResults(searchRes.data.results);

      // Step 3: 解答生成
      const answerRes = await axios.post("/api/v1/chat/generate-answer", {
        question: values.question,
        searchResults: searchRes.data.results
      }, { headers: { 'x-auth-token': `Bearer ${cookies.accessToken}`}});
      setAnswer(answerRes.data.answer);

    } catch (error) {
      message.error("失敗しました");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="container">
      <Flex
        justify="space-between"
        style={{ paddingTop: "50px", paddingBottom: "20px" }}
      >
        <div style={{ color: "white", textAlign: "left", fontSize: "large" }}>
          AI チャット
        </div>
      </Flex>

      <Flex wrap="wrap">
        <Form layout="inline" onFinish={handleSubmit}>
          <Form.Item name="question" rules={[{ required: true, message: "質問を入力してください"}]}>
            <Input placeholder="質問を入力..." />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading}>送信</Button>
          </Form.Item>
        </Form>
      </Flex>

      {keywords.length > 0 && (
        <Card title="抽出されたキーワード" style={{ marginTop: 16 }}>
          {keywords.join(", ")}
        </Card>
      )}

      {searchResults.length > 0 && (
        <Card title="検索結果" style={{ marginTop: 16 }}>
          <ul>
            {searchResults.map((result, i) => (
              <li key={i}>{result}</li>
            ))}
          </ul>
        </Card>
      )}

      {answer && (
        <Card title="回答" style={{ marginTop: 16 }}>
          {answer}
        </Card>
      )}
    </div>
  );
};

export default AiChat;
