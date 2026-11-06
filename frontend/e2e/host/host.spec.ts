import { expect, test } from "@playwright/test";

test("authenticated host can open the host dashboard", async ({ page }) => {
  await page.goto("/host/dashboard");
  await expect(page).toHaveURL(new URLPattern({ pathname: "/host/*" }));
  await expect(page.getByText(/total revenue/i)).toBeVisible();
  await expect(page.getByText(/average rating/i)).toBeVisible();
});

test("host cannot access guest routes", async ({ page }) => {
  await page.goto("/guest/dashboard");
  await expect(page).toHaveURL(new URLPattern({ pathname: "/guest/*" }));
  await expect(page.getByText(/403/i)).toBeVisible();
  await expect(page.getByText(/forbidden/i)).toBeVisible();
});
