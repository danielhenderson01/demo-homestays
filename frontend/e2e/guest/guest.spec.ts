import { expect, test } from "@playwright/test";

test("authenticated guest can open the guest dashboard", async ({ page }) => {
  await page.goto("/guest/dashboard");
  await expect(page).toHaveURL(new URLPattern({ pathname: "/guest/*" }));
  await expect(page.getByText(/my bookings/i)).toBeVisible();
  await expect(page.getByText(/my reviews/i)).toBeVisible();
});

test("guest cannot access host routes", async ({ page }) => {
  await page.goto("/host/dashboard");
  await expect(page).toHaveURL(new URLPattern({ pathname: "/host/*" }));
  await expect(page.getByText(/403/i)).toBeVisible();
  await expect(page.getByText(/forbidden/i)).toBeVisible();
});
