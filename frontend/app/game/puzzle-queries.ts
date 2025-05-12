import { useQuery } from "@tanstack/react-query";
import { getISODate } from "~/lib/date-utils";

export interface Puzzle {
  id: number;
  publishedDate: string;
  isDaily: boolean;
  cubes: PuzzleCube[];
  dimensions: PuzzleDimensions;
  solutions: PuzzleSolution[];
}

export interface PuzzleCube {
  id: number;
  x: number;
  y: number;
  z: number;
  topFace: PuzzleCubeFace;
  leftFace: PuzzleCubeFace;
  rightFace: PuzzleCubeFace;
}

export interface PuzzleCubeFace {
  id: number;
  letter: string;
  neighbourIds: number[];
}

export interface PuzzleDimensions {
  lengthX: number;
  lengthY: number;
  lengthZ: number;
}

export interface PuzzleSolution {
  word: string;
  faceIds: number[];
  isBonus: boolean;
}

export type PuzzleQueryId = number | "daily";

export function usePuzzleQuery(puzzleId: PuzzleQueryId) {
  return useQuery({
    queryKey: ["puzzle", puzzleId],
    queryFn: () => fetchPuzzle(puzzleId),
    refetchOnWindowFocus: false,
  });
}

async function fetchPuzzle(puzzleId: PuzzleQueryId): Promise<Puzzle> {
  const response = await fetch(
    `${import.meta.env.VITE_BACKEND_API_URL}/api/puzzles/` +
      (puzzleId === "daily" ? `daily/${getISODate(new Date())}` : puzzleId),
  );

  if (!response.ok) {
    const text = await response.text();
    throw new Error(`${response.status} ${response.statusText}: ${text}`);
  }

  return response.json();
}
