import random
import string
from locust import HttpUser, task, between
from builtins import print

GOAL_ID = 43649  # existing goal in DB

def random_user():
    suffix = ''.join(random.choices(string.ascii_lowercase, k=6))
    username = f"user_{suffix}"
    return {
        "username": username,
        "email": f"{username}@mail.com",
        "password": "pwd12345"
    }


class OnboardingUser(HttpUser):
    wait_time = between(1, 3)

    @task
    def onboarding_flow(self):
        """Simulate: create user → login (session) → create goal → create task → fetch goal"""

        # 1. Register a new random user
        user_data = random_user()
        resp = self.client.post("/user", json=user_data)

        if not (200 <= resp.status_code < 300):
            print(f"❌ Failed to create user {user_data['username']}: {resp.status_code}")
            return

        created_user = resp.json()
        user_id = created_user["id"]
        username = created_user["username"]
        password = user_data["password"]

        # 2. Login with Spring Security default endpoint (/login expects form fields)
        login_resp = self.client.post(
            "/login",
            data={"username": username, "password": password},  # form login, not JSON
            name="/login"
        )

        if not (200 <= login_resp.status_code < 300):
            print(f"❌ Failed to login {username}: {login_resp.status_code} | Body: {login_resp.text}")
            return
        # Grab the session cookie
        cookies = login_resp.cookies
        if "JSESSIONID" not in cookies:
            print(f"❌ No JSESSIONID received for {username}")
            return

        session_cookie = {"JSESSIONID": cookies["JSESSIONID"]}

        # 3. Join the existing goal
        join_resp = self.client.post(
            f"/goal/{GOAL_ID}/join/{user_id}",
            cookies=session_cookie,
            name="/goal/{goalId}/join/{userId}"
        )

        if not (200 <= join_resp.status_code < 300):
            print(f"❌ Failed to join goal {GOAL_ID} for user {username}: {join_resp.status_code} | Body: {join_resp.text}")