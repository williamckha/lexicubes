import { type ClassValue, clsx } from "clsx";
import { twMerge } from "tailwind-merge";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export async function sleep(ms: number, signal?: AbortSignal) {
  return new Promise((resolve, reject) => {
    const timeoutId = setTimeout(resolve, ms);
    signal?.addEventListener("abort", (e) => {
      window.clearTimeout(timeoutId);
      reject(new DOMException("Sleep aborted", "AbortError"));
    });
  });
}
