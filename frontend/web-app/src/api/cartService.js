const BASE_URL =
    (import.meta.env.VITE_API_BASE_URL?.trim()) ||
    "";

// Hardcoded until User Service is ready
const USER_ID = 1;

async function httpJson(url, options = {}) {
  const res = await fetch(url, {
    headers: { "Content-Type": "application/json" },
    ...options,
  });

  if (!res.ok) {
    const text = await res.text().catch(() => "");
    throw new Error(
        `HTTP ${res.status}: ${res.statusText} ${text}`
    );
  }

  if (res.status === 204) return null;

  return res.json();
}

export async function fetchCart() {
  const data = await httpJson(`${BASE_URL}/cart/v1/users/${USER_ID}/items`, {
    method: "GET",
  });

  return Array.isArray(data) ? data : [];
}

export async function upsertCartItem(id) {
  if (id == null)
    throw new Error("id is required to add cart item");

  const data = await httpJson(
      `${BASE_URL}/cart/v1/users/${USER_ID}/items/${id}`,
      {
        method: "POST",
      }
  );

  return data;
}

export async function removeCartItem(id) {
  if (id == null)
    throw new Error("id is required to remove a cart item");

  await httpJson(
      `${BASE_URL}/cart/v1/users/${USER_ID}/items/${id}`,
      {
        method: "DELETE",
      }
  );

  return true;
}