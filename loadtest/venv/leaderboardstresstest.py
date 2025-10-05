import random
import string
from locust import HttpUser, task, between
from builtins import print


GOAL_ID = 43649  # fixed goal
TOP_K = 10       # default value

def random_user():
    suffix = ''.join(random.choices(string.ascii_lowercase, k=6))
    username = f"user_{suffix}"
    return {
        "username": username,
        "email": f"{username}@mail.com",
        "password": "pwd12345"
    }

class LeaderboardUser(HttpUser):
    wait_time = between(1, 2)

    def on_start(self):
        """
        Runs when each simulated user starts:
        - Register a new user
        - Login via Spring Security form login
        - Store JSESSIONID cookie for authenticated requests
        """
        user_data = random_user()
        resp = self.client.post("/user", json=user_data)

        if not (200 <= resp.status_code < 300):
            print(f"❌ Failed to create user {user_data['username']}: {resp.status_code}")
            self.session_cookie = None
            return

        created_user = resp.json()
        self.user_id = created_user["id"]
        self.username = created_user["username"]
        self.password = user_data["password"]

        login_resp = self.client.post(
            "/login",
            data={"username": self.username, "password": self.password},  # form login
            name="/login"
        )

        if not (200 <= login_resp.status_code < 400):
            print(f"❌ Failed to login {self.username}: {login_resp.status_code} | {login_resp.text}")
            self.session_cookie = None
            return

        # Grab JSESSIONID (client.cookie_jar auto-persists too, but we keep explicit)
        jsess = self.client.cookies.get("JSESSIONID") or login_resp.cookies.get("JSESSIONID")
        if not jsess:
            print(f"❌ No JSESSIONID received for {self.username}")
            self.session_cookie = None
            return

        self.session_cookie = {"JSESSIONID": jsess}

    @task
    def fetch_leaderboard(self):
        """
        Each user requests the top-K leaderboard for the same goal.
        """
        if not self.session_cookie:
            return

        self.client.get(
            f"/leaderboard/{GOAL_ID}/top?k={TOP_K}",
            cookies=self.session_cookie,
            name="/goal/{goalId}/top"
        )