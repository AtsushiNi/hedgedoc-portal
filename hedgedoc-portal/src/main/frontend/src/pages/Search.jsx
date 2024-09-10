import { Spin, Flex, Button } from "antd";
import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";

const Search = () => {
  const [searchResults, setSearchResults] = useState([]);
  const [loading, setLoading] = useState(true);
  const location = useLocation();
  const navigate = useNavigate();

  const query = new URLSearchParams(location.search).get("query");

  useEffect(() => {
    const fetchData = async () => {
      try {
        const { data: notes } = await axios.get("/api/v1/notes/search", { params: { query }});
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
    </div>
  );
};

export default Search;
