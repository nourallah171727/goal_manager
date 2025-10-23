import React, { useState } from "react";
import { login } from "../services/authService";
import { Link } from "react-router-dom";

export default function LoginForm() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
  
    try {
      console.log("Attempting login...");
      const res = await login(username, password);
      console.log("✅ Logged in successfully!");
      alert("Login success!");
    } catch (err) {
      console.error("❌ Login failed:", err);
      alert("Login failed! Check credentials.");
    }
  };

  return (
    <form className="auth-card" onSubmit={handleSubmit}>
      <h2>Welcome back 👋</h2>
      <input
        placeholder="Username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
      />
      <input
        placeholder="Password"
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />
      <button type="submit">Login</button>
      <p>
        Don’t have an account? <Link to="/signup">Sign up</Link>
      </p>
    </form>
  );
}