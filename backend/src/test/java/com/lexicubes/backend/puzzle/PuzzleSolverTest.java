package com.lexicubes.backend.puzzle;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PuzzleSolverTest {

    private final PuzzleSolver puzzleSolver = new PuzzleSolver();

    @Test
    public void emptyPuzzle_shouldHaveNoSolutions() {
        final Puzzle puzzle = Puzzle.of(Collections.emptyList());
        final List<PuzzleSolver.PuzzleSolution> solutions = puzzleSolver.solve(puzzle);
        assertTrue(solutions.isEmpty());
    }

    @Test
    public void puzzleWithOneSpellableWord_shouldHaveOneSolution() {
        final Puzzle.Cube cube0 = new Puzzle.Cube(0, 0, 0, 0, 'e', 'x', 'x');
        final Puzzle.Cube cube1 = new Puzzle.Cube(1, 0, 0, 1, 'u', 's', 'q');
        final Puzzle.Cube cube2 = new Puzzle.Cube(2, 1, 0, 0, 'e', 'z', 'e');
        final Puzzle puzzle = Puzzle.of(List.of(cube0, cube1, cube2));

        final List<PuzzleSolver.PuzzleSolution> solutions = puzzleSolver.solve(puzzle);

        assertEquals(1, solutions.size());
        assertEquals("squeeze", solutions.getFirst().word());
        assertFalse(solutions.getFirst().isBonus());

        final Set<Puzzle.Cube.Face> expectedFaces = Set.of(
                cube0.getTopFace(),
                cube1.getTopFace(),
                cube1.getLeftFace(),
                cube1.getRightFace(),
                cube2.getTopFace(),
                cube2.getLeftFace(),
                cube2.getRightFace());

        assertEquals(expectedFaces, solutions.getFirst().faces());
    }

    @Test
    public void puzzleWithMultiplePathsToSpellWord_shouldIncludeAllEligibleFacesToSpellWordInSolution() {
        final Puzzle.Cube cube0 = new Puzzle.Cube(0, 0, 0, 0, 'l', 'x', 'a');
        final Puzzle.Cube cube2 = new Puzzle.Cube(1, 0, 0, 1, 'p', 'x', 'y');
        final Puzzle.Cube cube1 = new Puzzle.Cube(2, 0, 0, 2, 'l', 'x', 'a');
        final Puzzle puzzle = Puzzle.of(List.of(cube0, cube1, cube2));

        final List<PuzzleSolver.PuzzleSolution> solutions = puzzleSolver.solve(puzzle);

        final PuzzleSolver.PuzzleSolution playSolution = solutions.stream()
                .filter(sol -> sol.word().equals("play")).findFirst().orElse(null);
        assertNotNull(playSolution);

        final Set<Puzzle.Cube.Face> expectedFaces = Set.of(
                cube0.getTopFace(),
                cube0.getRightFace(),
                cube1.getTopFace(),
                cube1.getRightFace(),
                cube2.getTopFace(),
                cube2.getRightFace());

        assertEquals(expectedFaces, playSolution.faces());
    }

    @Test
    public void puzzleWithCubesBlockingFaces_shouldNotHaveSolutionsIncludingBlockedFaces() {
        final Puzzle puzzle = Puzzle.of(List.of(
                new Puzzle.Cube(0, 0, 0, 0, 'i', 'j', 'a'),
                new Puzzle.Cube(1, 0, 0, 1, 'l', 'x', 'x'),
                new Puzzle.Cube(2, 1, 0, 0, 'd', 'x', 'e'),
                new Puzzle.Cube(3, 0, 1, 0, 'a', 'x', 'e')
        ));

        final List<PuzzleSolver.PuzzleSolution> solutions = puzzleSolver.solve(puzzle);

        assertTrue(solutions.stream().noneMatch(sol -> sol.word().equals("jail")),
                "Cube 1 is blocking the 'j' face on cube 0, so it should be impossible to spell 'jail'");
        assertTrue(solutions.stream().noneMatch(sol -> sol.word().equals("aide")),
                "Cube 2 is blocking the 'a' face on cube 0, so it should be impossible to spell 'aide'");
        assertTrue(solutions.stream().noneMatch(sol -> sol.word().equals("idea")),
                "Cube 3 is blocking the 'i' face on cube 0, so it should be impossible to spell 'idea'");
    }

    @Test
    public void puzzleWithNonNeighbouringFacesThatSpellWord_shouldExcludeSolutionsWithNonContinuousPaths() {
        final Puzzle puzzle = Puzzle.of(List.of(
                new Puzzle.Cube(0, 0, 0, 0, 'x', 'x', 'x'),
                new Puzzle.Cube(1, 0, 0, 1, 'f', 'i', 'g'),
                new Puzzle.Cube(2, 1, 0, 0, 't', 'x', 'h'),
                new Puzzle.Cube(3, 0, 1, 0, 'x', 'x', 'x')
        ));

        final List<PuzzleSolver.PuzzleSolution> solutions = puzzleSolver.solve(puzzle);

        assertTrue(solutions.stream().noneMatch(sol -> sol.word().equals("fight")),
                "'g' face does not neighbour 'h' face, so it should be impossible to spell 'fight'");
    }

    @Test
    public void puzzleWithLettersThatSpellWord_shouldExcludeSolutionsThatRepeatFaces() {
        final Puzzle puzzle = Puzzle.of(List.of(
                new Puzzle.Cube(0, 0, 0, 0, 'c', 'e', 'd')
        ));

        final List<PuzzleSolver.PuzzleSolution> solutions = puzzleSolver.solve(puzzle);

        assertTrue(solutions.stream().noneMatch(sol -> sol.word().equals("cede")),
                "Spelling 'cede' requires using the 'e' face twice, which is not allowed");
    }

    @Test
    public void solutionRequiringFaceBlockedByCubeUsedInOtherSolutionAndViceVersa_shouldBeBonus() {
        final Puzzle puzzle = Puzzle.of(List.of(
                new Puzzle.Cube(0, 0, 0, 0, 'e', 'k', 'k'),
                new Puzzle.Cube(1, 0, 0, 1, 'x', 'b', 'a'),
                new Puzzle.Cube(2, 1, 0, 0, 'x', 'a', 'r')
        ));

        final List<PuzzleSolver.PuzzleSolution> solutions = puzzleSolver.solve(puzzle);

        final PuzzleSolver.PuzzleSolution bakeSolution = solutions.stream()
                .filter(sol -> sol.word().equals("bake")).findFirst().orElse(null);
        final PuzzleSolver.PuzzleSolution rakeSolution = solutions.stream()
                .filter(sol -> sol.word().equals("rake")).findFirst().orElse(null);
        assertNotNull(bakeSolution);
        assertNotNull(rakeSolution);

        assertTrue(bakeSolution.isBonus());
        assertTrue(rakeSolution.isBonus());
    }

    @Test
    public void solutionWithBonusAndNonBonusPath_shouldBeNonBonus() {
        final Puzzle puzzle = Puzzle.of(List.of(
                new Puzzle.Cube(0, 0, 0, 0, 'e', 'k', 'k'),
                new Puzzle.Cube(1, 0, 0, 1, 'x', 'b', 'a'),
                new Puzzle.Cube(2, 1, 0, 0, 'x', 'a', 'r'),
                new Puzzle.Cube(3, 0, 1, 1, 'e', 'a', 'k')
        ));

        final List<PuzzleSolver.PuzzleSolution> solutions = puzzleSolver.solve(puzzle);

        final PuzzleSolver.PuzzleSolution bakeSolution = solutions.stream()
                .filter(sol -> sol.word().equals("bake")).findFirst().orElse(null);
        final PuzzleSolver.PuzzleSolution rakeSolution = solutions.stream()
                .filter(sol -> sol.word().equals("rake")).findFirst().orElse(null);
        assertNotNull(bakeSolution);
        assertNotNull(rakeSolution);

        assertFalse(bakeSolution.isBonus());
        assertFalse(rakeSolution.isBonus());
    }
}
