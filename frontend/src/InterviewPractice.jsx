
import { useEffect, useState } from "react";
import "./InterviewPractice.css";

// const API = "http://localhost:8080/api";
const API = import.meta.env.VITE_API_BASE_URL;

function getAuthHeaders() {
  const token = sessionStorage.getItem("token");

  if (!token) {
    throw new Error("Please login first.");
  }

  return {
    Authorization: `Bearer ${token}`,
    "Content-Type": "application/json"
  };
}

function parseFeedback(feedback) {
  if (!feedback) return null;

  if (typeof feedback === "object") return feedback;

  try {
    return JSON.parse(feedback);
  } catch {
    return null;
  }
}

export default function InterviewPractice({ analysisId }) {
  const [difficulty, setDifficulty] = useState("MEDIUM");

  // Stores all five generated questions
  const [questions, setQuestions] = useState([]);

  // Stores the currently selected question
  const [selectedQuestionId, setSelectedQuestionId] = useState(null);

  const [answer, setAnswer] = useState("");
  const [feedback, setFeedback] = useState(null);
  const [history, setHistory] = useState([]);

  const [loadingQuestion, setLoadingQuestion] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [loadingHistory, setLoadingHistory] = useState(true);
  const [message, setMessage] = useState("");

  const selectedQuestion = questions.find(
    q => q.id === selectedQuestionId
  ) || null;

  useEffect(() => {
    if (analysisId) {
      loadHistory();
    }
  }, [analysisId]);

  async function loadHistory() {
    setLoadingHistory(true);

    try {
      const response = await fetch(
        `${API}/analysis/${analysisId}/practice-attempts`,
        {
          method: "GET",
          headers: getAuthHeaders()
        }
      );

      const data = await response.json().catch(() => []);

      if (!response.ok) {
        throw new Error(
          data.message || data.detail || "Unable to load practice history."
        );
      }

      setHistory(Array.isArray(data) ? data : []);
    } catch (error) {
      setMessage(error.message);
    } finally {
      setLoadingHistory(false);
    }
  }

  // =========================================
  // GENERATE FIVE QUESTIONS
  // =========================================

  async function generateQuestion() {
    setLoadingQuestion(true);
    setMessage("");
    setQuestions([]);
    setSelectedQuestionId(null);
    setFeedback(null);
    setAnswer("");

    try {
      const response = await fetch(
        `${API}/analysis/${analysisId}/practice?difficulty=${difficulty}`,
        {
          method: "POST",
          headers: getAuthHeaders()
        }
      );

      const data = await response.json().catch(() => ({}));

      if (!response.ok) {
        throw new Error(
          data.message ||
          data.detail ||
          "Unable to generate questions."
        );
      }

      // Backend returns List<PracticeQuestionResponse>
      if (!Array.isArray(data) || data.length === 0) {
        throw new Error("No questions were returned by the server.");
      }

      setQuestions(data);

      // Select the first question automatically
      setSelectedQuestionId(data[0].id);

      setMessage(
        `${data.length} interview questions generated successfully!`
      );

      await loadHistory();

    } catch (error) {
      setMessage(error.message);
    } finally {
      setLoadingQuestion(false);
    }
  }

  // =========================================
  // SELECT QUESTION
  // =========================================

  function selectQuestion(question) {
    setSelectedQuestionId(question.id);
    setAnswer("");
    setFeedback(null);
    setMessage("");
  }

  // =========================================
  // SUBMIT ANSWER
  // =========================================

  async function submitAnswer(e) {
    e.preventDefault();

    if (!answer.trim()) {
      setMessage("Please enter your answer before submitting.");
      return;
    }

    if (!selectedQuestion?.id) {
      setMessage("Select a question first.");
      return;
    }

    setSubmitting(true);
    setMessage("");

    try {
      const response = await fetch(
        `${API}/analysis/practice-attempts/${selectedQuestion.id}/submit`,
        {
          method: "POST",
          headers: getAuthHeaders(),
          body: JSON.stringify({
            answer: answer.trim()
          })
        }
      );

      const data = await response.json().catch(() => ({}));

      if (!response.ok) {
        throw new Error(
          data.message ||
          data.detail ||
          "Answer submission failed."
        );
      }

      setFeedback(data);

      // Update the selected question's status
      setQuestions(previous =>
        previous.map(q =>
          q.id === selectedQuestion.id
            ? { ...q, status: "EVALUATED" }
            : q
        )
      );

      setMessage("Your answer has been evaluated successfully.");

      await loadHistory();

    } catch (error) {
      setMessage(error.message);
    } finally {
      setSubmitting(false);
    }
  }

  // =========================================
  // VIEW PREVIOUS ATTEMPT
  // =========================================

  function viewAttempt(attempt) {
    setQuestions(previous => {
      const exists = previous.some(q => q.id === attempt.id);

      if (exists) {
        return previous;
      }

      return [attempt, ...previous];
    });

    setSelectedQuestionId(attempt.id);
    setAnswer(attempt.userAnswer || "");
    setFeedback(parseFeedback(attempt.feedback));
    setMessage("");
  }

  if (!analysisId) {
    return (
      <div className="practice-page">
        <div className="practice-empty">
          <div className="practice-empty-icon">◎</div>
          <h2>No analysis selected</h2>
          <p>
            Please open Skill Analysis, create or select an analysis,
            and then start interview practice.
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="practice-page">

      {/* PAGE HEADER */}

      <section className="practice-header">
        <div>
          <span className="practice-eyebrow">
            AI-POWERED PREPARATION
          </span>

          <h1>Interview Practice</h1>

          <p>
            Strengthen your weak areas with questions generated
            from your skill analysis.
          </p>
        </div>

        <div className="practice-header-icon">✦</div>
      </section>

      {/* QUESTION GENERATION */}

      <section className="practice-generator">

        <div className="practice-section-heading">
          <div>
            <h2>Practice Your Skills</h2>
            <p>
              Generate five targeted interview questions
              based on your skill gaps.
            </p>
          </div>

          <span className="practice-section-icon">✧</span>
        </div>

        <div className="practice-controls">

          <div className="practice-input-group">
            <label htmlFor="difficulty">
              Question Difficulty
            </label>

            <select
              id="difficulty"
              value={difficulty}
              onChange={e => setDifficulty(e.target.value)}
              disabled={loadingQuestion || submitting}
            >
              <option value="EASY">Easy</option>
              <option value="MEDIUM">Medium</option>
              <option value="HARD">Hard</option>
            </select>
          </div>

          <button
            type="button"
            className="generate-question-button"
            onClick={generateQuestion}
            disabled={loadingQuestion || submitting}
          >
            {loadingQuestion
              ? "Generating..."
              : "Generate 5 Questions"}

            {!loadingQuestion && <span>→</span>}
          </button>

        </div>

        <p className="practice-helper">
          Five questions are generated using the skills
          identified in your resume analysis.
        </p>

      </section>

      {/* MESSAGE */}

      {message && (
        <div
          className={`practice-message ${
            message.includes("successfully") || message.includes("generated")
              ? "success"
              : "info"
          }`}
          role="status"
        >
          {message}
        </div>
      )}

      {/* LOADING */}

      {loadingQuestion && (
        <div className="practice-loading">
          <div className="practice-spinner"></div>
          <p>AI is preparing your five interview questions...</p>
        </div>
      )}

      {/* GENERATED QUESTION LIST */}

      {questions.length > 0 && (
        <section className="question-card">

          <div className="question-card-header">
            <div>
              <span className="practice-eyebrow">
                GENERATED QUESTIONS
              </span>

              <h2>
                Your Practice Questions ({questions.length})
              </h2>
            </div>

            <span className="history-count">
              {questions.filter(q => q.status === "EVALUATED").length}
              /{questions.length} completed
            </span>
          </div>

          {/* QUESTION SELECTOR */}

          <div className="practice-question-list">

            {questions.map((q, index) => (
              <button
                type="button"
                key={q.id}
                className={`practice-question-item ${
                  selectedQuestionId === q.id ? "active" : ""
                }`}
                onClick={() => selectQuestion(q)}
                disabled={submitting}
              >
                <span className="practice-question-index">
                  {index + 1}
                </span>

                <span className="practice-question-title">
                  {q.skill}
                </span>

                <span className="practice-question-status">
                  {q.status === "EVALUATED"
                    ? "✓ Completed"
                    : "Practice"}
                </span>
              </button>
            ))}

          </div>

          {/* SELECTED QUESTION */}

          {selectedQuestion && (
            <div className="selected-question-content">

              <div className="question-card-header">

                <div>
                  <span className="practice-eyebrow">
                    QUESTION {questions.findIndex(
                      q => q.id === selectedQuestion.id
                    ) + 1}
                  </span>

                  <h2>{selectedQuestion.skill}</h2>
                </div>

                <span
                  className={`difficulty-badge ${
                    (selectedQuestion.difficulty || "MEDIUM").toLowerCase()
                  }`}
                >
                  {selectedQuestion.difficulty || "MEDIUM"}
                </span>

              </div>

              <div className="question-content">
                <span className="question-number">
                  INTERVIEW QUESTION
                </span>

                <p>{selectedQuestion.question}</p>
              </div>

              {/* HINT */}

              {selectedQuestion.hint && (
                <details className="practice-hint">
                  <summary>Need a hint?</summary>
                  <p>{selectedQuestion.hint}</p>
                </details>
              )}

              {/* ANSWER FORM */}

              {!feedback &&
                selectedQuestion.status !== "EVALUATED" && (
                  <form
                    className="practice-answer-form"
                    onSubmit={submitAnswer}
                  >
                    <label htmlFor="practice-answer">
                      Your Answer
                    </label>

                    <textarea
                      id="practice-answer"
                      value={answer}
                      onChange={e => setAnswer(e.target.value)}
                      placeholder="Write your answer here. Explain your approach with examples..."
                      rows={7}
                      maxLength={10000}
                      disabled={submitting}
                    />

                    <div className="answer-footer">

                      <span>
                        {answer.length}/10000 characters
                      </span>

                      <button
                        type="submit"
                        className="submit-answer-button"
                        disabled={submitting || !answer.trim()}
                      >
                        {submitting
                          ? "Evaluating..."
                          : "Submit Answer"}

                        {!submitting && <span>→</span>}
                      </button>

                    </div>
                  </form>
                )}

              {submitting && (
                <div className="evaluation-loading">
                  AI is evaluating your answer...
                </div>
              )}

              {/* FEEDBACK */}

              {feedback && (
                <FeedbackSection feedback={feedback} />
              )}

            </div>
          )}

        </section>
      )}

      {/* PRACTICE HISTORY */}

      <section className="practice-history">

        <div className="practice-section-heading">
          <div>
            <h2>Practice History</h2>
            <p>
              Review your previous questions and attempts.
            </p>
          </div>

          <span className="history-count">
            {history.length}
          </span>
        </div>

        {loadingHistory ? (
          <p className="history-empty">
            Loading practice history...
          </p>
        ) : history.length === 0 ? (
          <div className="history-empty">
            <span>◎</span>
            <h3>No practice attempts yet</h3>
            <p>Generate your first questions to begin.</p>
          </div>
        ) : (
          <div className="history-list">

            {history.map(attempt => (
              <button
                type="button"
                className="history-item"
                key={attempt.id}
                onClick={() => viewAttempt(attempt)}
              >

                <div className="history-item-main">
                  <strong>{attempt.skill}</strong>

                  <p>{attempt.question}</p>

                  <span>
                    {attempt.createdAt
                      ? new Date(attempt.createdAt).toLocaleString()
                      : ""}
                  </span>
                </div>

                <div className="history-item-meta">

                  <span
                    className={`history-status ${
                      (attempt.status || "").toLowerCase()
                    }`}
                  >
                    {attempt.status}
                  </span>

                  <span className="history-arrow">→</span>

                </div>

              </button>
            ))}

          </div>
        )}

      </section>

    </div>
  );
}

// =========================================
// FEEDBACK COMPONENT
// =========================================

function FeedbackSection({ feedback }) {
  const coveredPoints = feedback.coveredPoints || [];
  const missingPoints = feedback.missingPoints || [];
  const nextSteps = feedback.nextSteps || [];

  return (
    <div className="feedback-section">

      <div className="feedback-heading">
        <span>✦</span>

        <div>
          <h3>AI Evaluation Feedback</h3>
          <p>Here's how your answer performed.</p>
        </div>
      </div>

      <div className="feedback-grid">

        <div className="feedback-card covered">
          <h4>✓ Concepts Covered</h4>

          {coveredPoints.length === 0 ? (
            <p>No concepts were identified as covered.</p>
          ) : (
            <ul>
              {coveredPoints.map((point, index) => (
                <li key={index}>{point}</li>
              ))}
            </ul>
          )}
        </div>

        <div className="feedback-card missing">
          <h4>! Missing Points</h4>

          {missingPoints.length === 0 ? (
            <p>No major missing points identified.</p>
          ) : (
            <ul>
              {missingPoints.map((point, index) => (
                <li key={index}>{point}</li>
              ))}
            </ul>
          )}
        </div>

      </div>

      <div className="feedback-explanation">
        <h4>Explanation</h4>
        <p>
          {feedback.explanation || "No explanation provided."}
        </p>
      </div>

      <div className="feedback-next-steps">
        <h4>Recommended Next Steps</h4>

        {nextSteps.length === 0 ? (
          <p>Continue practising this skill.</p>
        ) : (
          <ul>
            {nextSteps.map((step, index) => (
              <li key={index}>{step}</li>
            ))}
          </ul>
        )}

      </div>

    </div>
  );
}
