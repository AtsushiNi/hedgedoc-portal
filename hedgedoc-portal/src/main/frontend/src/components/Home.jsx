import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button, Card, Divider, Flex } from "antd";
import dayjs from "dayjs";
import axios from "axios";

export default function Home() {
  const navigate = useNavigate();
  const [notes, setNotes] = useState([]);
  const [folders, setFolders] = useState([]);

  useEffect(() => {
    const fetchHistory = async () => {
      try {
        const response = await axios.get("/api/v1/history");
        setNotes(response.data.history);
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
    fetchHistory();
  }, []);

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
      <Flex
        justify="flex-end"
        style={{ paddingTop: "50px", paddingBottom: "20px" }}
      >
        <Button
          type="primary"
          onClick={() => window.open("http://localhost:3000/new")}
        >
          +新規ノート
        </Button>
      </Flex>
      <div style={{ color: "white", textAlign: "left", fontSize: "large" }}>履歴</div>
      <Flex wrap="wrap">
        {notes.map((note) => (
          <Card
            key={note.id}
            title={note.text}
            onClick={() =>
              window.open("http://localhost:3000/" + note.id, "_blank")
            }
            style={cardStyle}
            headStyle={cardHeaderStyle}
          >
            <p>{dayjs(note.time).format("YYYY-MM-DD HH:mm")}</p>
          </Card>
        ))}
      </Flex>
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
    </div>
  );
}
