import { defineConfig, devices } from "@playwright/test";

export default defineConfig({
  testDir: "./e2e",

  forbidOnly: !!process.env.CI,

  retries: process.env.CI ? 1 : 0,

  reporter: [["html", { open: "never" }]],

  use: {
    baseURL: process.env.PLAYWRIGHT_BASE_URL ?? "http://127.0.0.1:5173",

    trace: "retain-on-failure",
    screenshot: "only-on-failure",
    video: "retain-on-failure",
  },

  webServer: {
    command: "npm run dev -- --host 127.0.0.1",

    url: "http://127.0.0.1:5173",

    reuseExistingServer: !process.env.CI,
  },

  projects: [
    {
      name: "setup",
      testMatch: /.*\.setup\.ts/,
    },

    {
      name: "public",
      testMatch: /public\/.*\.spec\.ts/,

      use: {
        ...devices["Desktop Chrome"],
      },
    },

    {
      name: "guest",
      testMatch: /guest\/.*\.spec\.ts/,
      dependencies: ["setup"],

      use: {
        ...devices["Desktop Chrome"],

        storageState: "playwright/.auth/guest.json",
      },
    },

    {
      name: "host",
      testMatch: /host\/.*\.spec\.ts/,
      dependencies: ["setup"],

      use: {
        ...devices["Desktop Chrome"],

        storageState: "playwright/.auth/host.json",
      },
    },
  ],
});
