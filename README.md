# 🔐 OAuth2 Authorization Server

## 📌 개요

이 프로젝트는 Spring Boot와 Spring Authorization Server를 기반으로 구현한 OAuth2 Authorization Server입니다.

Spring Security의 확장 포인트(`AuthenticationConverter`, `AuthenticationProvider`)를 활용하여 **custom password grant**를 직접 구현했습니다.

> ⚠️ 주의: Password grant는 OAuth 2.1에서 deprecated 되었으며, 학습 및 내부 용도로 구현되었습니다.

---

## 🏗️ 아키텍처

```
Client (curl)
   ↓
/oauth2/token
   ↓
AuthenticationConverter
   ↓
PasswordGrantAuthenticationToken
   ↓
AuthenticationProvider
   ↓
OAuth2TokenGenerator (JWT)
   ↓
Access Token (JWT)
```

---

## 🚀 기술 스택

* Java 17
* Spring Boot 3.x
* Spring Security 6
* Spring Authorization Server
* Gradle

---

## ⚙️ 주요 기능

* ✅ Custom OAuth2 Grant Type (`password`)
* ✅ JWT Access Token 생성
* ✅ Client 인증 (Basic Authentication)
* ✅ In-memory 사용자 및 클라이언트 저장소
* ✅ Spring Security 기반 인증 흐름 확장

---

## ▶️ 실행 방법

```bash
./gradlew bootRun
```

서버 실행 주소:

```
http://localhost:8080
```

---

## 🔑 토큰 요청 (curl)

```bash
curl -u client:secret \\
-X POST http://localhost:8080/oauth2/token \\
-H "Content-Type: application/x-www-form-urlencoded" \\
-d "grant_type=urn:ietf:params:oauth:grant-type:password&username=user&password=1234&scope=read"
```

---

## 📥 응답 예시

```json
{
  "access_token": "eyJraWQiOiJ...",
  "token_type": "Bearer",
  "expires_in": 300
}
```

---

## 📦 프로젝트 구조

```
authorization-server/
├── config/
│   ├── SecurityConfig.java
│   ├── UserConfig.java
│
├── password/
│   ├── PasswordGrantAuthenticationToken.java
│   ├── PasswordGrantAuthenticationConverter.java
│   ├── PasswordGrantAuthenticationProvider.java
│
└── AuthorizationServerApplication.java
```

---

## 🧠 핵심 개념

### 1. AuthenticationConverter

* HTTP 요청을 Authentication 객체로 변환

### 2. AuthenticationProvider

* username/password 검증 수행
* Access Token 생성

### 3. Custom Grant Type

* Spring Authorization Server의 확장 구조를 활용하여 구현

---

## ⚠️ 참고 사항

* Password grant는 **운영 환경에서 권장되지 않음**
* 대신 아래 방식 사용 권장:

  * Authorization Code + PKCE
  * Client Credentials

