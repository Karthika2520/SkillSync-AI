
import { useState } from "react";
import "./AuthPage.css";

const API_URL = "http://localhost:8080/api/auth";

function AuthPage({ onLoginSuccess }) {
    const [isLogin, setIsLogin] = useState(true);

    const [form, setForm] = useState({
        name: "",
        email: "",
        password: ""
    });

    const [message, setMessage] = useState("");
    const [loading, setLoading] = useState(false);

    const handleChange = (e) => {
        setForm({
            ...form,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage("");
        setLoading(true);

        try {
            const endpoint = isLogin ? "/login" : "/register";

            const response = await fetch(`${API_URL}${endpoint}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(
                    isLogin
                        ? {
                            email: form.email,
                            password: form.password
                        }
                        : {
                            name: form.name,
                            email: form.email,
                            password: form.password
                        }
                )
            });

            const data = await response.json().catch(() => null);

            if (!response.ok) {
                throw new Error(
                    data?.message || data?.error ||
                    "Something went wrong. Please try again."
                );
            }

            if (isLogin) {
                sessionStorage.setItem("token", data.token);
                onLoginSuccess();
            } else {
                setMessage("Registration successful! Please log in.");
                setIsLogin(true);
                setForm({
                    name: "",
                    email: form.email,
                    password: ""
                });
            }

        } catch (error) {
            setMessage(error.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-container">
            <div className="auth-card">

                <h1>SkillSync AI</h1>
                <p className="auth-subtitle">
                    Your AI-powered career preparation platform
                </p>

                <div className="auth-tabs">
                    <button
                        type="button"
                        className={isLogin ? "active" : ""}
                        onClick={() => {
                            setIsLogin(true);
                            setMessage("");
                        }}
                    >
                        Login
                    </button>

                    <button
                        type="button"
                        className={!isLogin ? "active" : ""}
                        onClick={() => {
                            setIsLogin(false);
                            setMessage("");
                        }}
                    >
                        Register
                    </button>
                </div>

                <form onSubmit={handleSubmit}>

                    {!isLogin && (
                        <div className="auth-field">
                            <label>Full Name</label>
                            <input
                                type="text"
                                name="name"
                                placeholder="Enter your name"
                                value={form.name}
                                onChange={handleChange}
                                required
                            />
                        </div>
                    )}

                    <div className="auth-field">
                        <label>Email</label>
                        <input
                            type="email"
                            name="email"
                            placeholder="Enter your email"
                            value={form.email}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="auth-field">
                        <label>Password</label>
                        <input
                            type="password"
                            name="password"
                            placeholder="Enter your password"
                            value={form.password}
                            onChange={handleChange}
                            minLength={8}
                            required
                        />
                    </div>

                    {message && (
                        <p className="auth-message">{message}</p>
                    )}

                    <button
                        type="submit"
                        className="auth-submit"
                        disabled={loading}
                    >
                        {loading
                            ? "Please wait..."
                            : isLogin
                                ? "Login"
                                : "Create Account"}
                    </button>

                </form>

                <p className="auth-switch">
                    {isLogin
                        ? "Don't have an account?"
                        : "Already have an account?"}

                    <button
                        type="button"
                        onClick={() => {
                            setIsLogin(!isLogin);
                            setMessage("");
                        }}
                    >
                        {isLogin ? " Register" : " Login"}
                    </button>
                </p>

            </div>
        </div>
    );
}

export default AuthPage;
