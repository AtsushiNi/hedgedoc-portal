# ER図

```mermaid
erDiagram
    user {
        Long id
        String hedgedoc_id
    }
    folder {
        Long id
        String title
        Long user_id
        Long parent_folder_id
    }
    folder_note {
        Long id
        Long folder_id
        Long note_id
    }
    note {
        Long id
        String hedgedoc_id
    }

    user ||--o{ folder : ""
    folder |o--o{ folder : ""
    folder ||--o{ folder_note : ""
    folder_note }|--|| note : ""

    user ||..|| HedgeDoc_User : ""
    note ||..|| HedgeDoc_note : ""
```

# エンティティ
## User
HedgeDocのユーザーと1対1に対応する

|カラム名|タイプ|説明|
|--|--|--|
|id|Long|PK|
|hedgedoc_id|String|HegeDocのユーザーのID|

### ユーザー情報取得の流れ
```mermaid
sequenceDiagram
    actor ユーザー
    ユーザー->>HedgedocPortal: アクセス
    activate HedgedocPortal
    HedgedocPortal->>ユーザー: cookie入力画面表示
    ユーザー->>HedgedocPortal: cookie入力
    HedgedocPortal->>HedgeDoc: ユーザー情報取得APIを叩く
    activate HedgeDoc
    HedgeDoc->>HedgedocPortal: ユーザー情報
    deactivate HedgeDoc
    HedgedocPortal->>HedgedocPortal: userエンティティをsessionに保存
    HedgedocPortal->>HedgedocPortal: アクセスされた画面に応じた処理
    HedgedocPortal->>ユーザー: 画面表示 
    deactivate HedgedocPortal
```

## Folder
HedgeDocのノートを格納するエンティティ

|カラム名|タイプ|説明|
|--|--|--|
|id|Long|PK|
|title|String|フォルダのタイトル|
|user_id|Long|所有するユーザーのID(FK)|
|parent_folder_id|Long|親フォルダのID(FK)|

- フォルダは親子構造をとる
- 一番階層が上のフォルダはparent_folder_idがnull

## FolderNote
FolderとNoteの中間テーブル

|カラム名|タイプ|説明|
|--|--|--|
|id|Long|PK|
|folder_id|Long|FK|
|note_id|Long|FK|

## Note
HedgeDocのユーザーと1対1に対応する

|カラム名|タイプ|説明|
|--|--|--|
|id|Long|PK|
|hedgedoc_id|String|HedgeDocのノートのID(FK)|

- トップページでHedgeDocから履歴を取得する際、DBに存在しなければ作成する