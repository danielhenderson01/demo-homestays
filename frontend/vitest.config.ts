import { configDefaults, defineConfig, mergeConfig } from "vitest/config";

import viteConfig from "./vite.config";

export default mergeConfig(
  viteConfig,
  defineConfig({
    test: {
      environment: "jsdom",

      environmentOptions: {
        jsdom: {
          url: "http://localhost:5173",
        },
      },

      setupFiles: ["./src/test/setup.ts"],

      clearMocks: true,
      restoreMocks: true,

      exclude: [...configDefaults.exclude, "e2e/**"],
    },
  })
);
