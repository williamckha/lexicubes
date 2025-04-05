package com.lexicubes.backend.puzzle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class PuzzleGeneratorTest {

    public static void main(String[] args) throws JsonProcessingException {
        final PuzzleGenerator generator = new PuzzleGenerator(null);
        final Puzzle puzzle = generator.generatePuzzle(3);
        final PuzzleSolver puzzleSolver = new PuzzleSolver();
        final List<PuzzleSolver.PuzzleSolution> solution = puzzleSolver.solve(puzzle);
        final PuzzleResponse response = PuzzleMapper.toPuzzleResponse(puzzle, solution);
        final ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        final String json = mapper.writeValueAsString(response);
        System.out.println(json);
    }

}
