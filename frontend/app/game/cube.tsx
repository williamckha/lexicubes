import React, { type MouseEvent, type Ref, type TouchEvent } from "react";

const CUBE_BORDER_WIDTH_PX = 2;

const CUBE_FACE_TRANSFORM = {
  top: "transform-[rotate(210deg)_skewX(-30deg)_scaleY(0.864)]",
  left: "transform-[rotate(90deg)_skewX(-30deg)_scaleY(0.864)]",
  right: "transform-[rotate(-30deg)_skewX(-30deg)_scaleY(0.864)]",
};

const CUBE_FACE_INNER_ROTATION = {
  top: "rotate-135",
  left: "-rotate-90",
  right: "rotate-none",
};

const CUBE_FACE_BRIGHTNESS = {
  top: "brightness-100",
  left: "brightness-80",
  right: "brightness-90",
};

const CUBE_FACE_HINT_POSITION = {
  top: "-bottom-2",
  left: "bottom-0 left-1",
  right: "bottom-0 right-1",
};

export interface CubeProps {
  ref?: Ref<HTMLDivElement>;

  topFaceLetter: string;
  leftFaceLetter: string;
  rightFaceLetter: string;

  x: number;
  y: number;
  z: number;

  size: number;

  isTopFaceHighlighted?: boolean;
  isLeftFaceHighlighted?: boolean;
  isRightFaceHighlighted?: boolean;

  topFaceHint?: string;
  leftFaceHint?: string;
  rightFaceHint?: string;

  showHints?: boolean;

  onTopFaceMouseDown?: () => void;
  onLeftFaceMouseDown?: () => void;
  onRightFaceMouseDown?: () => void;

  onTopFaceMouseMove?: () => void;
  onLeftFaceMouseMove?: () => void;
  onRightFaceMouseMove?: () => void;
}

export function Cube({
  ref,
  topFaceLetter,
  leftFaceLetter,
  rightFaceLetter,
  x,
  y,
  z,
  size,
  ...props
}: CubeProps) {
  const tileWidth = ((size - CUBE_BORDER_WIDTH_PX) * Math.sqrt(3)) / 2;
  const tileHeight = (size - CUBE_BORDER_WIDTH_PX) / 2;

  const top = x * tileHeight + z * tileHeight - y * tileHeight * 2;
  const left = x * tileWidth - z * tileWidth;
  const zIndex = x + y + z;

  return (
    <div ref={ref} className="relative" style={{ top: top, left: left, zIndex: zIndex }}>
      <CubeFace
        letter={topFaceLetter}
        orientation="top"
        size={size}
        isHighlighted={props.isTopFaceHighlighted}
        hint={props.topFaceHint}
        showHint={props.showHints}
        onMouseDown={props.onTopFaceMouseDown}
        onMouseMove={props.onTopFaceMouseMove}
      />
      <CubeFace
        letter={leftFaceLetter}
        orientation="left"
        size={size}
        isHighlighted={props.isLeftFaceHighlighted}
        hint={props.leftFaceHint}
        showHint={props.showHints}
        onMouseDown={props.onLeftFaceMouseDown}
        onMouseMove={props.onLeftFaceMouseMove}
      />
      <CubeFace
        letter={rightFaceLetter}
        orientation="right"
        size={size}
        isHighlighted={props.isRightFaceHighlighted}
        hint={props.rightFaceHint}
        showHint={props.showHints}
        onMouseDown={props.onRightFaceMouseDown}
        onMouseMove={props.onRightFaceMouseMove}
      />
    </div>
  );
}

interface CubeFaceProps {
  letter: string;
  orientation: "top" | "left" | "right";
  size: number;
  isHighlighted?: boolean;
  hint?: string;
  showHint?: boolean;
  onMouseDown?: () => void;
  onMouseMove?: () => void;
}

function CubeFace({
  letter,
  orientation,
  size,
  isHighlighted,
  hint,
  showHint,
  onMouseDown,
  onMouseMove,
}: CubeFaceProps) {
  const handleMouseDown = (event: MouseEvent) => {
    if (event.button === 0) {
      onMouseDown?.();
    }
  };

  const handleMouseMove = (event: MouseEvent) => {
    if (event.button === 0) {
      onMouseMove?.();
    }
  };

  const handleTouchStart = (event: TouchEvent) => {
    const touch = event.changedTouches[0];
    const simulatedEvent = createSimulatedMouseEvent("mousedown", touch);
    touch.target.dispatchEvent(simulatedEvent);
  };

  const handleTouchMove = (event: TouchEvent) => {
    const touch = event.changedTouches[0];
    const simulatedEvent = createSimulatedMouseEvent("mousemove", touch);
    const element = document.elementFromPoint(touch.clientX, touch.clientY);
    if (element) {
      element.dispatchEvent(simulatedEvent);
    }
  };

  const handleTouchEnd = (event: TouchEvent) => {
    const touch = event.changedTouches[0];
    const simulatedEvent = createSimulatedMouseEvent("mouseup", touch);
    window.dispatchEvent(simulatedEvent);
  };

  const hitboxMargin = size * 0.2;
  const letterFontSize = size / 2;
  const hintFontSize = size / 6;

  return (
    <div
      className={`select-none flex items-stretch
                  border-l-1 border-t-1 border-r-2 border-b-2 box-border
                  absolute origin-[0_0]
                  ${isHighlighted ? "bg-accent-secondary" : "bg-card transition"}
                  ${CUBE_FACE_TRANSFORM[orientation]}
                  ${CUBE_FACE_BRIGHTNESS[orientation]}`}
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
                      ${CUBE_FACE_INNER_ROTATION[orientation]}`}
        >
          <span className="flex-1 text-center font-bold" style={{ fontSize: letterFontSize }}>
            {letter.toUpperCase()}
          </span>
          {showHint && hint && (
            <span
              className={`absolute -bottom-2 text-xs font-semibold opacity-50
                          ${CUBE_FACE_HINT_POSITION[orientation]}`}
              style={{ margin: -hitboxMargin, fontSize: hintFontSize }}
            >
              {hint}
            </span>
          )}
        </div>
      </div>
    </div>
  );
}

function createSimulatedMouseEvent(eventType: string, touch: React.Touch) {
  return new MouseEvent(eventType, {
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
}
