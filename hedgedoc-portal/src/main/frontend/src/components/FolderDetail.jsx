import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Flex, Card, Divider, Breadcrumb, Dropdown, Space } from 'antd';
import dayjs from 'dayjs';
import axios from 'axios';

export default function FolderDetail() {
  const { folderId } = useParams();
  const navigate = useNavigate();
  const [parentFolders, setParentFolders] = useState([]);
  const [subFolders, setSubFolders] = useState([]);
  const [notes, setNotes] = useState([]);

  const HEDGEDOC_URL = "http://localhost:3000";

  useEffect(() => {
    const fetchFolder = async() => {
      try {
        const response = await axios.get("/api/v1/folders/" + folderId);
        const folder = response.data;
        setParentFolders(folder.parentFolders);
        setSubFolders(folder.subFolders);
        setNotes(folder.notes);
      } catch (error) {
        if (error.status === 403) {
          console.log("wrong cookie for HedgeDoc.");
          navigate("/cookie-setting");
        }
        console.error("Error fetching folder: " + error);
      }
    };
    fetchFolder();
  }, [folderId])

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

  const noteMenuItems = id => [
    {
      key: '1',
      label: (
        <div
          onClick={() => {
            // setIsMoveModalOpen(true)
            // setMoveItemId(id)
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

  return (
    <div className="container">
      <Breadcrumb style={{ cursor: "pointer" }}>
        {parentFolders.map(folder => (
          <Breadcrumb.Item onClick={() => navigate("/folders/" + folder.id)}>
            {folder.title}
          </Breadcrumb.Item>
        ))}
      </Breadcrumb>
      <div style={{ color: "white", textAlign: "left", fontSize: "large" }}>ノート</div>
      <Flex wrap="wrap">
        {notes.map(note => (
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
      <Divider style={{ background: "silver" }} />
      <div style={{ color: "white", textAlign: "left", fontSize: "large" }}>フォルダ</div>
      <Flex wrap="wrap">
        {subFolders.map(subFolder => (
            <Card
              key={subFolder.id}
              title={subFolder.title}
              onClick={() => navigate("/folders/" + subFolder.id)}
              style={folderCardStyle}
              headStyle={cardHeaderStyle}
            ></Card>
        ))}
      </Flex>
    </div>
  )
}