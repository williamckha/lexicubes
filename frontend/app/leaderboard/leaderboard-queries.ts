import { useInfiniteQuery } from "@tanstack/react-query";

interface LeaderboardEntry {
  userId: number;
  userName: string;
  numPoints: number;
  numRequiredWordsFound: number;
  numBonusWordsFound: number;
}

interface Page<T> {
  items: T[];
  pageNumber: number;
  pageSize: number;
  totalPageCount: number;
  firstPage: boolean;
  lastPage: boolean;
}

async function fetchLeaderboard(
  puzzleId: number,
  page: number = 0,
): Promise<Page<LeaderboardEntry>> {
  const response = await fetch(
    `${import.meta.env.VITE_BACKEND_API_URL}/api/puzzles/${puzzleId}/leaderboard?page=${page}`,
    {
      method: "GET",
    },
  );

  if (!response.ok) {
    const text = await response.text();
    throw new Error(`${response.status} ${response.statusText}: ${text}`);
  }

  return response.json();
}

export function useLeaderboardQuery(puzzleId: number) {
  return useInfiniteQuery({
    queryKey: ["leaderboard"],
    queryFn: ({ pageParam }) => fetchLeaderboard(puzzleId, pageParam),
    initialPageParam: 0,
    getPreviousPageParam: (firstPage) => (firstPage.firstPage ? null : firstPage.pageNumber - 1),
    getNextPageParam: (lastPage) => (lastPage.lastPage ? null : lastPage.pageNumber + 1),
    refetchOnWindowFocus: false,
  });
}
