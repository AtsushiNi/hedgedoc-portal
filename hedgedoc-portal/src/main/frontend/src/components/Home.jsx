import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button, Card, Divider, Dropdown, Space, Flex, Modal, Cascader, Pagination } from "antd";
import dayjs from "dayjs";
import axios from "axios";
import "../css/Home.css";

export default function Home() {
  const navigate = useNavigate();
  const [notes, setNotes] = useState([]);
  const [showingNotes, setShowingNotes] = useState([]);
  const [folders, setFolders] = useState([]);
  const [isMoveModalOpen, setIsMoveModalOpen] = useState(false);
  const [moveItemId, setMoveItemId] = useState(null);
  const [moveToFolderId, setMovetoFolderId] = useState(null);

  const HEDGEDOC_URL = "http://localhost:3000";
  const notesPageSize = 15;

  useEffect(() => {
    fetchHistory();
  }, []);

  const fetchHistory = async () => {
    try {
      const response = await axios.get("/api/v1/history");
      setNotes(response.data);
      setShowingNotes(response.data.slice(0, notesPageSize));
      const { data: folders } = await axios.get("/api/v1/folders");
      setFolders(folders);
    } catch (error) {
      if (error.response.status === 403) {
        console.log("wrong cookie for HedgeDoc.");
        navigate("/cookie-setting");
      }
      console.error("Error fetching history: " + error);
    }
  };

  const handleChangeMoveToFolder = list => {
    if (list.length === 0) {
      setMovetoFolderId(null);
      return;
    }

    const selectedFolderId = list[list.length - 1];
    setMovetoFolderId(selectedFolderId);
  }

  const handleMoveItem = async() => {
    if (moveToFolderId === null) return;

    const data = { noteId: moveItemId, fromFolderId: null, toFolderId: moveToFolderId };

    await axios.post("/api/v1/notes/move", data);

    await fetchHistory();

    setIsMoveModalOpen(false);
  }

  const cardStyle = {
    width: 280,
    height: 120,
    margin: 10,
    cursor: "pointer",
    background: "white",
    color: "#777",
    title: {
      height: 40,
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
      danger: true,
      label: 'delete'
    }
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

  const mapFolder = folders => (
    folders.map(folder => ({
      value: folder.id,
      label: folder.title,
      children: mapFolder(folder.subFolders)
    }))
  );
  const folderOptions = mapFolder(folders);

  const handleChangeNotesPageNumber = pageIndex => {
    const startIndex = notesPageSize * (pageIndex - 1);
    const endIndex = Math.min(notesPageSize * pageIndex, notes.length);

    setShowingNotes(notes.slice(startIndex, endIndex))
  }

  return (
    <div className="container">
      <Flex
        justify="flex-end"
        style={{ paddingTop: "50px", paddingBottom: "20px" }}
      >
        <Button
          type="primary"
          onClick={() => window.open(HEDGEDOC_URL + "/new")}
        >
          +新規ノート
        </Button>
      </Flex>
      <div style={{ color: "white", textAlign: "left", fontSize: "large" }}>履歴</div>
      <Flex wrap="wrap">
        {showingNotes.map((note) => (
          <Card
            className="note-card"
            key={note.id}
            title={cardHead(note)}
            style={cardStyle}
            headStyle={cardHeaderStyle}
          >
            <p
              style={{ margin: "-15px -24px", height: "80px", paddingTop: 20 }}
              onClick={() => window.open(HEDGEDOC_URL + "/" + note.hedgedocId, "_blank")}
            >{dayjs(note.updateTime).format("YYYY-MM-DD HH:mm")}</p>
          </Card>
        ))}
      </Flex>
      <Pagination defaultCurrent={1} total={notes.length} defaultPageSize={notesPageSize} onChange={handleChangeNotesPageNumber} />
      <Divider style={{ background: "silver" }} />
      <div style={{ color: "white", textAlign: "left", fontSize: "large" }}>フォルダ</div>
      <Flex wrap="wrap">
        {folders.map(folder => (
          <Card
            key={folder.id}
            title={folder.title}
            onClick={() => navigate("/folders/" + folder.id)}
            style={folderCardStyle}
            headStyle={cardHeaderStyle}
          ></Card>
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
    </div>
  );
}
