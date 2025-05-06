import React, { useRef } from "react";
import { useAnimateProp } from "~/lib/use-animate-prop";
import { Cube } from "~/game/cube";

const CUBE_SIZE_PX = 40;

const WORD_LIST = ["MUSIC", "PUNK", "SICK", "PUNS", "EPIC"];

const SELECTION_ANIMATION_DURATION = 800;
const STILL_ANIMATION_DURATION = 1200;
const TOTAL_ANIMATION_DURATION = SELECTION_ANIMATION_DURATION + STILL_ANIMATION_DURATION;
const SELECTION_ANIMATION_PERCENTAGE = SELECTION_ANIMATION_DURATION / TOTAL_ANIMATION_DURATION;

export function GameplayExample() {
  const currentWordIndexRef = useRef(0);

  const isFaceHighlighted = useAnimateProp({
    durationMs: TOTAL_ANIMATION_DURATION,
    repeat: true,
    onProgress: (progress) => {
      const isFaceHighlighted: { [key: string]: boolean } = {};
      const word = WORD_LIST[currentWordIndexRef.current];
      for (let i = 1; i <= word.length; i++) {
        const threshold = (i / word.length) * SELECTION_ANIMATION_PERCENTAGE;
        isFaceHighlighted[word.charAt(i - 1)] = progress >= threshold;
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
