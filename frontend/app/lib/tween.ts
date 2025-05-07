export class Tween {
  private readonly durationMs: number;
  private readonly easing: EasingFunction;
  private readonly onProgress?: (value: number) => void;
  private readonly onComplete?: () => void;

  private progress: number = 0;
  private endTime: number = 0;
  private complete: boolean = false;
  private animationFrame?: number;

  public constructor(args: {
    durationMs: number;
    easing: EasingFunction;
    onProgress?: (progress: number) => void;
    onComplete?: () => void;
  }) {
    this.durationMs = args.durationMs;
    this.easing = args.easing;
    this.onProgress = args.onProgress;
    this.onComplete = args.onComplete;
  }

  public start() {
    this.stop();
    this.progress = 0;
    this.complete = false;
    this.endTime = Date.now() + this.durationMs * (1 - this.progress);
    this.update();
  }

  public stop() {
    if (this.animationFrame) {
      cancelAnimationFrame(this.animationFrame);
      this.animationFrame = undefined;
    }
  }

  private update() {
    if (!this.complete) {
      this.progress = 1 - Math.max(0, Math.min(1, (this.endTime - Date.now()) / this.durationMs));
      if (this.progress === 1) {
        this.complete = true;
        this.onComplete?.();
      } else {
        this.onProgress?.(this.easing(this.progress));
        this.animationFrame = requestAnimationFrame(this.update.bind(this));
      }
    }
  }
}

export type EasingFunction = (progress: number) => number;

export type EasingFunctionGroup = {
  In: EasingFunction;
  Out: EasingFunction;
  InOut: EasingFunction;
};

/**
 * Collection of optimized easing equations by Robert Penner
 * http://robertpenner.com/easing/
 */
export const Easing: {
  Linear: EasingFunction;
  Quadratic: EasingFunctionGroup;
  Cubic: EasingFunctionGroup;
} = Object.freeze({
  Linear: (progress) => progress,

  Quadratic: {
    In: (progress) => progress * progress,
    Out: (progress) => progress * (2 - progress),
    InOut: (progress) => {
      if ((progress *= 2) < 1) {
        return 0.5 * progress * progress;
      }
      return -0.5 * (--progress * (progress - 2) - 1);
    },
  },

  Cubic: {
    In: (progress) => progress * progress * progress,
    Out: (progress) => --progress * progress * progress + 1,
    InOut: (progress) => {
      if ((progress *= 2) < 1) {
        return 0.5 * progress * progress * progress;
      }
      return 0.5 * ((progress -= 2) * progress * progress + 2);
    },
  },
});
