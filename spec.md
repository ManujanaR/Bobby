# Bobby: Product Spec

## 1. Purpose
Let people ask questions and get answers from verified human knowledge. When the bank has no answer, the question goes to people who might know it. Their answer is saved for next time.

## 2. Users
- **Askers:** people with a question
- **Contributors:** people who answer questions and add entries
- **Reviewers:** trusted contributors who verify entries in a category

## 3. Core Concepts
- **Entry:** question, answer, steps, tags, category, optional source, contributor, status
- **Status:** `UNVERIFIED` → `VERIFIED` (or `FLAGGED` / `OUTDATED`)
- **Gap:** a question with no strong match in the bank
- **Verification:** an entry becomes VERIFIED when 2 different contributors confirm it, or one reviewer in that category approves it

## 4. MVP Scope (Phase 1, Android app)
In scope:
- Sign in with a simple account (email + password or magic link)
- Ask a question: see verified matches, or log it as a gap
- Browse and search entries with paged lists
- Answer open questions and add entries
- Confirm entries from other contributors
- Flag wrong or outdated entries
- Contributor profile with skill tags
- Show only VERIFIED answers by default; UNVERIFIED shown with a label

Out of scope for MVP:
- Voice input and output
- YouTube transcript import
- LLM-generated answers or summaries
- Payments, iOS app, real-time calls

## 5. Functional Requirements
1. **Ask:** the server returns up to 5 matching entries ranked by relevance, or "no answer yet" and creates an open question.
2. **Answer:** a contributor replies to an open question. The reply becomes an UNVERIFIED entry linked to that question.
3. **Confirm:** a different contributor confirms an entry. Two confirmations verify it.
4. **Flag:** any user can flag an entry. Flagged entries are hidden from default search until reviewed.
5. **Route:** open questions are shown first to contributors whose skill tags match the question's tags.
6. **Notify:** the asker is notified when their question gets a verified answer.
7. **Credit:** every entry shows its contributor.

## 6. Data Model (draft)
- `users` (id, name, email, created_at)
- `contributor_skills` (user_id, tag)
- `questions` (id, asker_id, text, tags, status: OPEN/ANSWERED, created_at)
- `entries` (id, question_id nullable, title, body, tags, category, contributor_id, status, created_at, updated_at)
- `confirmations` (entry_id, user_id, created_at), unique on (entry_id, user_id)
- `flags` (entry_id, user_id, reason, created_at)

## 7. API (v1, all JSON, paged with `page` and `size=10`)
- `POST /questions` ask a question, returns matches or creates a gap
- `GET /questions/open` open questions routed to the current user
- `POST /questions/{id}/answer` create an entry
- `POST /entries/{id}/confirm`
- `POST /entries/{id}/flag`
- `GET /entries/search?q=&tags=&page=`
- `GET /entries/{id}`

## 8. Non-Functional Requirements
- Search responds in under 300 ms on 100k entries (server side)
- App cold start under 3 seconds on a 1GB RAM Android phone
- App works on Android 7.0 (API 24) and newer
- Lists load 10 items at a time
- Works on slow mobile data; failed requests show a retry button
- No personal data in logs

## 9. Milestones
1. **Week 1:** Ktor server skeleton, Flyway migrations, entries CRUD, Testcontainers tests
2. **Week 2:** Keyword search (tsvector + pg_trgm), ask endpoint, gap logging
3. **Week 3:** Android app skeleton (Compose), login, ask screen, results list
4. **Week 4:** Answer and confirm flow, verification rules, routing by tags
5. **Week 5:** Flagging, notifications, low-RAM testing on a real cheap phone
6. **Week 6:** Closed beta APK with 10 to 20 people
7. **Later:** Voice in/out (Android SpeechRecognizer and TextToSpeech first, Whisper on server later), YouTube transcript import, optional LLM cleanup

## 10. Open Questions
- Who decides a reviewer's category expertise? (Start with you, then peer votes)
- Minimum confirmations for sensitive topics such as health or legal?
- Will contributors be paid, credited, or volunteers?

## 11. Success Criteria for MVP
- 50 verified entries across at least 3 categories
- 70% of test questions answered from the bank without human help
- Median time from open question to verified answer under 24 hours
- App runs on a 1GB RAM phone with no crashes over 20 minutes of use