
import { useEffect, useState } from "react";
import { LayoutDashboard, BriefcaseBusiness, Sparkles } from "lucide-react";
import "./App.css";

const API = "http://localhost:8080/api/jobs";

function App() {
  const [title, setTitle] = useState("");
  const [company, setCompany] = useState("");
  const [description, setDescription] = useState("");
  const [jobs, setJobs] = useState([]);
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  async function loadJobs() {
    try {
      const response = await fetch(API);
      if (!response.ok) throw new Error("Could not load jobs");
      setJobs(await response.json());
    } catch {
      setMessage("Backend unavailable. Check Spring Boot and MySQL.");
    }
  }

  useEffect(() => {
    loadJobs();
  }, []);

  async function handleSubmit(event) {
    event.preventDefault();
    setMessage("");
    setLoading(true);

    try {
      const response = await fetch(API, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ title, company, description })
      });

      if (!response.ok) throw new Error("Unable to save job");

      setMessage("Job description saved successfully!");
      setTitle("");
      setCompany("");
      setDescription("");
      await loadJobs();
    } catch {
      setMessage("Could not save. Check the backend and database.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="app">
      <aside className="sidebar">
        <h2><Sparkles size={22} /> SkillSync AI</h2>
        <p className="nav active">
          <LayoutDashboard size={18} /> Dashboard
        </p>
        <p className="nav">
          <BriefcaseBusiness size={18} /> Job descriptions
        </p>
        <div className="sidebar-bottom">
          Your career, in sync.
        </div>
      </aside>

      <main className="main">
        <header className="topbar">
          <div>
            <p className="eyebrow">CAREER WORKSPACE</p>
            <h1>Dashboard</h1>
          </div>
          <span className="avatar">S</span>
        </header>

        <section className="welcome">
          <div>
            <p className="eyebrow">YOUR NEXT OPPORTUNITY STARTS HERE</p>
            <h2>Prepare smarter. Get career-ready.</h2>
            <p>Start with a job description. Build your preparation plan next.</p>
          </div>
          <Sparkles size={42} />
        </section>

        <section className="stats">
          <article className="stat">
            <span>Saved job descriptions</span>
            <strong>{jobs.length}</strong>
          </article>
          <article className="stat">
            <span>Resumes analyzed</span>
            <strong>0</strong>
          </article>
          <article className="stat">
            <span>Preparation tasks completed</span>
            <strong>0</strong>
          </article>
        </section>

        <section className="content">
          <div className="section-heading">
            <div>
              <h2>Add a target job</h2>
              <p>Save the role you want to prepare for.</p>
            </div>
            <BriefcaseBusiness size={24} />
          </div>

          <form onSubmit={handleSubmit}>
            <label htmlFor="title">Job title *</label>
            <input
              id="title"
              value={title}
              onChange={e => setTitle(e.target.value)}
              placeholder="e.g. Software Development Engineer"
              required
            />

            <label htmlFor="company">Company</label>
            <input
              id="company"
              value={company}
              onChange={e => setCompany(e.target.value)}
              placeholder="e.g. Amazon"
            />

            <label htmlFor="description">Job description *</label>
            <textarea
              id="description"
              value={description}
              onChange={e => setDescription(e.target.value)}
              placeholder="Paste the responsibilities, requirements and skills..."
              rows={6}
              required
            />

            <button disabled={loading}>
              {loading ? "Saving..." : "Save job description"}
            </button>

            {message && <p role="status">{message}</p>}
          </form>
        </section>

        <section className="content">
          <h2>Your target jobs</h2>
          {jobs.length === 0 ? (
            <p>No jobs saved yet. Add your first target role above.</p>
          ) : (
            jobs.map(job => (
              <article className="job" key={job.id}>
                <h3>{job.title}</h3>
                <p>{job.company || "Company not specified"}</p>
                <p>{job.description}</p>
              </article>
            ))
          )}
        </section>
      </main>
    </div>
  );
}

export default App;
