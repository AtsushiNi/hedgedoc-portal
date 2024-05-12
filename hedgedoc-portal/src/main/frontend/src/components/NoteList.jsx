import { useState } from "react";
import { Flex, Card, Dropdown, Space, Modal, Cascader } from 'antd';
import dayjs from 'dayjs';
import axios from "axios";

const NoteList = props => {
  const { notes, folder, folders, reload } = props
  const [isMoveModalOpen, setIsMoveModalOpen] = useState(false);
  const [moveItemId, setMoveItemId] = useState(null);
  const [moveToFolderId, setMovetoFolderId] = useState(null);

  const HEDGEDOC_URL = "http://localhost:3000";

  // 移動先フォルダを選択した時のハンドラ
  const handleChangeMoveToFolder = list => {
    if (list.length === 0) {
      setMovetoFolderId(null);
      return;
    }

    const selectedFolderId = list[list.length - 1];
    setMovetoFolderId(selectedFolderId);
  }

  // フォルダへの移動を実行した時のハンドラ
  const handleMoveItem = async() => {
    if (moveToFolderId === null) return;

    const data = { noteId: moveItemId, fromFolderId: folder?.id, toFolderId: moveToFolderId };

    await axios.post("/api/v1/notes/move", data);

    await reload();

    setIsMoveModalOpen(false);
  }

  // フォルダからノートを削除したときのハンドラ
  const handleDeleteNoteFromFolder = async(id) => {
    await axios.delete("/api/v1/folders/" + folder.id + "/notes/" + id);

    await reload();
  }

  // 再帰的にフォルダ移動先選択のオプションを生成する
  const mapFolder = folders => (
    folders.map(folder => ({
      value: folder.id,
      label: folder.title,
      children: mapFolder(folder.subFolders)
    }))
  );
  const folderOptions = mapFolder(folders);


  const cardStyle = {
    width: 280,
    height: 120,
    margin: 10,
    cursor: "pointer",
    background: "white",
    color: "#777",
    title: {
      height: 40,
    },
  };
  const cardHeaderStyle = {
    minHeight: 40,
    textAlign: "left",
    color: "black",
  }

  const noteMenuItems = id => [
    {
      key: '1',
      label: (
        <div
          onClick={() => {
            setIsMoveModalOpen(true)
            setMoveItemId(id)
          }}
        >
          move
        </div>
      ),
    },
    {
      key: '2',
      label: (
        <div
          onClick={() => {
            handleDeleteNoteFromFolder(id)
          }}
        >
          delete from folder
        </div>
      ),
    },
    {
      key: '3',
      danger: true,
      label: 'delete from folder'
    },
  ]

  const cardHead = note => (
    <div style={{display: "flex", justifyContent: "space-between"}}>
      <div
        onClick={() => window.open(HEDGEDOC_URL + "/" + note.hedgedocId, "_blank")}
        style={{ lineHeight: "40px", width: "200px" }}
      >{note.title}</div>
      <Dropdown
        menu={{ items: noteMenuItems(note.id) }}
        onClick={e => e.preventDefault()}
        style={{ lineHeight: "40px", marginRight: "-20px" }}
      >
        <Space>...</Space>
      </Dropdown>
    </div>
  )

  return (
    <>
      <Flex wrap="wrap">
        {notes?.map(note => (
          <Card
            key={note.id}
            title={cardHead(note)}
            style={cardStyle}
            headStyle={cardHeaderStyle}
          >
            <p
              style={{ margin: "-25px -24px", height: "80px", paddingTop: 20 }}
              onClick={() => window.open(HEDGEDOC_URL + "/" + note.hedgedocId, "_blank")}
            >update at: {dayjs(note.updatetime).format("YYYY/MM/DD HH:mm")}</p>
          </Card>
        ))}
      </Flex>
      <Modal title="移動先フォルダの選択" open={isMoveModalOpen} onCancel={() => setIsMoveModalOpen(false)} onOk={handleMoveItem}>
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

export default NoteList;
