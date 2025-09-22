from builtins import print
from locust import HttpUser, task, between
import random, string
import base64


def random_user():
    suffix = ''.join(random.choices(string.ascii_lowercase, k=6))
    username = f"user_{suffix}"
    return {
        "username": username,
        "email": f"{username}@mail.com",
        "password": "pwd12345"
    }


class UserApiUser(HttpUser):
    wait_time = between(1, 3)

    @task
    def create_login_get(self):
        """Simulate: create user → login → fetch profile"""

        # 1. Create random user
        user_data = random_user()
        resp = self.client.post("/user", json=user_data)

        if resp.status_code != 201:
            print(f"❌ Failed to create user {user_data['username']}: {resp.status_code}")
            return

        created_user = resp.json()
        user_id = created_user["id"]   # <-- ID comes directly from response
        username = created_user["username"]
        password = user_data["password"]

        # 2. Prepare BasicAuth header
        auth_str = f"{username}:{password}"
        auth_bytes = base64.b64encode(auth_str.encode("utf-8")).decode("utf-8")
        headers = {"Authorization": f"Basic {auth_bytes}"}

        # 3. Fetch profile
        profile_resp = self.client.get(f"/user/{user_id}", headers=headers,name="/user/{id}")

        if profile_resp.status_code == 200:
            print(f"✅ User {username} fetched profile successfully")
        else:
            print(f"❌ Failed profile fetch for {username}: {profile_resp.status_code}")