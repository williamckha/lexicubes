import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "~/components/ui/dialog";
import { VisuallyHidden } from "@radix-ui/react-visually-hidden";
import React, { useEffect } from "react";
import { useLeaderboardQuery } from "~/leaderboard/leaderboard-queries";
import { LoadingSpinner } from "~/components/ui/loading-spinner";
import { useUserQuery } from "~/user/user-queries";
import { useInView } from "react-intersection-observer";
import { Button } from "~/components/ui/button";

export function LeaderboardDialog({
  puzzleId,
  ...props
}: { puzzleId: number } & React.ComponentProps<typeof Dialog>) {
  return (
    <Dialog {...props}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Leaderboard</DialogTitle>
          <VisuallyHidden>
            <DialogDescription>Leaderboard for puzzle</DialogDescription>
          </VisuallyHidden>
        </DialogHeader>
        <LeaderboardTable puzzleId={puzzleId} />
      </DialogContent>
    </Dialog>
  );
}

function LeaderboardTable({ puzzleId }: { puzzleId: number }) {
  const {
    data: leaderboard,
    isPending,
    isError,
    isFetchingNextPage,
    hasNextPage,
    fetchNextPage,
  } = useLeaderboardQuery(puzzleId);

  const { data: user } = useUserQuery();

  const { ref: inViewRef, inView } = useInView();

  useEffect(() => {
    (async () => {
      if (inView && hasNextPage) {
        await fetchNextPage();
      }
    })();
  }, [inView, hasNextPage, fetchNextPage]);

  if (isPending) {
    return (
      <div className="flex justify-center items-center m-8">
        <LoadingSpinner />
      </div>
    );
  }

  if (isError) {
    return null;
  }

  const isLeaderboardEmpty =
    leaderboard.pages.length === 0 || leaderboard.pages[0].totalPageCount === 0;

  return (
    <>
      <table className="w-full border-2 *:text-sm *:sm:text-base">
        <thead className="bg-muted">
          <tr className="border-b-1 *:py-2 *:px-2 sm:*:px-3">
            <th className="!pr-0" />
            <th className="font-semibold text-left">Name</th>
            <th className="font-semibold text-right">Score</th>
            <th className="font-semibold text-right">Words</th>
            <th className="font-semibold text-right">Bonus</th>
          </tr>
        </thead>
        <tbody>
          {leaderboard.pages.map((page) => (
            <React.Fragment key={page.pageNumber}>
              {page.items.map((item, index) => {
                const position = page.pageSize * page.pageNumber + index + 1;
                const rowColor = user?.id === item.userId ? "bg-accent-secondary/10" : "";
                return (
                  <tr key={item.userId} className={`border-b-1 *:py-2 *:px-2 sm:*:px-3 ${rowColor}`}>
                    <td className="!pr-0 font-semibold text-right">{position}.</td>
                    <td className="w-full max-w-0 text-left overflow-hidden text-ellipsis text-nowrap">
                      {item.userName}
                    </td>
                    <td className="text-right">{item.numPoints}</td>
                    <td className="text-right">{item.numRequiredWordsFound}</td>
                    <td className="text-right">{item.numBonusWordsFound}</td>
                  </tr>
                );
              })}
            </React.Fragment>
          ))}
          {isLeaderboardEmpty && (
            <tr>
              <td colSpan={5} className="text-center p-4">
                No leaderboard entries
              </td>
            </tr>
          )}
        </tbody>
      </table>
      <Button
        ref={inViewRef}
        onClick={() => fetchNextPage()}
        disabled={isFetchingNextPage || !hasNextPage}
        hidden={!hasNextPage}
      >
        {isFetchingNextPage ? "Loading..." : "Load next page"}
      </Button>
    </>
  );
}
