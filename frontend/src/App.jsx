
import { useState } from "react";
import ResumeManager from "./ResumeManager";
import SavedJobs from "./SavedJobs";
import AnalysisForm from "./AnalysisForm";
import JobDescriptions from "./JobDescriptions";
import "./App.css";

export default function App() {
  const [page, setPage] = useState("dashboard");

  const navItems = [
    { id: "dashboard", label: "Dashboard", icon: "⌂" },
    { id: "resumes", label: "My Resumes", icon: "▤" },
    { id: "jobs", label: "Saved Jobs", icon: "☆" },
    { id: "jobDescriptions", label: "Job Descriptions", icon: "▣" },
    { id: "analysis", label: "Analysis", icon: "✦" }
  ];

  const currentPage = navItems.find(item => item.id === page);

  return (
    <div className="app-layout">
      <aside className="sidebar">
        <h2 className="brand">
          SkillSync <span>AI</span>
        </h2>

        <p className="sidebar-label">WORKSPACE</p>

        <nav className="sidebar-nav">
          {navItems.map(item => (
            <button
              key={item.id}
              type="button"
              className={`nav-item ${
                page === item.id ? "active" : ""
              }`}
              onClick={() => setPage(item.id)}
            >
              <span className="nav-icon">{item.icon}</span>
              <span>{item.label}</span>
            </button>
          ))}
        </nav>

        <div className="sidebar-footer">
          <div className="footer-icon">✦</div>
          <p>Build skills. Grow your career.</p>
        </div>
      </aside>

      <main className="main-content">
        <header className="topbar">
          <div>
            <p className="topbar-eyebrow">YOUR CAREER WORKSPACE</p>
            <h1>{currentPage?.label || "Dashboard"}</h1>
            <p className="topbar-description">
              Organize your career journey with SkillSync AI.
            </p>
          </div>

          <div className="profile-avatar">S</div>
        </header>

        <div className="page-content">
          {page === "dashboard" && (
            <section className="welcome-card">
              <div className="welcome-content">
                <span className="welcome-tag">
                  YOUR CAREER, ORGANIZED
                </span>

                <h2>Welcome to SkillSync AI</h2>

                <p>
                  Manage your resumes, keep track of job opportunities,
                  and prepare for your next career milestone.
                </p>

                <div className="dashboard-actions">
                  <button
                    className="primary-button"
                    onClick={() => setPage("resumes")}
                  >
                    Manage Resumes <span>→</span>
                  </button>

                  <button
                    className="secondary-button"
                    onClick={() => setPage("jobs")}
                  >
                    View Saved Jobs
                  </button>
                </div>
              </div>
            </section>
          )}

          {page === "resumes" && <ResumeManager />}

          {page === "jobs" && <SavedJobs />}
          {page === "jobDescriptions" && <JobDescriptions />}
          {page === "analysis" && <AnalysisForm />}
        </div>

        <footer className="app-footer">
          SkillSync AI · Your personal career preparation workspace
        </footer>
      </main>
    </div>
  );
}
