import { useEffect, useState } from "react";
import { Tween } from "~/lib/tween";

const DEFAULT_DURATION_MS = 1000;

interface UseAnimatePropOptions<TProp> {
  durationMs?: number;
  repeat?: boolean;
  onProgress: (progress: number) => TProp;
  onComplete?: () => void;
}

export function useAnimateProp<TProp>({
  durationMs = DEFAULT_DURATION_MS,
  repeat,
  onProgress,
  onComplete,
}: UseAnimatePropOptions<TProp>) {
  const [propValue, setPropValue] = useState(onProgress(0));

  useEffect(() => {
    const tween = new Tween({
      durationMs,
      repeat,
      onProgress: (progress) => setPropValue(onProgress(progress)),
      onComplete,
    });

    tween.start();
    return () => tween.stop();
  }, []);

  return propValue;
}
