import {
  usePuzzleActions,
  usePuzzleCurrentPath,
  usePuzzleNumRemainingWordsIncludingFace,
  usePuzzleRemovedCubes,
  usePuzzleScore,
} from "~/game/puzzle-store";
import { createRef, type MouseEvent, type Ref, type TouchEvent, useEffect, useState } from "react";
import { useWindowSize } from "@uidotdev/usehooks";
import { CSSTransition, TransitionGroup } from "react-transition-group";
import type { Puzzle, PuzzleCube, PuzzleCubeFace } from "~/game/puzzle-queries";
import { PERK_SCORES } from "~/game/game-constants";

interface CubesProps {
  puzzle: Puzzle;
}

export function Cubes({ puzzle }: CubesProps) {
  const removedCubes = usePuzzleRemovedCubes(puzzle.id);
  const { submitPath } = usePuzzleActions();

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
      submitPath(puzzle);
    }
  };

  useEffect(() => {
    window.addEventListener("mouseup", handleMouseUp);
    return () => window.removeEventListener("mouseup", handleMouseUp);
  }, []);

  const windowSize = useWindowSize();
  const windowWidth = windowSize.width ?? 0;
  const windowHeight = windowSize.height ?? 0;

  if (windowWidth === 0 || windowHeight === 0) {
    return null;
  }

  const maxContainerWidth = Math.max(windowWidth - CUBES_CONTAINER_MARGIN_PX * 2, 0);
  const maxContainerHeight = Math.max(windowHeight - CUBES_CONTAINER_MARGIN_PX * 2 - 220, 0);

  const containerWidthFactor =
    Math.sqrt(3) * Math.max(puzzle.dimensions.lengthX, puzzle.dimensions.lengthZ);
  const containerHeightFactor =
    Math.max(puzzle.dimensions.lengthX, puzzle.dimensions.lengthZ) + puzzle.dimensions.lengthY;

  const maxCubeWidth = maxContainerWidth / containerWidthFactor;
  const maxCubeHeight = maxContainerHeight / containerHeightFactor;
  const cubeSize = Math.min(Math.min(maxCubeWidth, maxCubeHeight), CUBE_MAX_SIZE_PX);

  const containerWidth = cubeSize * containerWidthFactor;
  const containerHeight = cubeSize * containerHeightFactor;

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
              <Cube ref={cube.nodeRef} puzzle={puzzle} cube={cube} size={cubeSize} />
            </CSSTransition>
          ))}
      </TransitionGroup>
    </div>
  );
}

interface CubeProps {
  puzzle: Puzzle;
  cube: PuzzleCube;
  size: number;
  ref?: Ref<HTMLDivElement>;
}

function Cube({ puzzle, cube, size, ref }: CubeProps) {
  const tileWidth = ((size - CUBE_BORDER_WIDTH_PX) * Math.sqrt(3)) / 2;
  const tileHeight = (size - CUBE_BORDER_WIDTH_PX) / 2;

  const top = cube.x * tileHeight + cube.z * tileHeight - cube.y * tileHeight * 2;
  const left = cube.x * tileWidth - cube.z * tileWidth;
  const zIndex = cube.x + cube.y + cube.z;

  return (
    <div ref={ref} className="relative" style={{ top: top, left: left, zIndex: zIndex }}>
      <CubeFace puzzle={puzzle} face={cube.topFace} orientation="top" size={size} />
      <CubeFace puzzle={puzzle} face={cube.leftFace} orientation="left" size={size} />
      <CubeFace puzzle={puzzle} face={cube.rightFace} orientation="right" size={size} />
    </div>
  );
}

type CubeFaceOrientation = "top" | "left" | "right";

interface CubeFaceProps {
  puzzle: Puzzle;
  face: PuzzleCubeFace;
  orientation: CubeFaceOrientation;
  size: number;
}

function CubeFace({ puzzle, face, orientation, size }: CubeFaceProps) {
  const currentPath = usePuzzleCurrentPath();
  const numRemainingWordsIncludingFace = usePuzzleNumRemainingWordsIncludingFace(
    puzzle.id,
    face.id,
  );
  const puzzleScore = usePuzzleScore(puzzle.id);
  const { startPath, continuePath } = usePuzzleActions();

  const handleMouseDown = (event: MouseEvent) => {
    if (event.button === 0) {
      startPath(puzzle, face);
    }
  };

  const handleMouseMove = (event: MouseEvent) => {
    continuePath(puzzle, face);
  };

  const handleTouchStart = (event: TouchEvent) => {
    const touch = event.changedTouches[0];
    const simulatedEvent = new MouseEvent("mousedown", {
      bubbles: true,
      cancelable: true,
      view: window,
      detail: 1,
      screenX: touch.screenX,
      screenY: touch.screenY,
      clientX: touch.clientX,
      clientY: touch.clientY,
      ctrlKey: false,
      altKey: false,
      shiftKey: false,
      metaKey: false,
      button: 0,
      relatedTarget: null,
    });
    touch.target.dispatchEvent(simulatedEvent);
  };

  const handleTouchMove = (event: TouchEvent) => {
    const touch = event.changedTouches[0];
    const simulatedEvent = new MouseEvent("mousemove", {
      bubbles: true,
      cancelable: true,
      view: window,
      detail: 1,
      screenX: touch.screenX,
      screenY: touch.screenY,
      clientX: touch.clientX,
      clientY: touch.clientY,
      ctrlKey: false,
      altKey: false,
      shiftKey: false,
      metaKey: false,
      button: 0,
      relatedTarget: null,
    });
    const element = document.elementFromPoint(touch.clientX, touch.clientY);
    if (element) {
      element.dispatchEvent(simulatedEvent);
    }
  };

  const handleTouchEnd = (event: TouchEvent) => {
    const touch = event.changedTouches[0];
    const simulatedEvent = new MouseEvent("mouseup", {
      bubbles: true,
      cancelable: true,
      view: window,
      detail: 1,
      screenX: touch.screenX,
      screenY: touch.screenY,
      clientX: touch.clientX,
      clientY: touch.clientY,
      ctrlKey: false,
      altKey: false,
      shiftKey: false,
      metaKey: false,
      button: 0,
      relatedTarget: null,
    });
    window.dispatchEvent(simulatedEvent);
  };

  const faceStyle = currentPath.some((f) => f.id === face.id)
    ? "bg-accent-secondary"
    : "bg-card transition";

  const hitboxMargin = size * 0.2;

  return (
    <div
      className={`select-none flex items-stretch
                  border-l-1 border-t-1 border-r-2 border-b-2 box-border
                  absolute origin-[0_0]
                  ${faceStyle}
                  ${CUBE_FACE_CONSTANTS.TRANSFORM[orientation]}
                  ${CUBE_FACE_CONSTANTS.BRIGHTNESS[orientation]}`}
      style={{ width: size, height: size }}
      onMouseDown={handleMouseDown}
      onTouchStart={handleTouchStart}
      onTouchEnd={handleTouchEnd}
    >
      <div
        className="flex flex-1 items-stretch"
        style={{ margin: hitboxMargin }}
        onMouseMove={handleMouseMove}
        onTouchMove={handleTouchMove}
      >
        <div
          className={`flex flex-1 flex-col justify-center items-center relative
                      ${CUBE_FACE_CONSTANTS.INNER_ROTATION[orientation]}`}
        >
          <span className="flex-1 text-center font-bold" style={{ fontSize: size / 2 }}>
            {face.letter.toUpperCase()}
          </span>
          {puzzleScore >= PERK_SCORES.SHOW_NUM_REMAINING_WORDS_INCLUDING_FACE && (
            <span
              className={`absolute -bottom-2 text-xs font-semibold opacity-50
                        ${CUBE_FACE_CONSTANTS.HINT_POSITION[orientation]}`}
              style={{ margin: -hitboxMargin, fontSize: size / 6 }}
            >
              {numRemainingWordsIncludingFace}
            </span>
          )}
        </div>
      </div>
    </div>
  );
}

const CUBE_FACE_CONSTANTS = {
  TRANSFORM: {
    top: "transform-[rotate(210deg)_skewX(-30deg)_scaleY(0.864)]",
    left: "transform-[rotate(90deg)_skewX(-30deg)_scaleY(0.864)]",
    right: "transform-[rotate(-30deg)_skewX(-30deg)_scaleY(0.864)]",
  },
  INNER_ROTATION: {
    top: "rotate-135",
    left: "-rotate-90",
    right: "rotate-none",
  },
  BRIGHTNESS: {
    top: "brightness-100",
    left: "brightness-80",
    right: "brightness-90",
  },
  HINT_POSITION: {
    top: "-bottom-2",
    left: "bottom-0 left-1",
    right: "bottom-0 right-1",
  },
};

const CUBE_MAX_SIZE_PX = 60;
const CUBE_BORDER_WIDTH_PX = 2;
const CUBES_CONTAINER_MARGIN_PX = 24;
