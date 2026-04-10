import requests

response = requests.post(
    "http://localhost:8080/oauth2/token",
    auth=("client", "secret123"),
    headers={"Content-Type": "application/x-www-form-urlencoded"},
    data={
        "grant_type": "client_credentials",
        "scope": "read",
    },
    timeout=10,
)

print(response.status_code)
print(response.text)