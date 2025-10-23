export async function login(username, password) {
    const formData = new URLSearchParams();
    formData.append("username", username);
    formData.append("password", password);
  
    const response = await fetch("http://localhost:8080/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: formData.toString(),
      credentials: "include", // important for session cookies
    });
  
    if (!response.ok) {
      throw new Error("Login failed: " + response.status);
    }
  
    return response;
  }