package com.lexicubes.backend.statistics;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/api/puzzles/{puzzleId}/stats")
    public GlobalStatistics getGlobalStatisticsForPuzzle(@PathVariable Long puzzleId) {
        return statisticsService.getGlobalStatistics(puzzleId);
    }
}
