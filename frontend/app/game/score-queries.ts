import { type MutationFunction, useMutation, type UseMutationOptions } from "@tanstack/react-query";
import { useRef } from "react";
import { useUserQuery } from "~/user/user-queries";

const SUBMIT_SCORE_DELAY_MS = 30000;

interface SubmitScoreRequest {
  puzzleId: number;
  numPoints: number;
  numRequiredWordsFound: number;
  numBonusWordsFound: number;
}

async function submitScore(request: SubmitScoreRequest) {
  const { puzzleId, ...requestBody } = request;

  const response = await fetch(`/api/puzzles/${puzzleId}/score`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(requestBody),
  });

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

interface UseDebouncedMutationOptions<TData, TError, TVariables, TContext>
  extends UseMutationOptions<TData, TError, TVariables, TContext> {
  delayMs: number;
  mutationFn: MutationFunction<TData, TVariables>;
}

function useDebouncedMutation<TData, TError, TVariables, TContext>({
  delayMs,
  mutationFn: mutationFnBase,
  onMutate: onMutateBase,
  ...options
}: UseDebouncedMutationOptions<TData, TError, TVariables, TContext>) {
  const abortControllerRef = useRef<AbortController>(null);

  const mutationFn = async (input: TVariables) => {
    abortControllerRef.current = new AbortController();
    await sleep(delayMs, abortControllerRef.current?.signal);
    return mutationFnBase(input);
  };

  const onMutate = async (variables: TVariables) => {
    abortControllerRef.current?.abort();
    return onMutateBase?.(variables) as Promise<TContext>;
  };

  return useMutation({
    mutationFn,
    onMutate,
    ...options,
  });
}

async function sleep(ms: number, signal?: AbortSignal) {
  return new Promise((resolve, reject) => {
    const timeoutId = setTimeout(resolve, ms);
    signal?.addEventListener("abort", (e) => {
      window.clearTimeout(timeoutId);
      reject(new DOMException("Sleep aborted", "AbortError"));
    });
  });
}
