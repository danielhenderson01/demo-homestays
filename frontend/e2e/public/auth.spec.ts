import { expect, test } from "@playwright/test";

test("shows the customized sign-in form", async ({ page }) => {
  await page.goto("/");

  await expect(
    page.getByRole("heading", {
      name: "Demo Homestays",
    })
  ).toBeVisible();

  await expect(page.getByLabel("Email")).toBeVisible();
  await expect(page.getByPlaceholder("Enter your password")).toBeVisible();

  await expect(
    page.getByRole("button", {
      name: /^sign in$/i,
    })
  ).toBeVisible();

  await expect(
    page.getByRole("button", {
      name: /sign up here/i,
    })
  ).toBeVisible();
});

test("allows a new user to choose Guest or Host", async ({ page }) => {
  await page.goto("/");

  await page
    .getByRole("button", {
      name: /sign up here/i,
    })
    .click();

  await expect(
    page.getByRole("radio", {
      name: "Guest",
    })
  ).toBeVisible();

  await expect(
    page.getByRole("radio", {
      name: "Host",
    })
  ).toBeVisible();

  await expect(
    page.getByRole("button", {
      name: /create account/i,
    })
  ).toBeVisible();
});
