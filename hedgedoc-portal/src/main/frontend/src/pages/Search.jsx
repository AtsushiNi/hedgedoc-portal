import { Spin, Flex, Button, Card } from "antd";
import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import dayjs from "dayjs";
import axios from "axios";
import { useCookies } from "react-cookie";

const Search = () => {
  const [searchResults, setSearchResults] = useState([]);
  const [loading, setLoading] = useState(true);
  const location = useLocation();
  const navigate = useNavigate();
  const [ cookies ] = useCookies();

  const HEDGEDOC_URL = "https://localhost:3000";

  const query = new URLSearchParams(location.search).get("query");

  useEffect(() => {
    const fetchData = async () => {
      try {
        const { data: notes } = await axios.get("/api/v1/notes/search", { params: { query }, headers: { 'x-auth-token': `Bearer ${cookies.accessToken}` }});
        setSearchResults(notes);
        setLoading(false);
      } catch (error) {
        if (error.response.status === 403) {
          console.log("wrong cookie for HedgeDoc.");
          navigate("/login");
        }
        console.error("Error search: " + error);
      }
    };
    fetchData();
  }, [query]);

  if (loading) return <Spin tip="Loading..." />;

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
  }
  const cardHeaderStyle = {
    minHeight: 40,
    textAlign: "left",
    color: "black",
  }

  const cardHead = note => (
    <div style={{display: "flex", justifyContent: "space-between"}}>
      <div
        onClick={() => window.open(HEDGEDOC_URL + "/" + note.hedgedocId, "_blank")}
        style={{ whiteSpace: "normal", width: "200px" }}
      >{note.title}</div>
    </div>
  )

  return (
    <div className="container">
      <Flex
        justify="space-between"
        style={{ paddingTop: "50px", paddingBottom: "20px" }}
      >
        <div style={{ color: "white", textAlign: "left", fontSize: "large" }}>
          検索結果
        </div>
      </Flex>

      <Flex wrap="wrap">
        {searchResults?.map(note => (
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
    </div>
  );
};

export default Search;
