package com.lexicubes.backend.leaderboard;

import com.lexicubes.backend.common.PageResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping("/api/puzzles/{puzzleId}/leaderboard")
    public PageResponse<LeaderboardEntry> getLeaderboardEntriesForPuzzle(
            @PathVariable Long puzzleId,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(name = "size", required = false, defaultValue = "50") Integer pageSize,
            @RequestParam(name = "sort", required = false) Optional<LeaderboardPageRequest.SortOption> sort) {

        return leaderboardService.getLeaderboardEntriesForPuzzle(puzzleId, new LeaderboardPageRequest(
                pageNumber,
                pageSize,
                sort.orElse(LeaderboardPageRequest.SortOption.NUM_POINTS)
        ));
    }
}
