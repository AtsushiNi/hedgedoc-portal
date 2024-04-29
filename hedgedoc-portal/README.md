# 起動方法
開発時はReactのホットリロードを利用・Spring起動を短時間でしたいため、サーバーを分ける

### 開発
1. Spring Boot サーバー起動
```
mvn spring-boot:run
```

2. React Webサーバー起動
```
cd src/main/frontend
./node/npm start
```
3. localhost:3000にアクセス

### 本番
1. Spring Boot サーバー起動(自動的にReactがビルド・Spring Boot上に配置される)
```
mvn spring-boot:run -Pprod
```

2. localhost:8080にアクセス
