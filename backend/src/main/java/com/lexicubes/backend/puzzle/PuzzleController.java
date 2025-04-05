package com.lexicubes.backend.puzzle;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
public class PuzzleController {

    private final PuzzleRepository puzzleRepository;
    private final PuzzleSolver puzzleSolver;

    public PuzzleController(PuzzleRepository puzzleRepository, PuzzleSolver puzzleSolver) {
        this.puzzleRepository = puzzleRepository;
        this.puzzleSolver = puzzleSolver;
    }

    @GetMapping("/puzzles/{id}")
    public PuzzleResponse getPuzzle(@PathVariable Long id) {
        final Optional<Puzzle> puzzle = puzzleRepository.findById(id);
        if (puzzle.isEmpty()) {
            throw new PuzzleNotFoundException();
        }

        final List<PuzzleSolver.PuzzleSolution> solutions = puzzleSolver.solve(puzzle.get());

        return PuzzleMapper.toPuzzleResponse(puzzle.get(), solutions);
    }

    @GetMapping("/puzzles/daily")
    public PuzzleResponse getDailyPuzzle() {
        final Optional<Puzzle> puzzle = puzzleRepository.findByPublishedDate(LocalDate.now());
        if (puzzle.isEmpty()) {
            throw new PuzzleNotFoundException();
        }

        final List<PuzzleSolver.PuzzleSolution> solutions = puzzleSolver.solve(puzzle.get());

        return PuzzleMapper.toPuzzleResponse(puzzle.get(), solutions);
    }
}
