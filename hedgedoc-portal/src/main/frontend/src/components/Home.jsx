import { useEffect, useState } from 'react';
import { Button, Card, Flex } from 'antd';
import axios from 'axios';
import '../css/Home.css';

export default function Home() {
  const [notes, setNotes] = useState([]);

  useEffect(() => {
    const fetchHistory = async() => {
      try {
        const response = await axios.get("/api/v1/history");
        setNotes(response.data.history);
        console.log(response.data.history);
      } catch (error) {
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
            <Card title={note.text} style={{ width: "280px", height: "140px", margin: "20px" }}>
              <p>{note.time}</p>
            </Card>
          ))}
        </Flex>
      </div>
    )
}