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


class OnboardingUser(HttpUser):
    wait_time = between(1, 3)

    @task
    def onboarding_flow(self):
        """Simulate: create user → login (BasicAuth) → create goal → create task → fetch goal"""

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

        # 2. Prepare BasicAuth header
        auth_str = f"{username}:{password}"
        auth_bytes = base64.b64encode(auth_str.encode("utf-8")).decode("utf-8")
        headers = {"Authorization": f"Basic {auth_bytes}"}

        # 3. Create a goal for that user
        goal_payload = {
            "name": "My First Goal",
            "type": "FITNESS",   # adapt to your GoalType enum
            "category": "HEALTH"
        }
        goal_resp = self.client.post(f"/goal/{user_id}", json=goal_payload, headers=headers, name="/goal/{userId}")
        if not (200 <= goal_resp.status_code < 300):
            print(f"❌ Failed to create goal: {goal_resp.status_code}")
            return

        goal_id = goal_resp.json().get("id")
        if not goal_id:
            return

        # 4. Create a task under that goal
        task_payload = {
            "name": "First Task",
            "difficulty": "EASY"   # adapt to your TaskDifficulty enum if needed
        }
        task_resp = self.client.post(f"/task/{goal_id}", json=task_payload, headers=headers, name="/task/{goalId}")
        if not (200 <= task_resp.status_code < 300):
            print(f"❌ Failed to create task: {task_resp.status_code}")
            return

        task_id = task_resp.json().get("id")

        # 5. Fetch the created goal
        self.client.get(f"/goal/{goal_id}", headers=headers, name="/goal/{id}")