import { useEffect, useState } from "react";
import { Easing, type EasingFunction, Tween } from "~/lib/tween";

const DEFAULT_DURATION_MS = 1000;
const DEFAULT_EASING = Easing.Linear;

export interface UseAnimatedState<T> {
  durationMs?: number;
  easing?: EasingFunction;
  repeat?: boolean;
  onProgress: (progress: number) => T;
  onComplete?: () => void;
}

export function useAnimatedState<T>({
  durationMs = DEFAULT_DURATION_MS,
  easing = DEFAULT_EASING,
  repeat,
  onProgress,
  onComplete,
}: UseAnimatedState<T>) {
  const [state, setState] = useState(onProgress(0));

  useEffect(() => {
    const tween = new Tween({
      durationMs,
      easing,
      onProgress: (progress) => setState(onProgress(progress)),
      onComplete: () => {
        onComplete?.();
        if (repeat) {
          tween.start();
        }
      },
    });

    tween.start();
    return () => tween.stop();
  }, []);

  return state;
}
