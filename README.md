# OAuth2 Client Credentials Grant Authorization Server

## Client Credentials Grant
- Server to Server
- 클라이언트가 자신의 client_id, client_secret 같은 자격 증명으로 토큰 엔드포인트 호출
- 인증 서버가 access token 발급
- 그 토큰으로 API 호출

## 테스트 : curl 클라이언트

```shell
curl -X POST http://localhost:8080/oauth2/token \
  -u client:secret123 \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials&scope=read"
```

## 테스트 : Python 클라이언트

```shell
cd client
python3 client.py
```