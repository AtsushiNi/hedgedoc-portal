import { Flex, Card } from 'antd';
import { useNavigate } from 'react-router-dom';

const FolderList = props => {
  const { folders } = props
  const navigate = useNavigate();

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

  return (
    <>
      <div style={{ color: "white", textAlign: "left", fontSize: "large" }}>フォルダ</div>
      <Flex wrap="wrap">
        {folders?.map(folder => (
          <Card
            key={folder.id}
            title={folder.title}
            onClick={() => navigate("/folders/" + folder.id)}
            style={folderCardStyle}
            headStyle={cardHeaderStyle}
          ></Card>
        ))}
      </Flex>
    </>
  )
}

export default FolderList;
 