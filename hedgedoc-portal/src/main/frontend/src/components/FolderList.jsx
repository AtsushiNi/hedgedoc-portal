import { useRef, useState, useEffect } from "react";
import { Flex, Card, Button, Modal, Form, Input, Dropdown, Space, Cascader } from 'antd';
import { useNavigate } from 'react-router-dom';
import axios from "axios";

const FolderList = props => {
  const { folder: currentFolder, folders, folderTree, fetchFolder, fetchFolders } = props
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [createFolderName, setCreateFolderName] = useState("");
  const [isMoveModalOpen, setIsMoveModalOpen] = useState(false);
  const [moveToFolderId, setMoveToFolderId] = useState(null);
  const [moveFolder, setMoveFolder] = useState(null);
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
    const data = { title: createFolderName, parentFolderId: currentFolder?.id };
    await axios.post("/api/v1/folders", data);

    setIsCreateModalOpen(false);

    // 表示しているページのフォルダ情報を更新
    await fetchFolder();
    // 移動先フォルダツリーを更新
    await fetchFolders();
  }

  // 移動先フォルダを選択した時のハンドラ
  const handleChangeMoveToFolder = list => {
    if (list.length === 0) {
      setMoveToFolderId(null);
      return;
    }

    const selectedFolderId = list[list.length - 1];
    setMoveToFolderId(selectedFolderId);
  }

  // フォルダへの移動を実行したときのハンドラ
  const handleMoveItem = async() => {
    const data = { toFolderId: moveToFolderId };

    await axios.post("/api/v1/folders/" + moveFolder.id + "/move", data);

    // 表示しているページのフォルダ情報を更新
    await fetchFolder();
    // 移動先フォルダツリーを更新
    await fetchFolders();

    setIsMoveModalOpen(false);
  }

  // 引数のtargetFolderが引数のfolderもしくはその子孫かどうかを判定
  const isCurrentFolderOrSubFolder = (targetFolder, folder) => {
    // moveFolderを選択するまでにレンダリングされる場合
    if (folder == null) return true;

    if (targetFolder.id === folder.id) {
      return true;
    }
    // folderのsubFoldersが存在する場合、それらの子孫も再帰的にチェック
    const isDescendants = folder.subFolders?.some(subFolder => isCurrentFolderOrSubFolder(targetFolder, subFolder));
    if (isDescendants) {
      return true;
    }

    return false;
  }

  // 再帰的にフォルダ移動先選択のオプションを生成する
  const mapFolder = folders => (
    folders?.map(folder => ({
      value: folder.id,
      label: folder.title,
      disabled: isCurrentFolderOrSubFolder(folder, moveFolder),
      children: mapFolder(folder.subFolders)
    }))
  );
  const folderOptions = mapFolder(folderTree);

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

  const folderMenuItems = folder => [
    {
      key: "1",
      label: (
        <div>
          change title
        </div>
      )
    },
    {
      key: "2",
      label: (
        <div onClick={() => {
          setIsMoveModalOpen(true);
          setMoveFolder(folder);
        }}>
          move
        </div>
      )
    }
  ]

  const cardHead = folder => (
    <div style={{ display: "flex", justifyContent: "space-between" }}>
      <div
        onClick={() => navigate("/folders/" + folder.id)}
        style={{ lineHeight: "40px", width: "200px" }}
      >{folder.title}</div>
      <Dropdown
        menu={{ items: folderMenuItems(folder) }}
        onClick={e => {
          console.log("click")
          e.preventDefault()
        }}
        style={{ lineHeight: "40px", marginRight: "-20px" }}
      >
        <Space>...</Space>
      </Dropdown>
    </div>
  )

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
            title={cardHead(folder)}
            style={folderCardStyle}
            headStyle={cardHeaderStyle}
          >
            <p
              style={{ margin: "-25px -24px", height: "80px", paddingTop: 20 }}
              onClick={() => navigate("/folders/" + folder.id)}
            ></p>
          </Card>
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
      <Modal
        title="移動先フォルダの選択"
        open={isMoveModalOpen}
        onCancel={() => setIsMoveModalOpen(false)}
        onOk={handleMoveItem}
      >
        <Cascader
          options={folderOptions}
          expandTrigger="hover"
          changeOnSelect
          onChange={handleChangeMoveToFolder}
        />
      </Modal>
    </>
  )
}

export default FolderList;
 