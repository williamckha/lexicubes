import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

export interface User {
  id: number;
  email?: string;
  name: string;
  provider: "google" | "apple" | "facebook" | "github";
}

async function fetchUser(): Promise<User> {
  const response = await fetch(`${import.meta.env.VITE_BACKEND_API_URL}/api/user`, {
    method: "GET",
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(`${response.status} ${response.statusText}: ${text}`);
  }

  return response.json();
}

async function deleteUser() {
  const response = await fetch(`${import.meta.env.VITE_BACKEND_API_URL}/api/user`, {
    method: "DELETE",
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(`${response.status} ${response.statusText}: ${text}`);
  }
}

async function logout() {
  const response = await fetch("/logout", {
    method: "POST",
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(`${response.status} ${response.statusText}: ${text}`);
  }
}

export function useUserQuery() {
  return useQuery({
    queryKey: ["user"],
    queryFn: fetchUser,
    staleTime: Infinity,
    retry: false,
    refetchOnWindowFocus: false,
  });
}

export function useDeleteUserMutation(onSuccess?: () => void) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: deleteUser,
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["user"] });
      onSuccess?.();
    },
  });
}

export function useLogoutMutation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: logout,
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["user"] });
    },
  });
}
