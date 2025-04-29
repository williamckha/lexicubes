package com.lexicubes.backend.score;

import com.lexicubes.backend.user.AuthenticatedUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class ScoreController {

    public record SubmitScoreRequestBody(int numPoints,
                                         int numRequiredWordsFound,
                                         int numBonusWordsFound) {}

    private final ScoreService scoreService;

    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @GetMapping("/api/puzzles/{puzzleId}/score")
    public ScoreResponse getScoreForPuzzle(@PathVariable Long puzzleId,
                                           @AuthenticationPrincipal AuthenticatedUserDetails principal) {

        return scoreService.getScoreForPuzzleAndUser(puzzleId, principal.getId());
    }

    @PutMapping("/api/puzzles/{puzzleId}/score")
    public void submitScoreForPuzzle(@PathVariable Long puzzleId,
                                     @RequestBody SubmitScoreRequestBody requestBody,
                                     @AuthenticationPrincipal AuthenticatedUserDetails principal) {

        scoreService.submitScore(new ScoreService.SubmitScoreRequest(
                puzzleId,
                principal.getId(),
                requestBody.numPoints(),
                requestBody.numRequiredWordsFound(),
                requestBody.numBonusWordsFound()
        ));
    }
}
