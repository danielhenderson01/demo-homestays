import axios from "axios";
import { fetchAuthSession } from "aws-amplify/auth";

export const ax = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
});

ax.interceptors.request.use(
  async (config) => {
    try {
      const session = await fetchAuthSession();
      const { idToken } = session.tokens ?? {};
      if (idToken) {
        config.headers.Authorization = `Bearer ${idToken}`;
      }
    } catch (err) {
      console.error("Could not attach token", err);
    }
    return config;
  },
  (error) => Promise.reject(error)
);
