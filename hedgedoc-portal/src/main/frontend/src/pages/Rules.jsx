import { useRef, useEffect, useState } from "react";
import { Button, Flex, Modal, Table, Form, Input, Cascader } from "antd";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const Rules = () => {
  const [rules, setRules] = useState([]);
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [ruleTitle, setRuleTitle] = useState("");
  const [regularExpression, setRegularExpression] = useState("");
  const [folderTree, setFolderTree] = useState([]);
  const [toFolderId, setToFolderId] = useState(null);
  const navigate = useNavigate();
  const createInputRef = useRef(null);

  useEffect(() => {
    fetchRules();
    fetchFolders();
  }, [])

  useEffect(() => {
    // モーダルが開いた時inputにフォーカスする
    setTimeout(() => {createInputRef.current?.focus()}, 2)
  }, [isCreateModalOpen])

  // 振り分けルール作成実行時のハンドラ
  const handleCreateRule = async() => {
    const data = { title: ruleTitle, regularExpression: regularExpression, folderId: toFolderId };
    await axios.post("/api/v1/rules", data);

    fetchRules();
    setIsCreateModalOpen(false);
  }

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

  const fetchFolders = async () => {
    try {
      const { data: folders } = await axios.get("/api/v1/folders");
      setFolderTree(folders);
    } catch (error) {
      if (error.response.status === 403) {
        console.log("wrong cookie for HedgeDoc.");
        navigate("/cookie-setting");
      }
      console.error("Error fetching folders: " + error);
    }
  }

  // 振り分け先フォルダを選択した時のハンドラ
  const handleChangeToFolder = list => {
    if (list.length === 0) {
      setToFolderId(null);
      return;
    }

    const selectedFolderId = list[list.length - 1];
    setToFolderId(selectedFolderId);
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
      dataIndex: "toFolderTitle",
    }
  ]

  // 再帰的にフォルダ移動先選択のオプションを生成する
  const mapFolder = folders => (
    folders?.map(folder => ({
      value: folder.id,
      label: folder.title,
      children: mapFolder(folder.subFolders)
    }))
  );
  const folderOptions = mapFolder(folderTree);

  return (
    <>
      <Flex
        justify="space-between"
        style={{ paddingTop: "50px", paddingBottom: "20px" }}
      >
        <div style={{ color: "white", textAlign: "left", fontSize: "large" }}>振り分けルール</div>
        <Button
          type="primary"
          onClick={() => setIsCreateModalOpen(true)}
        >
          +新規ルール
        </Button>
      </Flex>
      <Table
        dataSource={rules}
        columns={columns}
      />
      <Modal
        title="新規ルール作成"
        open={isCreateModalOpen}
        onCancel={() => setIsCreateModalOpen(false)}
        onOk={handleCreateRule}
        afterClose={() => setRuleTitle("")}
      >
        <Form style={{ marginTop: 50 }}>
          <Form.Item label="タイトル">
            <Input ref={createInputRef} value={ruleTitle} onChange={e => setRuleTitle(e.target.value)}/>
          </Form.Item>
          <Form.Item label="正規表現">
            <Input value={regularExpression} onChange={e => setRegularExpression(e.target.value)}/>
          </Form.Item>
          <Form.Item label="振り分け先フォルダ">
            <Cascader
              options={folderOptions}
              expandTrigger="hover"
              changeOnSelect
              onChange={handleChangeToFolder}
            />
          </Form.Item>
        </Form>
      </Modal>
    </>
  )
}

export default Rules;