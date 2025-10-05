from builtins import print
from locust import HttpUser, task, between
import random, string


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

        # 3. Create a goal for that user
        goal_payload = {
            "name": "My First Goal",
            "category": "HEALTH",
            "type": "PUBLIC",
            "votesToMarkCompleted": 2,
            "tasks": [{"name": "task1", "difficulty": "DIFFICULT"}]
        }
        goal_resp = self.client.post(
            f"/goal/{user_id}",
            json=goal_payload,
            cookies=session_cookie,
            name="/goal/{userId}"
        )
        if not (200 <= goal_resp.status_code < 300):
            print(f"❌ Failed to create goal: {goal_resp.status_code} | Body: {goal_resp.text}")
            return

        goal_id = goal_resp.json().get("id")
        if not goal_id:
            return

        # 4. Create a task under that goal
        task_payload = {"name": "First Task", "difficulty": "EASY"}
        task_resp = self.client.post(
            f"/task/{goal_id}",
            json=task_payload,
            cookies=session_cookie,
            name="/task/{goalId}"
        )
        if not (200 <= task_resp.status_code < 300):
            print(f"❌ Failed to create task: {task_resp.status_code}")
            return

        # 5. Fetch the created goal
        self.client.get(
            f"/goal/{goal_id}",
            cookies=session_cookie,
            name="/goal/{id}"
        )