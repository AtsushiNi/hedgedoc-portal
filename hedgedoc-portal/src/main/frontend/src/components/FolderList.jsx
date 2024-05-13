import { useRef, useState, useEffect } from "react";
import { Flex, Card, Button, Modal, Form, Input } from 'antd';
import { useNavigate } from 'react-router-dom';
import axios from "axios";

const FolderList = props => {
  const { folder, folders, fetchFolders } = props
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [createFolderName, setCreateFolderName] = useState("");
  const navigate = useNavigate();
  const inputRef = useRef(null);

  useEffect(() => {
    // モーダルが開いた時inputにフォーカスする
    setTimeout(() => {inputRef.current?.focus()}, 2)
  }, [isCreateModalOpen])

  // 新規フォルダボタンを押下したときのハンドラ
  const handleClickModalOpenButton = () => {
    setIsCreateModalOpen(true);
  }

  // フォルダ作成を実行したときのハンドラ
  const handleCreateFolder = async() => {
    const data = { title: createFolderName, parentId: folder?.id };
    await axios.post("/api/v1/folders", data);

    setIsCreateModalOpen(false);
    fetchFolders();
  }

  const cardStyle = {
    width: 280,
    height: 120,
    margin: 10,
    cursor: "pointer",
    background: "white",
    color: "#777",
    title: {
      height: 40
    }
  };
  const cardHeaderStyle = {
    minHeight: 40,
    textAlign: "left",
    color: "black",
  }
  const folderCardStyle = {
    ...cardStyle,
    background: "#E8CD89",
    border: "none",
  };

  return (
    <>
      <Flex justify="space-between" style={{ paddingTop: "30px", paddingBottom: "20px" }}>
        <div style={{ color: "white", textAlign: "left", fontSize: "large" }}>フォルダ</div>
        <Button type="primary" onClick={handleClickModalOpenButton}>+新規フォルダ</Button>
      </Flex>
      <Flex wrap="wrap">
        {folders?.map(folder => (
          <Card
            key={folder.id}
            title={folder.title}
            onClick={() => navigate("/folders/" + folder.id)}
            style={folderCardStyle}
            headStyle={cardHeaderStyle}
          ></Card>
        ))}
      </Flex>
      <Modal
        title="フォルダ作成"
        open={isCreateModalOpen}
        onCancel={() => setIsCreateModalOpen(false)}
        onOk={handleCreateFolder}
        afterClose={() => setCreateFolderName("")}
      >
        <Form style={{ marginTop: 50 }}>
          <Form.Item label="フォルダ名">
            <Input ref={inputRef} value={createFolderName} onChange={e => setCreateFolderName(e.target.value)}/>
          </Form.Item>
        </Form>
      </Modal>
    </>
  )
}

export default FolderList;
 