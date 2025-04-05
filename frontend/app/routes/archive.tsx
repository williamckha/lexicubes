import { GamePage } from "~/game/game-page";
import type { Route } from "./+types/archive";
import { data } from "react-router";

export async function clientLoader({ params }: Route.ClientLoaderArgs) {
  const puzzleId = Number(params.puzzleId);
  if (!Number.isFinite(puzzleId) || !Number.isInteger(puzzleId)) {
    throw data("Puzzle not found", { status: 404 });
  }
  return { puzzleId };
}

export default function Archive({ loaderData }: Route.ComponentProps) {
  return <GamePage puzzleId={loaderData.puzzleId} />;
}
