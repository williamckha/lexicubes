import { Game } from "~/game/game";
import { type PuzzleQueryId, usePuzzleQuery } from "~/game/puzzle-queries";

interface GamePageProps {
  puzzleId: PuzzleQueryId;
}

export function GamePage({ puzzleId }: GamePageProps) {
  const { data: puzzle, error, isPending, isError } = usePuzzleQuery(puzzleId);

  if (isPending) {
    return <span>Loading...</span>;
  }

  if (isError) {
    return <span>Error: {error.message}</span>;
  }

  return (
    <main className="flex flex-1 justify-center items-stretch flex-col">
      <Game puzzle={puzzle} />
    </main>
  );
}
