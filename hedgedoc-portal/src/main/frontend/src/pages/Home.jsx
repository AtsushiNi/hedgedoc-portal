import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button, Divider, Flex, Modal, Cascader, Pagination } from "antd";
import axios from "axios";
import { useCookies } from "react-cookie";
import "../css/Home.css";
import FolderList from "../components/FolderList";
import NoteList from "../components/NoteList";

export default function Home() {
  const navigate = useNavigate();
  const [notes, setNotes] = useState([]);
  const [showingNotes, setShowingNotes] = useState([]);
  const [folders, setFolders] = useState([]);
  const [cookie] = useCookies();

  const HEDGEDOC_URL = "http://localhost:3000";
  const notesPageSize = 15;

  const authAxios = axios.create({
    headers: {
      "x-auth-token": `Bearer ${cookie.token}`
    }
  })

  useEffect(() => {
    fetchNotes();
    fetchFolders();
  }, []);

  const fetchNotes = async () => {
    try {
      const response = await authAxios.get("/api/v1/notes");
      setNotes(response.data);
      setShowingNotes(response.data.slice(0, notesPageSize));
    } catch (error) {
      if (error.response.status === 403) {
        console.log("wrong cookie for HedgeDoc.");
        navigate("/login");
      }
      console.error("Error fetching notes: " + error);
    }
  };

  const fetchFolders = async () => {
    try {
      const { data: folders } = await authAxios.get("/api/v1/folders");
      setFolders(folders);
    } catch (error) {
      if (error.response.status === 403) {
        console.log("wrong cookie for HedgeDoc.");
        navigate("/login");
      }
      console.error("Error fetching folders: " + error);
    }
  }

  // フォルダーリスト画面のデータを更新する処理
  const fetchDataForFolderList = () => {
    fetchNotes();
    fetchFolders();
  }

  const handleChangeNotesPageNumber = pageIndex => {
    const startIndex = notesPageSize * (pageIndex - 1);
    const endIndex = Math.min(notesPageSize * pageIndex, notes.length);

    setShowingNotes(notes.slice(startIndex, endIndex))
  }

  return (
    <div className="container">
      <Flex
        justify="space-between"
        style={{ paddingTop: "50px", paddingBottom: "20px" }}
      >
        <div style={{ color: "white", textAlign: "left", fontSize: "large" }}>未分類ノート</div>
        <Button
          type="primary"
          onClick={() => window.open(HEDGEDOC_URL + "/new")}
        >
          +新規ノート
        </Button>
      </Flex>

      <NoteList notes={showingNotes} folder={null} folders={folders} reload={fetchNotes} root />
      <Pagination defaultCurrent={1} total={notes.length} defaultPageSize={notesPageSize} onChange={handleChangeNotesPageNumber} />

      <Divider style={{ background: "silver" }} />

      <FolderList folder={null} folders={folders} folderTree={folders} fetchData={fetchDataForFolderList} />
    </div>
  );
}
