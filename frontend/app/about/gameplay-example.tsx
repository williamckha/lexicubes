import React, { useRef } from "react";
import { useAnimatedState } from "~/lib/use-animated-state";
import { Cube } from "~/game/cube";
import { Easing } from "~/lib/tween";

const CUBE_SIZE_PX = 40;

const WORD_LIST = ["MUSIC", "PUNK", "SICK", "PUNS", "EPIC"];

const SELECTION_ANIMATION_DURATION_MS = 800;
const STILL_ANIMATION_DURATION_MS = 1200;
const TOTAL_ANIMATION_DURATION_MS = SELECTION_ANIMATION_DURATION_MS + STILL_ANIMATION_DURATION_MS;
const SELECTION_ANIMATION_PERCENTAGE =
  SELECTION_ANIMATION_DURATION_MS / TOTAL_ANIMATION_DURATION_MS;

export function GameplayExample() {
  const currentWordIndexRef = useRef(0);

  const isFaceHighlighted = useAnimatedState({
    durationMs: TOTAL_ANIMATION_DURATION_MS,
    easing: Easing.Linear,
    repeat: true,
    onProgress: (value) => {
      const isFaceHighlighted: Record<string, boolean> = {};
      const word = WORD_LIST[currentWordIndexRef.current];
      for (let i = 1; i <= word.length; i++) {
        const threshold = (i / word.length) * SELECTION_ANIMATION_PERCENTAGE;
        isFaceHighlighted[word.charAt(i - 1)] = value >= threshold;
      }
      return isFaceHighlighted;
    },
    onComplete: () => {
      currentWordIndexRef.current = (currentWordIndexRef.current + 1) % WORD_LIST.length;
    },
  });

  return (
    <div className="flex justify-center items-center h-[160px] mt-[20px] [&_*]:transition">
      <Cube
        topFaceLetter={"E"}
        leftFaceLetter={"P"}
        rightFaceLetter={"S"}
        x={0}
        y={1}
        z={0}
        size={CUBE_SIZE_PX}
        isTopFaceHighlighted={isFaceHighlighted["E"] ?? false}
        isLeftFaceHighlighted={isFaceHighlighted["P"] ?? false}
        isRightFaceHighlighted={isFaceHighlighted["S"] ?? false}
      />
      <Cube
        topFaceLetter={"I"}
        leftFaceLetter={"K"}
        rightFaceLetter={"C"}
        x={1}
        y={0}
        z={0}
        size={CUBE_SIZE_PX}
        isTopFaceHighlighted={isFaceHighlighted["I"] ?? false}
        isLeftFaceHighlighted={isFaceHighlighted["K"] ?? false}
        isRightFaceHighlighted={isFaceHighlighted["C"] ?? false}
      />
      <Cube
        topFaceLetter={"N"}
        leftFaceLetter={"M"}
        rightFaceLetter={"U"}
        x={0}
        y={0}
        z={1}
        size={CUBE_SIZE_PX}
        isTopFaceHighlighted={isFaceHighlighted["N"] ?? false}
        isLeftFaceHighlighted={isFaceHighlighted["M"] ?? false}
        isRightFaceHighlighted={isFaceHighlighted["U"] ?? false}
      />
    </div>
  );
}
