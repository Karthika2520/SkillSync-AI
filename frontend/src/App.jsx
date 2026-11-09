
// import { useState } from "react";

// import ResumeManager from "./ResumeManager";
// import SavedJobs from "./SavedJobs";
// import AnalysisForm from "./AnalysisForm";
// import JobDescriptions from "./JobDescriptions";
// import AuthPage from "./AuthPage";
// import InterviewPractice from "./InterviewPractice";

// import "./App.css";

// // Read the email from the JWT subject (sub) for display.
// function getUserEmail() {
//   try {
//     const token = sessionStorage.getItem("token");

//     if (!token) return "User";

//     const payload = token.split(".")[1];
//     const base64 = payload
//       .replace(/-/g, "+")
//       .replace(/_/g, "/");

//     const decoded = JSON.parse(
//       decodeURIComponent(
//         atob(base64)
//           .split("")
//           .map(char =>
//             "%" + char.charCodeAt(0).toString(16).padStart(2, "0")
//           )
//           .join("")
//       )
//     );

//     return decoded.sub || "User";
//   } catch {
//     return "User";
//   }
// }

// export default function App() {
//   const [page, setPage] = useState("dashboard");

//   const [isAuthenticated, setIsAuthenticated] = useState(
//     !!sessionStorage.getItem("token")
//   );

//   const [profileOpen, setProfileOpen] = useState(false);
//   const [selectedAnalysisId, setSelectedAnalysisId] = useState(null);

//   const userEmail = getUserEmail();


//   const navItems = [
//     { id: "dashboard", label: "Dashboard", icon: "⌂" },
//     { id: "resumes", label: "My Resumes", icon: "▤" },
//     { id: "jobs", label: "Saved Jobs", icon: "☆" },
//     { id: "jobDescriptions", label: "Job Descriptions", icon: "▣" },
//     { id: "analysis", label: "Skill Analysis", icon: "✧" }
//     { id: "practice", label: "Interview Practice", icon: "✦" }
//   ];

//   const currentPage = navItems.find(item => item.id === page);

//   const handleLogout = () => {
//     sessionStorage.removeItem("token");
//     setProfileOpen(false);
//     setIsAuthenticated(false);
//     setPage("dashboard");
//   };

//   if (!isAuthenticated) {
//     return (
//       <AuthPage
//         onLoginSuccess={() => {
//           setIsAuthenticated(true);
//           setPage("dashboard");
//         }}
//       />
//     );
//   }

//   return (
//     <div className="app-layout">

//       {/* LEFT NAVIGATION */}
//       <aside className="sidebar">

//         <div className="sidebar-brand">
//           <div className="brand-symbol">S</div>
//           <h2>SkillSync <span>AI</span></h2>
//         </div>

//         <p className="sidebar-label">MAIN MENU</p>

//         <nav className="sidebar-nav">
//           {navItems.map(item => (
//             <button
//               key={item.id}
//               type="button"
//               className={`nav-item ${page === item.id ? "active" : ""}`}
//               onClick={() => {
//                 setPage(item.id);
//                 setProfileOpen(false);
//               }}
//             >
//               <span className="nav-icon">{item.icon}</span>
//               <span>{item.label}</span>
//               {page === item.id && (
//                 <span className="nav-indicator">●</span>
//               )}
//             </button>
//           ))}
//         </nav>

//         <div className="sidebar-bottom">
//           <div className="sidebar-bottom-icon">✦</div>
//           <h3>Grow with purpose.</h3>
//           <p>
//             Build your skills and move closer to your dream career.
//           </p>
//         </div>

//       </aside>

//       {/* RIGHT MAIN AREA */}
//       <main className="main-content">

//         {/* TOP HEADER */}
//         <header className="topbar">

//           <div className="topbar-title">
//             <span>YOUR CAREER WORKSPACE</span>
//             <h2>{currentPage?.label || "Dashboard"}</h2>
//           </div>

//           {/* PROFILE DROPDOWN */}
//           <div className="profile-wrapper">

//             <button
//               className="profile-button"
//               type="button"
//               onClick={() => setProfileOpen(!profileOpen)}
//               aria-label="Open profile"
//               aria-expanded={profileOpen}
//             >
//               <span className="profile-avatar">♙</span>
//               <span className="profile-chevron">
//                 {profileOpen ? "▲" : "▼"}
//               </span>
//             </button>

//             {profileOpen && (
//               <div className="profile-dropdown">
//                 <div className="profile-dropdown-heading">
//                   <div className="profile-avatar large">♙</div>
//                   <div>
//                     <strong>Signed in as</strong>
//                     <p>{userEmail}</p>
//                   </div>
//                 </div>

//                 <div className="profile-divider"></div>

//                 <button
//                   className="profile-logout"
//                   onClick={handleLogout}
//                 >
//                   ↪ Logout
//                 </button>
//               </div>
//             )}

//           </div>

//         </header>

//         {/* PAGE CONTENT */}
//         <div className="page-content">

//           {page === "dashboard" && (
//             <div className="dashboard-container">

//               {/* WELCOME SECTION */}
//               <section className="dashboard-welcome">

//                 <span className="welcome-label">
//                   ✦ YOUR PERSONAL CAREER COMPANION
//                 </span>

//                 <h1>
//                   SkillSync <span>AI</span>
//                 </h1>

//                 <h2>
//                   Prepare today. Succeed tomorrow.
//                 </h2>

//                 <p>
//                   Your AI-powered career preparation platform.
//                   Manage resumes, explore job opportunities,
//                   identify skill gaps, and prepare confidently
//                   for your next career opportunity.
//                 </p>

//               </section>

//               {/* FOUR FEATURE CARDS */}
//               <section className="dashboard-features">

//                 <div className="feature-card">
//                   <div className="feature-icon resume-icon">▤</div>

//                   <h3>My Resumes</h3>

//                   <p>
//                     Upload and organize your resumes in one place.
//                     Maintain your professional profile and prepare
//                     your resume for your target roles.
//                   </p>

//                   <button onClick={() => setPage("resumes")}>
//                     Manage Resumes <span>→</span>
//                   </button>
//                 </div>

//                 <div className="feature-card">
//                   <div className="feature-icon jobs-icon">☆</div>

//                   <h3>Saved Jobs</h3>

//                   <p>
//                     Save interesting job opportunities and keep
//                     their details organized. Build your personal
//                     collection of career opportunities.
//                   </p>

//                   <button onClick={() => setPage("jobs")}>
//                     View Saved Jobs <span>→</span>
//                   </button>
//                 </div>

//                 <div className="feature-card">
//                   <div className="feature-icon description-icon">▣</div>

//                   <h3>Job Descriptions</h3>

//                   <p>
//                     Add and manage job descriptions from companies
//                     you are interested in. Keep track of the skills
//                     and qualifications employers expect.
//                   </p>

//                   <button onClick={() => setPage("jobDescriptions")}>
//                     Explore Job Descriptions <span>→</span>
//                   </button>
//                 </div>

//                 <div className="feature-card">
//                   <div className="feature-icon analysis-icon">✧</div>

//                   <h3>Skill Analysis</h3>

//                   <p>
//                     Compare your resume with job requirements.
//                     Discover matching skills, identify missing
//                     skills, and understand where to improve.
//                   </p>

//                   <button onClick={() => setPage("analysis")}>
//                     Analyze My Skills <span>→</span>
//                   </button>
//                 </div>

//               </section>

//               {/* BOTTOM MESSAGE */}
//               <section className="dashboard-message">
//                 <span>✦</span>
//                 <div>
//                   <h3>Your career journey starts here.</h3>
//                   <p>
//                     Stay consistent, strengthen your skills,
//                     and take one step closer to your goals.
//                   </p>
//                 </div>
//               </section>

//             </div>
//           )}

//           {page === "resumes" && <ResumeManager />}
//           {page === "jobs" && <SavedJobs />}
//           {page === "jobDescriptions" && <JobDescriptions />}
//           {page === "analysis" && <AnalysisForm />}
//           {page === "analysis" && (
//   <AnalysisForm
//     onStartPractice={(id) => {
//       setSelectedAnalysisId(id);
//       setPage("practice");
//     }}
//   />
// )}

// {page === "practice" && (
//   <InterviewPractice analysisId={selectedAnalysisId} />
// )}

//         </div>

//         {/* FOOTER */}
//         <footer className="app-footer">
//           <span>© 2026 SkillSync AI. All rights reserved.</span>
//           <span>Your career journey, one step at a time. ✦</span>
//         </footer>

//       </main>
//     </div>
//   );
// }


import { useState } from "react";

import ResumeManager from "./ResumeManager";
import SavedJobs from "./SavedJobs";
import AnalysisForm from "./AnalysisForm";
import JobDescriptions from "./JobDescriptions";
import AuthPage from "./AuthPage";
import InterviewPractice from "./InterviewPractice";

import "./App.css";

// Read the email from the JWT subject (sub) for display.
function getUserEmail() {
  try {
    const token = sessionStorage.getItem("token");

    if (!token) return "User";

    const payload = token.split(".")[1];
    const base64 = payload
      .replace(/-/g, "+")
      .replace(/_/g, "/");

    const decoded = JSON.parse(
      decodeURIComponent(
        atob(base64)
          .split("")
          .map(char =>
            "%" + char.charCodeAt(0).toString(16).padStart(2, "0")
          )
          .join("")
      )
    );

    return decoded.sub || "User";
  } catch {
    return "User";
  }
}

export default function App() {
  const [page, setPage] = useState("dashboard");

  const [isAuthenticated, setIsAuthenticated] = useState(
    !!sessionStorage.getItem("token")
  );

  const [profileOpen, setProfileOpen] = useState(false);

  // Stores the analysis ID selected for interview practice.
  const [selectedAnalysisId, setSelectedAnalysisId] = useState(null);

  const userEmail = getUserEmail();

  const navItems = [
    { id: "dashboard", label: "Dashboard", icon: "⌂" },
    { id: "resumes", label: "My Resumes", icon: "▤" },
    { id: "jobs", label: "Saved Jobs", icon: "☆" },
    { id: "jobDescriptions", label: "Job Descriptions", icon: "▣" },
    { id: "analysis", label: "Skill Analysis", icon: "✧" },
    { id: "practice", label: "Interview Practice", icon: "✦" }
  ];

  const currentPage = navItems.find(item => item.id === page);

  const handleLogout = () => {
    sessionStorage.removeItem("token");
    setProfileOpen(false);
    setIsAuthenticated(false);
    setSelectedAnalysisId(null);
    setPage("dashboard");
  };

  if (!isAuthenticated) {
    return (
      <AuthPage
        onLoginSuccess={() => {
          setIsAuthenticated(true);
          setPage("dashboard");
        }}
      />
    );
  }

  return (
    <div className="app-layout">

      {/* LEFT NAVIGATION */}
      <aside className="sidebar">

        <div className="sidebar-brand">
          <div className="brand-symbol">S</div>
          <h2>SkillSync <span>AI</span></h2>
        </div>

        <p className="sidebar-label">MAIN MENU</p>

        <nav className="sidebar-nav">
          {navItems.map(item => (
            <button
              key={item.id}
              type="button"
              className={`nav-item ${page === item.id ? "active" : ""}`}
              onClick={() => {
                setPage(item.id);
                setProfileOpen(false);
              }}
            >
              <span className="nav-icon">{item.icon}</span>
              <span>{item.label}</span>

              {page === item.id && (
                <span className="nav-indicator">●</span>
              )}
            </button>
          ))}
        </nav>

        <div className="sidebar-bottom">
          <div className="sidebar-bottom-icon">✦</div>
          <h3>Grow with purpose.</h3>
          <p>
            Build your skills and move closer to your dream career.
          </p>
        </div>

      </aside>

      {/* RIGHT MAIN AREA */}
      <main className="main-content">

        {/* TOP HEADER */}
        <header className="topbar">

          <div className="topbar-title">
            <span>YOUR CAREER WORKSPACE</span>
            <h2>{currentPage?.label || "Dashboard"}</h2>
          </div>

          {/* PROFILE DROPDOWN */}
          <div className="profile-wrapper">

            <button
              className="profile-button"
              type="button"
              onClick={() => setProfileOpen(!profileOpen)}
              aria-label="Open profile"
              aria-expanded={profileOpen}
            >
              <span className="profile-avatar">♙</span>
              <span className="profile-chevron">
                {profileOpen ? "▲" : "▼"}
              </span>
            </button>

            {profileOpen && (
              <div className="profile-dropdown">

                <div className="profile-dropdown-heading">
                  <div className="profile-avatar large">♙</div>

                  <div>
                    <strong>Signed in as</strong>
                    <p>{userEmail}</p>
                  </div>
                </div>

                <div className="profile-divider"></div>

                <button
                  className="profile-logout"
                  onClick={handleLogout}
                >
                  ↪ Logout
                </button>

              </div>
            )}

          </div>

        </header>

        {/* PAGE CONTENT */}
        <div className="page-content">

          {/* DASHBOARD */}
          {page === "dashboard" && (
            <div className="dashboard-container">

              <section className="dashboard-welcome">

                <span className="welcome-label">
                  ✦ YOUR PERSONAL CAREER COMPANION
                </span>

                <h1>
                  SkillSync <span>AI</span>
                </h1>

                <h2>
                  Prepare today. Succeed tomorrow.
                </h2>

                <p>
                  Your AI-powered career preparation platform.
                  Manage resumes, explore job opportunities,
                  identify skill gaps, and prepare confidently
                  for your next career opportunity.
                </p>

              </section>

              {/* FEATURE CARDS */}
              <section className="dashboard-features">

                <div className="feature-card">
                  <div className="feature-icon resume-icon">▤</div>

                  <h3>My Resumes</h3>

                  <p>
                    Upload and organize your resumes in one place.
                    Maintain your professional profile and prepare
                    your resume for your target roles.
                  </p>

                  <button onClick={() => setPage("resumes")}>
                    Manage Resumes <span>→</span>
                  </button>
                </div>

                <div className="feature-card">
                  <div className="feature-icon jobs-icon">☆</div>

                  <h3>Saved Jobs</h3>

                  <p>
                    Save interesting job opportunities and keep
                    their details organized. Build your personal
                    collection of career opportunities.
                  </p>

                  <button onClick={() => setPage("jobs")}>
                    View Saved Jobs <span>→</span>
                  </button>
                </div>

                <div className="feature-card">
                  <div className="feature-icon description-icon">▣</div>

                  <h3>Job Descriptions</h3>

                  <p>
                    Add and manage job descriptions from companies
                    you are interested in. Keep track of the skills
                    and qualifications employers expect.
                  </p>

                  <button onClick={() => setPage("jobDescriptions")}>
                    Explore Job Descriptions <span>→</span>
                  </button>
                </div>

                <div className="feature-card">
                  <div className="feature-icon analysis-icon">✧</div>

                  <h3>Skill Analysis</h3>

                  <p>
                    Compare your resume with job requirements.
                    Discover matching skills, identify missing
                    skills, and understand where to improve.
                  </p>

                  <button onClick={() => setPage("analysis")}>
                    Analyze My Skills <span>→</span>
                  </button>
                </div>

              </section>

              {/* BOTTOM MESSAGE */}
              <section className="dashboard-message">
                <span>✦</span>

                <div>
                  <h3>Your career journey starts here.</h3>
                  <p>
                    Stay consistent, strengthen your skills,
                    and take one step closer to your goals.
                  </p>
                </div>
              </section>

            </div>
          )}

          {/* OTHER PAGES */}
          {page === "resumes" && <ResumeManager />}

          {page === "jobs" && <SavedJobs />}

          {page === "jobDescriptions" && <JobDescriptions />}

          {/* SKILL ANALYSIS */}
          {page === "analysis" && (
            <AnalysisForm
              onStartPractice={(id) => {
                setSelectedAnalysisId(id);
                setPage("practice");
              }}
            />
          )}

          {/* INTERVIEW PRACTICE */}
          {page === "practice" && (
            <InterviewPractice
              analysisId={selectedAnalysisId}
            />
          )}

        </div>

        {/* FOOTER */}
        <footer className="app-footer">
          <span>© 2026 SkillSync AI. All rights reserved.</span>
          <span>Your career journey, one step at a time. ✦</span>
        </footer>

      </main>
    </div>
  );
}
