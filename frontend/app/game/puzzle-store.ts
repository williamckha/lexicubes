import { immer } from "zustand/middleware/immer";
import { create } from "zustand/react";
import { persist } from "zustand/middleware";
import { useShallow } from "zustand/react/shallow";
import type { Puzzle, PuzzleCube, PuzzleCubeFace } from "~/game/puzzle-queries";

export const MIN_WORD_LENGTH = 4;

export type WordInfoStatus = "tooShort" | "notInList" | "alreadyFound" | "success";

export interface WordInfoState {
  status: WordInfoStatus;
  word: string;
}

interface PuzzleState {
  foundWords: string[];
  removedCubes: number[];
  numRemainingWordsIncludingFace: Record<number, number>;
  score: number;
  maxScore: number;
}

type PuzzleStoreState = {
  currentWord: string;
  currentPath: PuzzleCubeFace[];
  wordInfo: WordInfoState | null;
  puzzles: Record<number, PuzzleState>;
  actions: PuzzleStoreActions;
};

type PuzzleStoreActions = {
  initializePuzzleStateIfAbsent: (puzzle: Puzzle) => void;
  resetPuzzleState: (puzzle: Puzzle) => void;
  startPath: (puzzle: Puzzle, startingFace: PuzzleCubeFace) => void;
  continuePath: (puzzle: Puzzle, face: PuzzleCubeFace) => void;
  submitPath: (puzzle: Puzzle) => void;

  _tryAddFaceToPath: (puzzle: Puzzle, face: PuzzleCubeFace) => void;
  _updateRemovedCubes: (puzzle: Puzzle) => void;
};

const usePuzzleStore = create<PuzzleStoreState>()(
  persist(
    immer((set, get) => ({
      currentWord: "",
      currentPath: [],
      wordInfo: null,
      puzzles: {},
      actions: {
        initializePuzzleStateIfAbsent: (puzzle: Puzzle) => {
          if (!(puzzle.id in get().puzzles)) {
            get().actions.resetPuzzleState(puzzle);
          }
        },

        resetPuzzleState: (puzzle: Puzzle) => {
          set((state) => {
            const requiredSolutions = puzzle.solutions.filter((sol) => !sol.isBonus);

            const numRemainingWordsIncludingFace: Record<number, number> = {};
            for (const solution of requiredSolutions) {
              for (const faceId of solution.faceIds) {
                numRemainingWordsIncludingFace[faceId] =
                  (numRemainingWordsIncludingFace[faceId] ?? 0) + 1;
              }
            }

            const maxScore = requiredSolutions
              .map((sol) => getNumberOfPointsForWord(sol.word))
              .reduce((acc, score) => acc + score, 0);

            state.puzzles[puzzle.id] = {
              foundWords: [],
              removedCubes: [],
              numRemainingWordsIncludingFace: numRemainingWordsIncludingFace,
              score: 0,
              maxScore: maxScore,
            };

            state.currentWord = "";
            state.currentPath = [];
            state.wordInfo = null;
          });

          get().actions._updateRemovedCubes(puzzle);
        },

        startPath: (puzzle: Puzzle, startingFace: PuzzleCubeFace) => {
          get().actions._tryAddFaceToPath(puzzle, startingFace);
        },

        continuePath: (puzzle: Puzzle, face: PuzzleCubeFace) => {
          const currentPath = get().currentPath;
          if (currentPath.length === 0 || currentPath[currentPath.length - 1].id === face.id) {
            return;
          }

          const indexInPath = currentPath.findIndex((f) => f.id === face.id);
          if (indexInPath != -1) {
            if (indexInPath == currentPath.length - 2) {
              set((state) => {
                state.currentWord = state.currentWord.slice(0, -1);
                state.currentPath.pop();
              });
            }
          } else if (currentPath[currentPath.length - 1].neighbourIds.includes(face.id)) {
            get().actions._tryAddFaceToPath(puzzle, face);
          }
        },

        submitPath: (puzzle: Puzzle) => {
          set((state) => {
            if (state.currentWord.length === 0) {
              return;
            }

            const solution = puzzle.solutions.find((sol) => sol.word === state.currentWord);
            if (state.currentWord.length === 1) {
              state.wordInfo = null;
            } else if (state.currentWord.length < MIN_WORD_LENGTH) {
              state.wordInfo = {
                status: "tooShort",
                word: state.currentWord,
              };
            } else if (solution) {
              if (state.puzzles[puzzle.id].foundWords.includes(state.currentWord)) {
                state.wordInfo = {
                  status: "alreadyFound",
                  word: state.currentWord,
                };
              } else {
                state.puzzles[puzzle.id].foundWords.push(state.currentWord);
                for (const face of state.currentPath) {
                  state.puzzles[puzzle.id].numRemainingWordsIncludingFace[face.id] -= 1;
                }
                state.puzzles[puzzle.id].score += getNumberOfPointsForWord(state.currentWord);
                state.wordInfo = {
                  status: "success",
                  word: state.currentWord,
                };
              }
            } else {
              state.wordInfo = {
                status: "notInList",
                word: state.currentWord,
              };
            }

            state.currentWord = "";
            state.currentPath = [];
          });

          get().actions._updateRemovedCubes(puzzle);
        },

        _tryAddFaceToPath: (puzzle: Puzzle, face: PuzzleCubeFace) => {
          set((state) => {
            if (getCubesBlockingFace(puzzle, face).length === 0) {
              state.currentWord += face.letter;
              state.currentPath.push(face);
            }
          });
        },

        _updateRemovedCubes: (puzzle: Puzzle) => {
          set((state) => {
            const numRemainingWordsIncludingFace =
              state.puzzles[puzzle.id].numRemainingWordsIncludingFace;

            const removedCubes = state.puzzles[puzzle.id].removedCubes;
            const presentCubes = puzzle.cubes.filter((c) => !removedCubes.includes(c.id));

            for (const cube of presentCubes) {
              if (
                numRemainingWordsIncludingFace[cube.topFace.id] === 0 &&
                numRemainingWordsIncludingFace[cube.leftFace.id] === 0 &&
                numRemainingWordsIncludingFace[cube.rightFace.id] === 0
              ) {
                state.puzzles[puzzle.id].removedCubes.push(cube.id);
              }
            }
          });
        },
      },
    })),
    {
      name: "puzzleStoreStorage",
      partialize: (state) => ({ puzzles: state.puzzles }),
    },
  ),
);

export const usePuzzleCurrentWord = () => usePuzzleStore((state) => state.currentWord);

export const usePuzzleCurrentPath = () => usePuzzleStore((state) => state.currentPath);

export const usePuzzleWordInfo = () => usePuzzleStore((state) => state.wordInfo);

export const usePuzzleFoundWords = (puzzleId: number) =>
  usePuzzleStore(useShallow((state) => state.puzzles[puzzleId]?.foundWords ?? []));

export const usePuzzleRemovedCubes = (puzzleId: number) =>
  usePuzzleStore(useShallow((state) => state.puzzles[puzzleId]?.removedCubes ?? []));

export const usePuzzleScore = (puzzleId: number) =>
  usePuzzleStore(
    useShallow((state) => {
      const score = state.puzzles[puzzleId]?.score ?? 0;
      const maxScore = state.puzzles[puzzleId]?.maxScore ?? 0;
      return (score / maxScore) * 100;
    }),
  );

export const usePuzzleActions = () => usePuzzleStore((state) => state.actions);

export function getNumberOfPointsForWord(word: string): number {
  return word.length;
}

function getCubeWithFace(puzzle: Puzzle, face: PuzzleCubeFace): PuzzleCube {
  const cubeWithFace = puzzle.cubes.find(
    (cube) =>
      cube.topFace.id === face.id || cube.leftFace.id === face.id || cube.rightFace.id === face.id,
  );

  if (!cubeWithFace) {
    throw new Error(`Face (id ${face.id}) does not exist in puzzle (id ${puzzle.id})`);
  }

  return cubeWithFace;
}

function isCoordinateWithinPuzzleBounds(puzzle: Puzzle, x: number, y: number, z: number) {
  return (
    x >= 0 &&
    y >= 0 &&
    z >= 0 &&
    x < puzzle.dimensions.lengthX &&
    y < puzzle.dimensions.lengthY &&
    z < puzzle.dimensions.lengthZ
  );
}

function getCubesBlockingFace(puzzle: Puzzle, face: PuzzleCubeFace): PuzzleCube[] {
  const cubesBlockingFace = new Set<PuzzleCube>();

  function getCubesBlockingCoordinate(x: number, y: number, z: number) {
    for (let i = 0; isCoordinateWithinPuzzleBounds(puzzle, x + i, y + i, z + i); i++) {
      const cube = puzzle.cubes.find(
        (cube) => cube.x === x + i && cube.y === y + i && cube.z === z + i,
      );
      if (cube) {
        cubesBlockingFace.add(cube);
      }
    }
  }

  const cube = getCubeWithFace(puzzle, face);
  if (cube.topFace.id === face.id) {
    getCubesBlockingCoordinate(cube.x, cube.y + 1, cube.z);
    getCubesBlockingCoordinate(cube.x + 1, cube.y + 1, cube.z);
    getCubesBlockingCoordinate(cube.x, cube.y + 1, cube.z + 1);
    getCubesBlockingCoordinate(cube.x + 1, cube.y + 1, cube.z + 1);
  } else if (cube.leftFace.id === face.id) {
    getCubesBlockingCoordinate(cube.x, cube.y, cube.z + 1);
    getCubesBlockingCoordinate(cube.x + 1, cube.y, cube.z + 1);
    getCubesBlockingCoordinate(cube.x + 1, cube.y + 1, cube.z + 1);
    getCubesBlockingCoordinate(cube.x, cube.y + 1, cube.z + 1);
  } else if (cube.rightFace.id === face.id) {
    getCubesBlockingCoordinate(cube.x + 1, cube.y, cube.z);
    getCubesBlockingCoordinate(cube.x + 1, cube.y, cube.z + 1);
    getCubesBlockingCoordinate(cube.x + 1, cube.y + 1, cube.z + 1);
    getCubesBlockingCoordinate(cube.x + 1, cube.y + 1, cube.z);
  }

  return Array.from(cubesBlockingFace);
}
