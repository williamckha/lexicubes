package com.lexicubes.backend.puzzle;

import java.util.Comparator;
import java.util.List;

public class PuzzleMapper {

    public static PuzzleResponse toPuzzleResponse(Puzzle puzzle,
                                                  List<PuzzleSolver.PuzzleSolution> solutions) {

        final List<PuzzleResponse.Cube> puzzleResponseCubes = puzzle.getCubes().stream()
                .map(PuzzleMapper::toPuzzleResponseCube)
                .sorted(Comparator.comparingInt(PuzzleResponse.Cube::id))
                .toList();

        final PuzzleResponse.Dimensions puzzleDimensions = new PuzzleResponse.Dimensions(
                puzzle.getLengthX(),
                puzzle.getLengthY(),
                puzzle.getLengthZ());

        final List<PuzzleResponse.Solution> puzzleResponseSolutions = solutions.stream()
                .map(PuzzleMapper::toPuzzleResponseSolution)
                .sorted(Comparator.comparing(PuzzleResponse.Solution::word))
                .toList();

        return new PuzzleResponse(
                puzzle.getId(),
                puzzle.getPublishedDate(),
                puzzle.isDaily(),
                puzzleResponseCubes,
                puzzleDimensions,
                puzzleResponseSolutions);
    }

    public static PuzzleResponse.Cube toPuzzleResponseCube(Puzzle.Cube cube) {
        return new PuzzleResponse.Cube(
                cube.getId(),
                cube.getX(),
                cube.getY(),
                cube.getZ(),
                toPuzzleResponseCubeFace(cube.getTopFace()),
                toPuzzleResponseCubeFace(cube.getLeftFace()),
                toPuzzleResponseCubeFace(cube.getRightFace()));
    }

    public static PuzzleResponse.Cube.Face toPuzzleResponseCubeFace(Puzzle.Cube.Face face) {
        return new PuzzleResponse.Cube.Face(
                face.getId(),
                face.getLetter(),
                face.getNeighbours().stream().map(Puzzle.Cube.Face::getId).toList());
    }

    public static PuzzleResponse.Solution toPuzzleResponseSolution(PuzzleSolver.PuzzleSolution solution) {
        return new PuzzleResponse.Solution(
                solution.word(),
                solution.faces().stream().map(Puzzle.Cube.Face::getId).sorted().toList(),
                solution.isBonus());
    }
}
