import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button, Card, Flex } from 'antd';
import dayjs from 'dayjs';
import axios from 'axios';

export default function Home() {
  const navigate = useNavigate();
  const [notes, setNotes] = useState([]);

  useEffect(() => {
    const fetchHistory = async() => {
      try {
        const response = await axios.get("/api/v1/history");
        setNotes(response.data.history);
      } catch (error) {
        if (error.response.status === 403) {
          console.log("wrong cookie for HedgeDoc.");
          navigate("/cookie-setting");
        }
        console.error("Error fetching history: " + error);
      }
    }
    fetchHistory();
  }, [])

    return (
      <div className="container">
        <Flex justify='flex-end' style={{ paddingTop: "50px", paddingBottom: "20px"}}>
          <Button type="primary">+新規ノート</Button>
        </Flex>
        <Flex wrap="wrap" >
          {notes.map(note => (
            <Card
              key={note.id}
              title={note.text}
              onClick={() => window.open("http://localhost:3000/" + note.id, "_blank")}
              style={{ width: "280px", height: "140px", margin: "20px", cursor: "pointer" }}
            >
              <p>{dayjs(note.time).format("YYYY-MM-DD HH:mm")}</p>
            </Card>
          ))}
        </Flex>
      </div>
    )
}