// src/api/authService.js
//
// Dummy auth service — replace with real User Service calls when the backend
// is ready. Currently returns a fake JWT + user object so the frontend auth
// flow (login/signup/profile/guards) can be built and tested end-to-end.

const BASE_URL =
  (import.meta.env.VITE_API_BASE_URL?.trim()) || "http://localhost";

// Fake in-memory user store so signup actually persists during the session.
const userStore = [
  { id: 1, name: "Test User", email: "test@example.com", password: "password123" },
];

let nextId = 2;

const wait = (ms = 400) => new Promise((r) => setTimeout(r, ms));

function makeToken(user) {
  // Dummy JWT-shaped string. Real backend will return a signed JWT.
  const header = btoa(JSON.stringify({ alg: "HS256", typ: "JWT" }));
  const payload = btoa(
    JSON.stringify({ sub: user.id, email: user.email, name: user.name })
  );
  return `${header}.${payload}.dummy-signature`;
}

export async function login({ email, password }) {
  await wait();
  if (!email || !password) throw new Error("Email and password are required");
  const user = userStore.find(
    (u) => u.email.toLowerCase() === email.toLowerCase() && u.password === password
  );
  if (!user) throw new Error("Invalid email or password");
  return { token: makeToken(user), user: { id: user.id, name: user.name, email: user.email } };
}

export async function signup({ name, email, password }) {
  await wait();
  if (!name || !email || !password) throw new Error("All fields are required");
  if (password.length < 6) throw new Error("Password must be at least 6 characters");
  if (userStore.some((u) => u.email.toLowerCase() === email.toLowerCase())) {
    throw new Error("Email already registered");
  }
  const user = { id: nextId++, name: name.trim(), email: email.trim().toLowerCase(), password };
  userStore.push(user);
  return { token: makeToken(user), user: { id: user.id, name: user.name, email: user.email } };
}

export async function getMe() {
  const token = localStorage.getItem("authToken");
  if (!token) return null;
  await wait(200);
  // Decode the dummy payload to recover the user id/email.
  try {
    const payload = token.split(".")[1];
    const decoded = JSON.parse(atob(payload));
    const user = userStore.find((u) => u.id === decoded.sub);
    if (!user) return null;
    return { id: user.id, name: user.name, email: user.email };
  } catch {
    return null;
  }
}

export function logout() {
  localStorage.removeItem("authToken");
}