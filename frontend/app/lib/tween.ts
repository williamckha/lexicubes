export class Tween {
  private readonly durationMs: number;
  private readonly repeat?: boolean;
  private readonly onProgress?: (progress: number) => void;
  private readonly onComplete?: () => void;

  private progress: number = 0;
  private endTime: number = 0;
  private complete: boolean = false;
  private animationFrame?: number;

  constructor(args: {
    durationMs: number;
    repeat?: boolean;
    onProgress?: (progress: number) => void;
    onComplete?: () => void;
  }) {
    this.durationMs = args.durationMs;
    this.repeat = args.repeat;
    this.onProgress = args.onProgress;
    this.onComplete = args.onComplete;
  }

  start() {
    this.stop();
    this.progress = 0;
    this.complete = false;
    this.endTime = Date.now() + this.durationMs * (1 - this.progress);
    this.update();
  }

  stop() {
    if (this.animationFrame) {
      cancelAnimationFrame(this.animationFrame);
      this.animationFrame = undefined;
    }
  }

  private update() {
    if (!this.complete) {
      this.progress = 1 - Math.max(0, Math.min(1, (this.endTime - Date.now()) / this.durationMs));

      if (this.progress === 1) {
        this.onComplete?.();
        if (this.repeat) {
          this.start();
        } else {
          this.complete = true;
        }
      } else {
        this.onProgress?.(this.progress);
        this.animationFrame = requestAnimationFrame(this.update.bind(this));
      }
    }
  }
}
