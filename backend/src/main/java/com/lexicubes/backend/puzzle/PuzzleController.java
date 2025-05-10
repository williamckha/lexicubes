package com.lexicubes.backend.puzzle;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
public class PuzzleController {

    private final PuzzleRepository puzzleRepository;
    private final PuzzleGenerator puzzleGenerator;
    private final PuzzleSolver puzzleSolver;

    public PuzzleController(PuzzleRepository puzzleRepository,
                            PuzzleGenerator puzzleGenerator,
                            PuzzleSolver puzzleSolver) {

        this.puzzleRepository = puzzleRepository;
        this.puzzleGenerator = puzzleGenerator;
        this.puzzleSolver = puzzleSolver;
    }

    @GetMapping("/api/puzzles/{id}")
    public PuzzleResponse getPuzzle(@PathVariable Long id) {
        final Optional<Puzzle> puzzle = puzzleRepository.findById(id);
        if (puzzle.isEmpty()) {
            throw new PuzzleNotFoundException();
        }

        final List<PuzzleSolver.PuzzleSolution> solutions = puzzleSolver.solve(puzzle.get());

        return PuzzleMapper.toPuzzleResponse(puzzle.get(), solutions);
    }

    @GetMapping("/api/puzzles/daily/{date}")
    public PuzzleResponse getDailyPuzzle(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        final Optional<Puzzle> puzzle = puzzleRepository.findDailyByPublishedDate(date);
        if (puzzle.isEmpty()) {
            throw new PuzzleNotFoundException();
        }

        final List<PuzzleSolver.PuzzleSolution> solutions = puzzleSolver.solve(puzzle.get());

        return PuzzleMapper.toPuzzleResponse(puzzle.get(), solutions);
    }

    @PostMapping("/api/admin/puzzles/daily")
    public ResponseEntity<?> generateDailyPuzzle() {
        final Puzzle puzzle = puzzleGenerator.generateAndSaveDailyPuzzle();

        final URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/puzzles/{id}")
                .buildAndExpand(puzzle.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }
}
