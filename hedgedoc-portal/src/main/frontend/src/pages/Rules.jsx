import { useRef, useEffect, useState } from "react";
import { Button, Flex, Modal, Table, Form, Input, Cascader } from "antd";
import { FaTrashAlt } from "react-icons/fa";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { useCookies } from "react-cookie";

const Rules = () => {
  const [rules, setRules] = useState([]);
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [isUpdateModalOpen, setIsUpdateModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [selectedRuleId, setSelectedRuleId] = useState(null);
  const [ruleTitle, setRuleTitle] = useState("");
  const [regularExpression, setRegularExpression] = useState("");
  const [folderTree, setFolderTree] = useState([]);
  const [toFolderIdList, setToFolderIdList] = useState([]);
  const navigate = useNavigate();
  const createInputRef = useRef(null);
  const updateInputRef = useRef(null);
  const [ cookies ] = useCookies();

  useEffect(() => {
    fetchRules();
    fetchFolders();
  }, [])

  useEffect(() => {
    // モーダルが開いた時inputにフォーカスする
    setTimeout(() => {createInputRef.current?.focus()}, 2)
  }, [isCreateModalOpen])

  useEffect(() => {
    // モーダルが開いた時inputにフォーカスする
    setTimeout(() => {updateInputRef.current?.focus()}, 2)
  }, [isUpdateModalOpen])

  // 振り分けルール作成実行時のハンドラ
  const handleCreateRule = async() => {
    let toFolderId = null;
    if (toFolderIdList.length !== 0) toFolderId = toFolderIdList[toFolderIdList.length - 1];
    const data = { title: ruleTitle, regularExpression: regularExpression, folderId: toFolderId };
    await axios.post("/api/v1/rules", data, { headers: { 'x-auth-token': `Bearer ${cookies.accessToken}` }});

    fetchRules();
    setIsCreateModalOpen(false);
  }

  // ルール更新実行時のハンドラ
  const handleUpdateRule = async() => {
    let toFolderId = null;
    if (toFolderIdList.length !== 0) toFolderId = toFolderIdList[toFolderIdList.length - 1];
    const data = { title: ruleTitle, regularExpression: regularExpression, folderId: toFolderId };
    await axios.put("/api/v1/rules/" + selectedRuleId, data, { headers: { 'x-auth-token': `Bearer ${cookies.accessToken}` }});

    fetchRules();
    setIsUpdateModalOpen(false);
  }

  // ルール削除実行時のハンドラ
  const handleDeleteRule = async() => {
    await axios.delete("/api/v1/rules/" + selectedRuleId, { headers: { 'x-auth-token': `Bearer ${cookies.accessToken}` }});

    fetchRules();
    setIsDeleteModalOpen(false);
  }

  // モーダルを閉じたときのハンドラ
  const handleCloseModal = () => {
    setSelectedRuleId(null);
    setRuleTitle("");
    setRegularExpression("");
    setToFolderIdList([]);
  }

  const fetchRules = async() => {
    try {
      const response = await axios.get("/api/v1/rules", { headers: { 'x-auth-token': `Bearer ${cookies.accessToken}` }});
      const rules = response.data.map(rule => ({
        id: rule.id,
        title: rule.title,
        regularExpression: rule.regularExpression,
        toFolderId: rule.folder.id,
        toFolderTitle: rule.folder.title,
      }));
      setRules(rules);
    } catch (error) {
      if (error.response.status === 403) {
        console.log("wrong cookie for HedgeDoc.");
        navigate("/login");
      }
      console.error("Error fetching rules: " + error);
    }
  }

  const fetchFolders = async () => {
    try {
      const { data: folders } = await axios.get("/api/v1/folders", { headers: { 'x-auth-token': `Bearer ${cookies.accessToken}` }});
      setFolderTree(folders);
    } catch (error) {
      if (error.response.status === 403) {
        console.log("wrong cookie for HedgeDoc.");
        navigate("/login");
      }
      console.error("Error fetching folders: " + error);
    }
  }

  // テーブルの行をクリックしたときのハンドラ
  const handleClickRow = (record, rowIndex) => {
    // idがrecord.toFolderIdであるフォルダまでのパスを取得する
    let toFolderIdList = []
    function search(folders, currentPath) {
        for (let folder of folders) {
            let newPath = [...currentPath, folder.id];
            if (folder.id === record.toFolderId) {
                toFolderIdList = newPath;
                return true;
            }
            if (folder.subFolders && folder.subFolders.length > 0) {
                if (search(folder.subFolders, newPath)) {
                    return true;
                }
            }
        }
        return false;
    }
    search(folderTree, []);

    return {
      onClick: event => {
        setSelectedRuleId(record.id);
        setRuleTitle(record.title);
        setRegularExpression(record.regularExpression);
        setToFolderIdList(toFolderIdList);
        setIsUpdateModalOpen(true);
      }
    }
  }

  // 削除ボタンをクリックしたときのハンドラ
  const handleClickDelete = (event, record) => {
    event.stopPropagation();
    setSelectedRuleId(record.id);
    setIsDeleteModalOpen(true);
  }

  const columns = [
    {
      title: "title",
      dataIndex: "title",
      key: "title",
    },
    {
      title: "regular Expression",
      dataIndex: "regularExpression",
      key: "regularExpression",
    },
    {
      title: "folder",
      dataIndex: "toFolderTitle",
      key: "folder"
    },
    {
      title: "action",
      dataIndex: "",
      key: "action",
      width: 80,
      render: (text, record) => <Button type="none" icon={<FaTrashAlt color="red" onClick={event => handleClickDelete(event, record)} />} /> 
    }
  ]

  // 再帰的にフォルダ移動先選択のオプションを生成する
  const mapFolder = folders => (
    folders?.map(folder => ({
      value: folder.id,
      label: folder.title,
      children: mapFolder(folder.subFolders)
    }))
  );
  const folderOptions = mapFolder(folderTree);

  return (
    <>
      <Flex
        justify="space-between"
        style={{ paddingTop: "50px", paddingBottom: "20px" }}
      >
        <div style={{ color: "white", textAlign: "left", fontSize: "large" }}>振り分けルール</div>
        <Button
          type="primary"
          onClick={() => setIsCreateModalOpen(true)}
        >
          +新規ルール
        </Button>
      </Flex>
      <Table
        dataSource={rules}
        columns={columns}
        onRow={handleClickRow}
      />
      <Modal
        title="新規ルール作成"
        open={isCreateModalOpen}
        onCancel={() => setIsCreateModalOpen(false)}
        onOk={handleCreateRule}
        afterClose={handleCloseModal}
      >
        <Form style={{ marginTop: 50 }}>
          <Form.Item label="タイトル">
            <Input ref={createInputRef} value={ruleTitle} onChange={e => setRuleTitle(e.target.value)}/>
          </Form.Item>
          <Form.Item label="正規表現">
            <Input value={regularExpression} onChange={e => setRegularExpression(e.target.value)}/>
          </Form.Item>
          <Form.Item label="振り分け先フォルダ">
            <Cascader
              options={folderOptions}
              expandTrigger="hover"
              changeOnSelect
              onChange={list => setToFolderIdList(list)}
              value={toFolderIdList}
            />
          </Form.Item>
        </Form>
      </Modal>
      <Modal
        title="ルール更新"
        open={isUpdateModalOpen}
        onCancel={() => setIsUpdateModalOpen(false)}
        onOk={handleUpdateRule}
        afterClose={handleCloseModal}
      >
        <Form style={{ marginTop: 50 }}>
          <Form.Item label="タイトル">
            <Input ref={updateInputRef} value={ruleTitle} onChange={e => setRuleTitle(e.target.value)}/>
          </Form.Item>
          <Form.Item label="正規表現">
            <Input value={regularExpression} onChange={e => setRegularExpression(e.target.value)}/>
          </Form.Item>
          <Form.Item label="振り分け先フォルダ">
            <Cascader
              options={folderOptions}
              expandTrigger="hover"
              changeOnSelect
              onChange={list => setToFolderIdList(list)}
              value={toFolderIdList}
            />
          </Form.Item>
        </Form>
      </Modal>
      <Modal
        title="ルールの削除"
        open={isDeleteModalOpen}
        onCancel={() => setIsDeleteModalOpen(false)}
        onOk={handleDeleteRule}
        okType="danger"
        afterClose={() => setSelectedRuleId(null)}
      >
        <p>本当に振り分けルールを削除しますか？</p>
      </Modal>
    </>
  )
}

export default Rules;