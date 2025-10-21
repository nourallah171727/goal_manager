import React, { useState } from "react";
import { login } from "../services/authService";

export default function LoginForm() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
  
    try {
      console.log("Attempting login...");
      const res = await login(email, password);
      console.log("✅ Logged in successfully!");
      alert("Login success!");
    } catch (err) {
      console.error("❌ Login failed:", err);
      alert("Login failed! Check credentials.");
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <h2>Login</h2>
      <input
        placeholder="Email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
      />
      <input
        placeholder="Password"
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />
      <button type="submit">Login</button>
    </form>
  );
}