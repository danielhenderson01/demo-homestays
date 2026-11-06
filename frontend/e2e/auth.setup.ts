import { expect, test as setup, type Page } from "@playwright/test";

const guestAuthFile = "playwright/.auth/guest.json";

const hostAuthFile = "playwright/.auth/host.json";

function requiredEnv(name: string) {
  const value = process.env[name];

  if (!value) {
    throw new Error(`Missing environment variable: ${name}`);
  }

  return value;
}

async function signIn(page: Page, email: string, password: string) {
  await page.goto("/");

  await page.getByLabel("Email").fill(email);

  await page.getByPlaceholder("Enter your password").fill(password);

  await page
    .getByRole("button", {
      name: /^sign in$/i,
    })
    .click();

  await expect(
    page.getByRole("button", {
      name: /dashboard/i,
    })
  ).toBeVisible();
}

setup("authenticate guest", async ({ page }) => {
  await signIn(
    page,
    requiredEnv("TEST_GUEST_EMAIL"),
    requiredEnv("TEST_GUEST_PASSWORD")
  );

  await page.context().storageState({
    path: guestAuthFile,
  });
});

setup("authenticate host", async ({ page }) => {
  await signIn(
    page,
    requiredEnv("TEST_HOST_EMAIL"),
    requiredEnv("TEST_HOST_PASSWORD")
  );

  await page.context().storageState({
    path: hostAuthFile,
  });
});
