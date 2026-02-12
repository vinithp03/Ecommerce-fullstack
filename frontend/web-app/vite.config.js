import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      "/cart": {
        target: "http://localhost",
        changeOrigin: true,
      },
      "/gap": {
        target: "http://localhost",
        changeOrigin: true,
      },
      "/catalog": {
        target: "http://localhost",
        changeOrigin: true,
      },
    },
  },
});
