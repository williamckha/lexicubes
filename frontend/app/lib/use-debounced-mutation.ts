import { type MutationFunction, useMutation, type UseMutationOptions } from "@tanstack/react-query";
import { useRef } from "react";
import { sleep } from "~/lib/utils";

export interface UseDebouncedMutationOptions<TData, TError, TVariables, TContext>
  extends UseMutationOptions<TData, TError, TVariables, TContext> {
  delayMs: number;
  mutationFn: MutationFunction<TData, TVariables>;
}

export function useDebouncedMutation<TData, TError, TVariables, TContext>({
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
