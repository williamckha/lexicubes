import type { Puzzle, PuzzleDimensions } from "~/game/puzzle-queries";
import {
  usePuzzleActions,
  usePuzzleCurrentPath,
  usePuzzleNumRemainingWordsIncludingFace,
  usePuzzleRemovedCubes,
  usePuzzleScore,
} from "~/game/puzzle-store";
import { useSubmitScoreMutation } from "~/game/score-queries";
import React, { createRef, useEffect, useState } from "react";
import { useWindowSize } from "@uidotdev/usehooks";
import { PERK_SCORES } from "~/game/game-constants";
import { CSSTransition, TransitionGroup } from "react-transition-group";
import { Cube } from "~/game/cube";

export interface PuzzleCubesProps {
  puzzle: Puzzle;
}

export function PuzzleCubes({ puzzle }: PuzzleCubesProps) {
  const currentPath = usePuzzleCurrentPath();
  const removedCubes = usePuzzleRemovedCubes(puzzle.id);
  const numRemainingWordsIncludingFace = usePuzzleNumRemainingWordsIncludingFace(puzzle.id);
  const { startPath, continuePath, submitPath } = usePuzzleActions();

  const [cubes] = useState(
    puzzle.cubes.map((cube) => {
      return {
        ...cube,
        nodeRef: createRef<HTMLDivElement | null>(),
      };
    }),
  );

  const handleMouseUp = (event: { button: number }) => {
    if (event.button === 0) {
      const puzzleStateChange = submitPath(puzzle);
      if (isSubmitScoreEnabled && puzzleStateChange) {
        submitScore({
          puzzleId: puzzle.id,
          ...puzzleStateChange,
        });
      }
    }
  };

  useEffect(() => {
    window.addEventListener("mouseup", handleMouseUp);
    return () => window.removeEventListener("mouseup", handleMouseUp);
  }, []);

  const windowSize = useWindowSize();
  const windowWidth = windowSize.width ?? 0;
  const windowHeight = windowSize.height ?? 0;

  const { cubeSize, containerWidth, containerHeight } = calculateCubeSize(
    windowWidth,
    windowHeight,
    puzzle.dimensions,
  );

  const puzzleScore = usePuzzleScore(puzzle.id);
  const { mutate: submitScore, isEnabled: isSubmitScoreEnabled } = useSubmitScoreMutation();
  const showHints = puzzleScore >= PERK_SCORES.SHOW_NUM_REMAINING_WORDS_INCLUDING_FACE;

  return (
    <div
      className="flex justify-center items-center"
      style={{ width: containerWidth, height: containerHeight }}
    >
      <TransitionGroup>
        {cubes
          .filter((cube) => !removedCubes.includes(cube.id))
          .map((cube) => (
            <CSSTransition
              key={cube.id}
              nodeRef={cube.nodeRef}
              timeout={500}
              classNames={{
                exit: "pointer-events-none touch-none",
                exitActive: "opacity-0 scale-0 transition-all duration-500 ease-in-out",
              }}
            >
              <Cube
                ref={cube.nodeRef}
                topFaceLetter={cube.topFace.letter}
                leftFaceLetter={cube.leftFace.letter}
                rightFaceLetter={cube.rightFace.letter}
                x={cube.x}
                y={cube.y}
                z={cube.z}
                size={cubeSize}
                isTopFaceHighlighted={currentPath.some((face) => face.id === cube.topFace.id)}
                isLeftFaceHighlighted={currentPath.some((face) => face.id === cube.leftFace.id)}
                isRightFaceHighlighted={currentPath.some((face) => face.id === cube.rightFace.id)}
                topFaceHint={numRemainingWordsIncludingFace[cube.topFace.id]?.toString()}
                leftFaceHint={numRemainingWordsIncludingFace[cube.leftFace.id]?.toString()}
                rightFaceHint={numRemainingWordsIncludingFace[cube.rightFace.id]?.toString()}
                showHints={showHints}
                onTopFaceMouseDown={() => startPath(puzzle, cube.topFace)}
                onLeftFaceMouseDown={() => startPath(puzzle, cube.leftFace)}
                onRightFaceMouseDown={() => startPath(puzzle, cube.rightFace)}
                onTopFaceMouseMove={() => continuePath(puzzle, cube.topFace)}
                onLeftFaceMouseMove={() => continuePath(puzzle, cube.leftFace)}
                onRightFaceMouseMove={() => continuePath(puzzle, cube.rightFace)}
              />
            </CSSTransition>
          ))}
      </TransitionGroup>
    </div>
  );
}

const CUBE_MAX_SIZE_PX = 60;
const CUBES_CONTAINER_MARGIN_X_PX = 48;
const CUBES_CONTAINER_MARGIN_Y_PX = 268;

function calculateCubeSize(
  windowWidth: number,
  windowHeight: number,
  puzzleDimensions: PuzzleDimensions,
) {
  if (windowWidth === 0 || windowHeight === 0) {
    return { cubeSize: 0, containerWidth: 0, containerHeight: 0 };
  }

  const maxContainerWidth = Math.max(windowWidth - CUBES_CONTAINER_MARGIN_X_PX, 0);
  const maxContainerHeight = Math.max(windowHeight - CUBES_CONTAINER_MARGIN_Y_PX, 0);

  const containerWidthFactor =
    Math.sqrt(3) * Math.max(puzzleDimensions.lengthX, puzzleDimensions.lengthZ);
  const containerHeightFactor =
    Math.max(puzzleDimensions.lengthX, puzzleDimensions.lengthZ) + puzzleDimensions.lengthY;

  const maxCubeWidth = maxContainerWidth / containerWidthFactor;
  const maxCubeHeight = maxContainerHeight / containerHeightFactor;

  const cubeSize = Math.min(maxCubeWidth, maxCubeHeight, CUBE_MAX_SIZE_PX);
  const containerWidth = cubeSize * containerWidthFactor;
  const containerHeight = cubeSize * containerHeightFactor;

  return { cubeSize, containerWidth, containerHeight };
}
