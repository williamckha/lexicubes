import { immer } from "zustand/middleware/immer";
import { create } from "zustand/react";
import { persist } from "zustand/middleware";
import { useShallow } from "zustand/react/shallow";
import type { Puzzle, PuzzleCube, PuzzleCubeFace } from "~/game/puzzle-queries";
import { MIN_WORD_LENGTH } from "~/game/game-constants";

type WordInfoStatus = "tooShort" | "notInList" | "alreadyFound" | "success";

interface WordInfoState {
  status: WordInfoStatus;
  word: string;
  numPoints: number;
  isBonus: boolean;
}

interface PuzzleState {
  requiredWordsFound: string[];
  bonusWordsFound: string[];
  removedCubes: number[];
  numRemainingWordsIncludingFace: Record<number, number>;
  numPoints: number;
  maxNumPoints: number;
}

interface PuzzleStoreState {
  currentWord: string;
  currentPath: PuzzleCubeFace[];
  wordInfo: WordInfoState | null;
  puzzles: Record<number, PuzzleState>;
  actions: PuzzleStoreActions;
}

interface PuzzleStateChange {
  numPoints: number;
  numRequiredWordsFound: number;
  numBonusWordsFound: number;
}

interface PuzzleStoreActions {
  initializePuzzleStateIfAbsent: (puzzle: Puzzle) => void;
  resetPuzzleState: (puzzle: Puzzle) => void;
  startPath: (puzzle: Puzzle, startingFace: PuzzleCubeFace) => void;
  continuePath: (puzzle: Puzzle, face: PuzzleCubeFace) => void;
  submitPath: (puzzle: Puzzle) => PuzzleStateChange | null;

  _tryAddFaceToPath: (puzzle: Puzzle, face: PuzzleCubeFace) => void;
  _updateRemovedCubes: (puzzle: Puzzle) => void;
}

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
            const allFaces = puzzle.cubes.flatMap((cube) => [
              cube.topFace,
              cube.leftFace,
              cube.rightFace,
            ]);

            const numRemainingWordsIncludingFace: Record<number, number> = {};
            for (const face of allFaces) {
              numRemainingWordsIncludingFace[face.id] = 0;
            }

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
              requiredWordsFound: [],
              bonusWordsFound: [],
              removedCubes: [],
              numRemainingWordsIncludingFace: numRemainingWordsIncludingFace,
              numPoints: 0,
              maxNumPoints: maxScore,
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

        submitPath: (puzzle: Puzzle): PuzzleStateChange | null => {
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
                numPoints: 0,
                isBonus: false,
              };
            } else if (solution) {
              const puzzleState = state.puzzles[puzzle.id];
              if (
                puzzleState.requiredWordsFound.includes(state.currentWord) ||
                puzzleState.bonusWordsFound.includes(state.currentWord)
              ) {
                state.wordInfo = {
                  status: "alreadyFound",
                  word: state.currentWord,
                  numPoints: 0,
                  isBonus: false,
                };
              } else {
                const solution = puzzle.solutions.find((sol) => sol.word === state.currentWord);

                const isBonus = solution?.isBonus ?? false;
                if (isBonus) {
                  puzzleState.bonusWordsFound.push(state.currentWord);
                } else {
                  puzzleState.requiredWordsFound.push(state.currentWord);
                }

                const numPoints = !isBonus ? getNumberOfPointsForWord(state.currentWord) : 0;
                puzzleState.numPoints += numPoints;

                if (solution && !solution.isBonus) {
                  for (const faceId of solution.faceIds) {
                    puzzleState.numRemainingWordsIncludingFace[faceId] -= 1;
                  }
                }

                state.wordInfo = {
                  status: "success",
                  word: state.currentWord,
                  numPoints: numPoints,
                  isBonus: isBonus,
                };
              }
            } else {
              state.wordInfo = {
                status: "notInList",
                word: state.currentWord,
                numPoints: 0,
                isBonus: false,
              };
            }

            state.currentWord = "";
            state.currentPath = [];
          });

          get().actions._updateRemovedCubes(puzzle);

          if (get().wordInfo?.status === "success") {
            const puzzleState = get().puzzles[puzzle.id];
            return {
              numPoints: puzzleState.numPoints,
              numRequiredWordsFound: puzzleState.requiredWordsFound.length,
              numBonusWordsFound: puzzleState.bonusWordsFound.length,
            };
          }

          return null;
        },

        _tryAddFaceToPath: (puzzle: Puzzle, face: PuzzleCubeFace) => {
          set((state) => {
            const removedCubes = state.puzzles[puzzle.id].removedCubes;
            if (getCubesBlockingFace(puzzle, face, removedCubes).length === 0) {
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
            const presentCubes = puzzle.cubes.filter((cube) => !removedCubes.includes(cube.id));

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

function getCubesBlockingFace(
  puzzle: Puzzle,
  face: PuzzleCubeFace,
  removedCubes: number[],
): PuzzleCube[] {
  const allCubes = puzzle.cubes.filter((cube) => !removedCubes.includes(cube.id));
  const cubesBlockingFace = new Set<PuzzleCube>();

  function getCubesBlockingCoordinate(x: number, y: number, z: number) {
    for (let i = 0; isCoordinateWithinPuzzleBounds(puzzle, x + i, y + i, z + i); i++) {
      const cube = allCubes.find(
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

function getNumberOfPointsForWord(word: string): number {
  return word.length;
}

export const usePuzzleCurrentWord = () => usePuzzleStore((state) => state.currentWord);

export const usePuzzleCurrentPath = () => usePuzzleStore((state) => state.currentPath);

export const usePuzzleWordInfo = () => usePuzzleStore((state) => state.wordInfo);

export const usePuzzleRequiredWordsFound = (puzzleId: number) =>
  usePuzzleStore(useShallow((state) => state.puzzles[puzzleId]?.requiredWordsFound ?? []));

export const usePuzzleBonusWordsFound = (puzzleId: number) =>
  usePuzzleStore(useShallow((state) => state.puzzles[puzzleId]?.bonusWordsFound ?? []));

export const usePuzzleRemovedCubes = (puzzleId: number) =>
  usePuzzleStore(useShallow((state) => state.puzzles[puzzleId]?.removedCubes ?? []));

export const usePuzzleNumRemainingWordsIncludingFace = (puzzleId: number, faceId: number) =>
  usePuzzleStore((state) => state.puzzles[puzzleId]?.numRemainingWordsIncludingFace[faceId] ?? 0);

export const usePuzzleScore = (puzzleId: number) =>
  usePuzzleStore((state) => {
    const numPoints = state.puzzles[puzzleId]?.numPoints ?? 0;
    const maxNumPoints = state.puzzles[puzzleId]?.maxNumPoints ?? 0;
    const score = (numPoints / maxNumPoints) * 100;
    return Number.isFinite(score) ? score : 0;
  });

export const usePuzzleActions = () => usePuzzleStore((state) => state.actions);
