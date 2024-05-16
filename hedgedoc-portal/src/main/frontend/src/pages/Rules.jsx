import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Flex, Button, Table } from "antd";
import axios from "axios";

const Rules = () => {
  const [rules, setRules] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    fetchRules();
  }, [])

  const fetchRules = async() => {
    try {
      const response = await axios.get("/api/v1/rules");
      const rules = response.data.map(rule => ({
        id: rule.id,
        title: rule.title,
        regularExpression: rule.regularExpression,
        toFolderId: rule.folder.id,
        toFolderTitle: rule.folder.title,
      }));
      setRules(rules);
    } catch (error) {
      if (error.response.status === 403) {
        console.log("wrong cookie for HedgeDoc.");
        navigate("/cookie-setting");
      }
      console.error("Error fetching rules: " + error);
    }
  }

  const columns = [
    {
      title: "title",
      dataIndex: "title",
      key: "title",
    },
    {
      title: "regular Expression",
      dataIndex: "regularExpression",
      key: "regularExpression",
    },
    {
      title: "folder",
      dataIndex: "folder",
    }
  ]

  return (
    <>
      <Flex
        justify="space-between"
        style={{ paddingTop: "50px", paddingBottom: "20px" }}
      >
        <div style={{ color: "white", textAlign: "left", fontSize: "large" }}>振り分けルール</div>
        <Button
          type="primary"
        >
          +新規ルール
        </Button>
      </Flex>
      <Table
        dataSource={rules}
        columns={columns}
      />
    </>
  )
}

export default Rules;