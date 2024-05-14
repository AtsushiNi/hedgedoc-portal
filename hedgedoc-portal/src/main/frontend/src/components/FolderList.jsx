import { useRef, useState, useEffect } from "react";
import { Flex, Card, Button, Modal, Form, Input, Dropdown, Space, Cascader } from 'antd';
import { useNavigate } from 'react-router-dom';
import axios from "axios";

const FolderList = props => {
  const { folder: currentFolder, folders, folderTree, fetchData } = props
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [createFolderName, setCreateFolderName] = useState("");
  const [isMoveModalOpen, setIsMoveModalOpen] = useState(false);
  const [moveToFolderId, setMoveToFolderId] = useState(null);
  const [moveFolder, setMoveFolder] = useState(null);
  const [isChangeFolderNameModalOpen, setIsChangeFolderNameModalOpen] = useState(false);
  const [changeNameFolderId, setChangeNameFolderId] = useState(null);
  const [changeFolderName, setChangeFolderName] = useState("");
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [folderToDelete, setFolderToDelete] = useState(null);
  const navigate = useNavigate();
  const createInputRef = useRef(null);
  const changeNameInputRef = useRef(null);

  useEffect(() => {
    // モーダルが開いた時inputにフォーカスする
    setTimeout(() => {createInputRef.current?.focus()}, 2)
  }, [isCreateModalOpen])
  useEffect(() => {
    // モーダルが開いた時inputにフォーカスする
    setTimeout(() => {changeNameInputRef.current?.focus()}, 2)
  }, [isChangeFolderNameModalOpen])

  // 新規フォルダボタンを押下したときのハンドラ
  const handleClickModalOpenButton = () => {
    setIsCreateModalOpen(true);
  }

  // フォルダ作成を実行したときのハンドラ
  const handleCreateFolder = async() => {
    const data = { title: createFolderName, parentFolderId: currentFolder?.id };
    await axios.post("/api/v1/folders", data);

    setIsCreateModalOpen(false);

    // 表示しているデータを更新
    await fetchData();
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

    // 表示しているデータを更新
    await fetchData();

    setIsMoveModalOpen(false);
  }

  // ファイル名変更を実行したときのハンドラ
  const handleChangeFolderName = async() => {
    const data = { title: changeFolderName };
    await axios.put("/api/v1/folders/" + changeNameFolderId, data);

    setIsChangeFolderNameModalOpen(false);

    // 表示しているデータを更新
    await fetchData();
  }

  // フォルダ削除確認モーダルを開くハンドラ
  const handleOpenDeleteConfirm = folder => {
    setFolderToDelete(folder);
    setIsDeleteModalOpen(true);
  }

  // フォルダ削除を実行したときのハンドラ
  const handleDelete = async() => {
    await axios.delete("/api/v1/folders/" + folderToDelete.id);

    setIsDeleteModalOpen(false);

    // 表示しているデータを更新
    await fetchData();
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
        <div onClick={() => {
          setIsChangeFolderNameModalOpen(true);
          setChangeNameFolderId(folder.id);
        }}>
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
    },
    {
      key: "3",
      danger: true,
      label: (
        <div onClick={() => handleOpenDeleteConfirm(folder)}>
          delete
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
        onClick={e => e.preventDefault()}
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
            <Input ref={createInputRef} value={createFolderName} onChange={e => setCreateFolderName(e.target.value)}/>
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
      <Modal
        title="フォルダのタイトル変更"
        open={isChangeFolderNameModalOpen}
        onCancel={() => setIsChangeFolderNameModalOpen(false)}
        onOk={handleChangeFolderName}
      >
        <Form style={{ marginTop: 50 }}>
          <Form.Item label="フォルダ名">
            <Input ref={changeNameInputRef} value={changeFolderName} onChange={e => setChangeFolderName(e.target.value)}/>
          </Form.Item>
        </Form>
      </Modal>
      <Modal
        title="フォルダの削除"
        open={isDeleteModalOpen}
        onCancel={() => setIsDeleteModalOpen(false)}
        onOk={handleDelete}
      >
        <p>本当にフォルダ<strong>{setFolderToDelete.title}</strong>を削除しますか？</p>
        <p>削除した場合、該当フォルダ以下の全てのノートが未分類になります</p>
      </Modal>
    </>
  )
}

export default FolderList;
 