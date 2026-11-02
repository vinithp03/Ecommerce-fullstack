import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import { VitePWA } from "vite-plugin-pwa";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    react(),
    VitePWA({
      registerType: "autoUpdate",
      manifest: {
        name: "Vinith Ecommerce",
        short_name: "ShopApp",
        description: "Full stack eCommerce app",
        theme_color: "#6c47ff",
        background_color: "#ffffff",
        display: "standalone",
        start_url: "/",
        icons: [
          {
            src: "/icons/icon-192.png",
            sizes: "192x192",
            type: "image/png",
          },
          {
            src: "/icons/icon-512.png",
            sizes: "512x512",
            type: "image/png",
          },
        ],
      },
      workbox: {
        globPatterns: ["**/*.{js,css,html,ico,svg}"],
        globIgnores: [
        "images/Hp_spotlight.png",
        "images/Hp_MOBspotlight.png",
        "images/HP_desk_spotlight_frame2.png",
        "images/HP_mob_spotlight_frame2.png",
      ],
        maximumFileSizeToCacheInBytes: 2 * 1024 * 1024,
      },
    }),
  ],
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