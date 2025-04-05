package com.lexicubes.backend.puzzle;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PuzzleSolver {

    private static final String WORDS_LIST_RESOURCE_NAME = "wordlists\\common.txt";

    private final TrieNode wordTrie;

    private final List<Puzzle.Cube.Face> path;

    private final Multiset<Puzzle.Cube> unusableCubes;

    private final Map<String, List<List<Puzzle.Cube.Face>>> pathsForWord;

    private final Map<Puzzle.Cube, Set<String>> wordsIncludingCube;

    private final Map<Puzzle.Cube.Face, Set<Puzzle.Cube>> cubesBlockingFace;

    private final Set<String> dependentWords;

    public record PuzzleSolution(String word,
                                 Set<Puzzle.Cube.Face> faces,
                                 boolean isBonus) {}

    public PuzzleSolver() {
        wordTrie = new TrieNode();
        loadWordTrie();

        path = new ArrayList<>();
        unusableCubes = new Multiset<>();
        pathsForWord = new HashMap<>();
        wordsIncludingCube = new HashMap<>();
        cubesBlockingFace = new HashMap<>();
        dependentWords = new HashSet<>();
    }

    @Cacheable(cacheNames = "puzzleSolutions", key = "#puzzle.id")
    public List<PuzzleSolution> solve(Puzzle puzzle) {
        path.clear();
        unusableCubes.clear();
        pathsForWord.clear();
        wordsIncludingCube.clear();
        cubesBlockingFace.clear();
        dependentWords.clear();

        for (final Puzzle.Cube cube : puzzle.getCubes()) {
            backtrack(puzzle, cube.getTopFace(), wordTrie);
            backtrack(puzzle, cube.getLeftFace(), wordTrie);
            backtrack(puzzle, cube.getRightFace(), wordTrie);
        }

        final List<PuzzleSolution> solutions = new ArrayList<>();
        for (final Map.Entry<String, List<List<Puzzle.Cube.Face>>> entry : pathsForWord.entrySet()) {
            final String word = entry.getKey();
            final List<List<Puzzle.Cube.Face>> paths = entry.getValue();

            final boolean isBonus = !isWordPossible(word);
            final Set<Puzzle.Cube.Face> faces = paths.stream().flatMap(Collection::stream).collect(Collectors.toSet());
            solutions.add(new PuzzleSolution(word, faces, isBonus));
        }

        return solutions;
    }

    private void backtrack(Puzzle puzzle, Puzzle.Cube.Face face, TrieNode trieNode) {
        final TrieNode childTrieNode = trieNode.getChild(face.getLetter());
        if (childTrieNode == null) {
            return;
        }

        path.add(face);

        if (childTrieNode.isEnd()) {
            final String word = getPathAsString();
            final List<Puzzle.Cube.Face> pathCopy = List.copyOf(path);
            pathsForWord.computeIfAbsent(word, k -> new ArrayList<>()).add(pathCopy);

            for (final Puzzle.Cube.Face faceInPath : path) {
                wordsIncludingCube.computeIfAbsent(faceInPath.getCube(), k -> new HashSet<>()).add(word);
            }
        }

        // There may be cubes in front of the current face blocking its visibility.
        // Therefore, for the current path to be valid (i.e. using only faces visible to the player),
        // we must remove these cubes from consideration when searching for neighbouring faces.
        final Set<Puzzle.Cube> cubesBlockingFace = getCubesBlockingFace(puzzle, face);
        unusableCubes.addAll(cubesBlockingFace);

        for (final Puzzle.Cube.Face neighbour : face.getNeighbours()) {
            if (!unusableCubes.contains(neighbour.getCube()) && !path.contains(neighbour)) {
                backtrack(puzzle, neighbour, childTrieNode);
            }
        }

        unusableCubes.removeAll(cubesBlockingFace);

        path.removeLast();
    }

    private Set<Puzzle.Cube> getCubesBlockingFace(Puzzle puzzle, Puzzle.Cube.Face face) {
        if (!cubesBlockingFace.containsKey(face)) {
            final Puzzle.Cube cube = face.getCube();
            final Set<Puzzle.Cube> cubes = new HashSet<>();
            if (cube.getTopFace() == face) {
                getCubesRenderedAtCoordinate(puzzle, cubes, cube.getX(), cube.getY() + 1, cube.getZ());
                getCubesRenderedAtCoordinate(puzzle, cubes, cube.getX() + 1, cube.getY() + 1, cube.getZ());
                getCubesRenderedAtCoordinate(puzzle, cubes, cube.getX(), cube.getY() + 1, cube.getZ() + 1);
                getCubesRenderedAtCoordinate(puzzle, cubes, cube.getX() + 1, cube.getY() + 1, cube.getZ() + 1);
            } else if (cube.getLeftFace() == face) {
                getCubesRenderedAtCoordinate(puzzle, cubes, cube.getX(), cube.getY(), cube.getZ() + 1);
                getCubesRenderedAtCoordinate(puzzle, cubes, cube.getX() + 1, cube.getY(), cube.getZ() + 1);
                getCubesRenderedAtCoordinate(puzzle, cubes, cube.getX() + 1, cube.getY() + 1, cube.getZ() + 1);
                getCubesRenderedAtCoordinate(puzzle, cubes, cube.getX(), cube.getY() + 1, cube.getZ() + 1);
            } else if (cube.getRightFace() == face) {
                getCubesRenderedAtCoordinate(puzzle, cubes, cube.getX() + 1, cube.getY(), cube.getZ());
                getCubesRenderedAtCoordinate(puzzle, cubes, cube.getX() + 1, cube.getY(), cube.getZ() + 1);
                getCubesRenderedAtCoordinate(puzzle, cubes, cube.getX() + 1, cube.getY() + 1, cube.getZ() + 1);
                getCubesRenderedAtCoordinate(puzzle, cubes, cube.getX() + 1, cube.getY() + 1, cube.getZ());
            }
            cubesBlockingFace.put(face, Set.copyOf(cubes));
        }
        return cubesBlockingFace.get(face);
    }

    private void getCubesRenderedAtCoordinate(Puzzle puzzle, Set<Puzzle.Cube> cubes, int x, int y, int z) {
        for (int i = 0; puzzle.isWithinBounds(x + i, y + i, z + i); i++) {
            puzzle.getCubeAtCoordinate(x + i, y + i, z + i).ifPresent(cubes::add);
        }
    }

    private boolean isWordPossible(String word) {
        if (dependentWords.contains(word)) {
            return false;
        }

        dependentWords.add(word);

        final List<List<Puzzle.Cube.Face>> paths = Objects.requireNonNull(pathsForWord.get(word));
        final boolean isPossible = paths.stream().anyMatch(this::isPathPossible);

        dependentWords.remove(word);

        return isPossible;
    }

    private boolean isPathPossible(List<Puzzle.Cube.Face> path) {
        final Set<Puzzle.Cube> cubesToRemove = new HashSet<>();
        for (final Puzzle.Cube.Face face : path) {
            cubesToRemove.addAll(cubesBlockingFace.getOrDefault(face, Collections.emptySet()));
        }
        return cubesToRemove.stream().allMatch(this::isRemovingCubePossible);
    }

    private boolean isRemovingCubePossible(Puzzle.Cube cube) {
        return wordsIncludingCube.getOrDefault(cube, Collections.emptySet()).stream()
                .allMatch(this::isWordPossible);
    }

    private String getPathAsString() {
        char[] wordChars = new char[path.size()];
        for (int i = 0; i < wordChars.length; i++) {
            wordChars[i] = path.get(i).getLetter();
        }
        return new String(wordChars);
    }

    private void loadWordTrie() {
        try (final Stream<String> lines = Files.lines(
                Path.of(ClassLoader.getSystemResource(WORDS_LIST_RESOURCE_NAME).toURI()))) {
            lines.forEach(wordTrie::insert);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
