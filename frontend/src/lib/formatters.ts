export const capitalize = (text: string) =>
  text.charAt(0).toUpperCase() + text.slice(1);

export function toTitleCase(text: string) {
  return text
    .split(" ")
    .map((w) => w.charAt(0).toUpperCase() + w.slice(1))
    .join(" ");
}

const CURRENCY_FORMATTER = new Intl.NumberFormat("en-US", {
  currency: "USD",
  style: "currency",
  minimumFractionDigits: 2,
});

export function formatCurrency(amount: number | string | null) {
  if (typeof amount === "number") {
    return CURRENCY_FORMATTER.format(amount);
  } else if (typeof amount === "string") {
    return CURRENCY_FORMATTER.format(Number(amount));
  } else {
    return "NaN";
  }
}

export function tokenize(q?: string) {
  if (!q) return [];
  return q
    .split(/[^\p{L}\p{N}]+/u)
    .map((s) => s.trim())
    .filter(Boolean)
    .slice(0, 6); // cap terms a bit
}

export function formatQuantity(quantity: number, noun: string): string {
  return quantity === 1 ? `${quantity} ${noun}` : `${quantity} ${noun}s`;
}

export function formatMonth(date: string) {
  const dateTimeFormat = new Intl.DateTimeFormat("en-US", {
    month: "long",
    timeZone: "UTC",
  });
  return dateTimeFormat.format(new Date(date));
}
