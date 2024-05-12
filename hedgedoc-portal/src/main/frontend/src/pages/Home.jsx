import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button, Divider, Flex, Modal, Cascader, Pagination } from "antd";
import axios from "axios";
import "../css/Home.css";
import FolderList from "../components/FolderList";
import NoteList from "../components/NoteList";

export default function Home() {
  const navigate = useNavigate();
  const [notes, setNotes] = useState([]);
  const [showingNotes, setShowingNotes] = useState([]);
  const [folders, setFolders] = useState([]);

  const HEDGEDOC_URL = "http://localhost:3000";
  const notesPageSize = 15;

  useEffect(() => {
    fetchHistory();
    fetchFolders();
  }, []);

  const fetchHistory = async () => {
    try {
      const response = await axios.get("/api/v1/history");
      setNotes(response.data);
      setShowingNotes(response.data.slice(0, notesPageSize));
    } catch (error) {
      if (error.response.status === 403) {
        console.log("wrong cookie for HedgeDoc.");
        navigate("/cookie-setting");
      }
      console.error("Error fetching history: " + error);
    }
  };

  const fetchFolders = async () => {
    try {
      const { data: folders } = await axios.get("/api/v1/folders");
      setFolders(folders);
    } catch (error) {
      if (error.response.status === 403) {
        console.log("wrong cookie for HedgeDoc.");
        navigate("/cookie-setting");
      }
      console.error("Error fetching folders: " + error);
    }
  }

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

      <div style={{ color: "white", textAlign: "left", fontSize: "large" }}>未分類ノート</div>
      <NoteList notes={showingNotes} folder={null} folders={folders} reload={fetchHistory} root />
      <Pagination defaultCurrent={1} total={notes.length} defaultPageSize={notesPageSize} onChange={handleChangeNotesPageNumber} />

      <Divider style={{ background: "silver" }} />

      <FolderList folders={folders} />
    </div>
  );
}
