import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Divider, Breadcrumb, Flex, Button } from 'antd';
import { HomeFilled } from '@ant-design/icons';
import axios from 'axios';
import FolderList from '../components/FolderList';
import NoteList from '../components/NoteList';

export default function FolderDetail() {
  const { folderId } = useParams();
  const navigate = useNavigate();
  const [folder, setFolder] = useState(null);
  const [folders, setFolders] = useState([]);

  useEffect(() => {
    fetchFolder();
    fetchFolders();
  }, [folderId])

  const handleCreateNote = async() => {
    const data = { parentFolderId: folderId };
    const response = await axios.post("/api/v1/notes", data);
    const { data: newNoteUrl } = response;
    window.open(newNoteUrl);
  }

  const fetchFolder = async() => {
    try {
      const response = await axios.get("/api/v1/folders/" + folderId);
      setFolder(response.data);
    } catch (error) {
      if (error.response.status === 403) {
        console.log("wrong cookie for HedgeDoc.");
        navigate("/login");
      }
      console.error("Error fetching folder: " + error);
    }
  };

  const fetchFolders = async () => {
    try {
      const { data: folders } = await axios.get("/api/v1/folders");
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
    fetchFolder();
    fetchFolders();
  }

  return (
    <div className="container">
      <Breadcrumb style={{ cursor: "pointer", marginBottom: 30 }}>
        <Breadcrumb.Item onClick={() => navigate("/")}>
          <HomeFilled />
        </Breadcrumb.Item>
        {folder?.parentFolders.map(folder => (
          <Breadcrumb.Item key={folder.id} onClick={() => navigate("/folders/" + folder.id)}>
            {folder.title}
          </Breadcrumb.Item>
        ))}
      </Breadcrumb>

      <Flex
        justify="space-between"
        style={{ paddingTop: "50px", paddingBottom: "20px" }}
      >
        <div style={{ color: "white", textAlign: "left", fontSize: "large" }}>ノート</div>
        <Button
          type="primary"
          onClick={handleCreateNote}
        >
          +新規ノート
        </Button>
      </Flex>
      <NoteList notes={folder?.notes} folder={folder} folders={folders} reload={fetchFolder} />

      <Divider style={{ background: "silver" }} />

      <FolderList folder={folder} folders={folder?.subFolders} folderTree={folders} fetchData={fetchDataForFolderList} />
    </div>
  )
}