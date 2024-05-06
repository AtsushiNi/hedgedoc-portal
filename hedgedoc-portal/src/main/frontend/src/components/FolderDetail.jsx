import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Flex, Card, Divider } from 'antd';
import dayjs from 'dayjs';
import axios from 'axios';

export default function FolderDetail() {
  const { folderId } = useParams();
  const navigate = useNavigate();
  const [subFolders, setSubFolders] = useState([]);
  const [notes, setNotes] = useState([]);

  useEffect(() => {
    const fetchFolder = async() => {
      try {
        const response = await axios.get("/api/v1/folders/" + folderId);
        setSubFolders(response.data.subFolders);
        setNotes(response.data.notes);
      } catch (error) {
        if (error.status === 403) {
          console.log("wrong cookie for HedgeDoc.");
          navigate("/cookie-setting");
        }
        console.error("Error fetching folder: " + error);
      }
    };
    fetchFolder();
  }, [])

  const cardStyle = {
    width: 280,
    height: 120,
    margin: 10,
    cursor: "pointer",
    title: {
      height: 40
    }
  };
  const cardHeaderStyle = {
    minHeight: 40,
    textAlign: "left",
  }
  const folderCardStyle = {
    ...cardStyle,
    background: "#E8CD89",
    border: "none",
  };

  return (
    <div className="container">
      <div style={{ color: "white", textAlign: "left", fontSize: "large" }}>ノート</div>
      <Flex wrap="wrap">
        {notes.map(note => (
          <Card
            key={note.id}
            title={note.title}
            style={cardStyle}
            headStyle={cardHeaderStyle}
          >
            <p>update at: {dayjs(note.updatetime).format("YYYY/MM/DD HH:mm")}</p>
          </Card>
        ))}
      </Flex>
      <Divider style={{ background: "silver" }} />
      <div style={{ color: "white", textAlign: "left", fontSize: "large" }}>フォルダ</div>
      <Flex wrap="wrap">
        {subFolders.map(subFolder => (
            <Card
              key={subFolder.id}
              title={subFolder.title}
              style={folderCardStyle}
              headStyle={cardHeaderStyle}
            ></Card>
        ))}
      </Flex>
    </div>
  )
}