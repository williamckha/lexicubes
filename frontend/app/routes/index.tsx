import type { Route } from "./+types";
import { redirect } from "react-router";

export async function clientLoader({}: Route.ClientLoaderArgs) {
  return redirect("daily");
}
