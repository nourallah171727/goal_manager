import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import SignupForm from "./component/SignUpFrom";
import LoginForm from "./component/LoginForm";
import "./App.css";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* show login by default */}
        <Route path="/" element={<LoginForm />} />
        <Route path="/login" element={<LoginForm />} />
        <Route path="/signup" element={<SignupForm />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;