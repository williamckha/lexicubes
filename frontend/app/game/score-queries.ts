import { useUserQuery } from "~/user/user-queries";
import { useDebouncedMutation } from "~/lib/use-debounced-mutation";

const SUBMIT_SCORE_DELAY_MS = 30000;

interface SubmitScoreRequest {
  puzzleId: number;
  numPoints: number;
  numRequiredWordsFound: number;
  numBonusWordsFound: number;
}

async function submitScore(request: SubmitScoreRequest) {
  const { puzzleId, ...requestBody } = request;

  const response = await fetch(
    `${import.meta.env.VITE_BACKEND_API_URL}/api/puzzles/${puzzleId}/score`,
    {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(requestBody),
    },
  );

  if (!response.ok) {
    const text = await response.text();
    throw new Error(`${response.status} ${response.statusText}: ${text}`);
  }
}

export function useSubmitScoreMutation() {
  const { data: user } = useUserQuery();

  const mutation = useDebouncedMutation({
    mutationKey: ["submitScore"],
    mutationFn: async (request: SubmitScoreRequest) => {
      if (user) {
        await submitScore(request);
      }
    },
    delayMs: SUBMIT_SCORE_DELAY_MS,
    retry: false,
  });

  return { ...mutation, isEnabled: !!user };
}
