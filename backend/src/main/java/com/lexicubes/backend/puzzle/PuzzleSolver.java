package com.lexicubes.backend.puzzle;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PuzzleSolver {

    private static final String WORD_LIST_RESOURCE_NAME = "wordlists/common.txt";

    /**
     * The root node of a trie storing the dictionary of valid words.
     */
    private final TrieNode wordTrie;

    /**
     * The current path being explored by {@link #explorePaths(Puzzle.Cube.Face, TrieNode)}.
     */
    private final List<Puzzle.Cube.Face> path;

    /**
     * Used by {@link #explorePaths(Puzzle.Cube.Face, TrieNode)} to track which cubes
     * have been removed from consideration for extending the current path. Multiple
     * faces in the current path may have the same cube blocking it, in which case
     * this multiset will contain multiple instances of that cube.
     */
    private final Multiset<Puzzle.Cube> unusableCubes;

    /**
     * Maps a valid word to all the paths that form it.
     */
    private final Map<String, List<List<Puzzle.Cube.Face>>> pathsForWord;

    /**
     * Maps a cube to all the valid words with a path including that cube.
     */
    private final Map<Puzzle.Cube, Set<String>> wordsIncludingCube;

    /**
     * Memoization cache for {@link #getCubesBlockingFace(Puzzle.Cube.Face)}.
     */
    private final Map<Puzzle.Cube.Face, Set<Puzzle.Cube>> cubesBlockingFace;

    /**
     * Used by {@link #isWordAlwaysPossible(String)} and {@link #canRemoveCube(Puzzle.Cube)}
     * to track the current words we are trying to uncover paths for. This lets us detect
     * when removing a cube would require us to form a word that we are in the midst of
     * trying to uncover (which would indicate a circular dependency).
     */
    private final Set<String> wordsBeingUncovered;

    /**
     * The current puzzle being solved.
     */
    private Puzzle puzzle;

    /**
     * Represents a valid word found in the puzzle.
     * <p>
     * Words can either be bonus words or non-bonus (required) words.
     * A cube is removed when all required words that have a path including that cube are found.
     * It is always possible to form all required words, unlike bonus words, which may or may not
     * be possible to form depending on the order in which cubes are removed.
     *
     * @param word    the word that was found in the puzzle
     * @param faces   all the faces from any path forming the word
     * @param isBonus whether the word is a bonus word
     */
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
        wordsBeingUncovered = new HashSet<>();
    }

    /**
     * Solves the given puzzle, finding all valid words that can be formed.
     *
     * @param puzzle the puzzle to solve
     * @return the list of puzzle solutions
     */
    @Cacheable(cacheNames = "puzzleSolutions", key = "#puzzle.id", unless = "#puzzle.id == null")
    public List<PuzzleSolution> solve(Puzzle puzzle) {
        this.puzzle = puzzle;
        resetState();

        for (final Puzzle.Cube cube : puzzle.getCubes()) {
            explorePaths(cube.getTopFace(), wordTrie);
            explorePaths(cube.getLeftFace(), wordTrie);
            explorePaths(cube.getRightFace(), wordTrie);
        }

        final List<PuzzleSolution> solutions = buildSolutionsList();

        this.puzzle = null;
        resetState();

        return solutions;
    }

    /**
     * Resets all the working state.
     */
    private void resetState() {
        path.clear();
        unusableCubes.clear();
        pathsForWord.clear();
        wordsIncludingCube.clear();
        cubesBlockingFace.clear();
        wordsBeingUncovered.clear();
    }

    /**
     * Builds the final list of solutions from the discovered word paths.
     *
     * @return the list of puzzle solutions
     */
    private List<PuzzleSolution> buildSolutionsList() {
        final List<PuzzleSolution> solutions = new ArrayList<>();

        for (final Map.Entry<String, List<List<Puzzle.Cube.Face>>> entry : pathsForWord.entrySet()) {
            final String word = entry.getKey();
            final List<List<Puzzle.Cube.Face>> paths = entry.getValue();

            final boolean isBonus = !isWordAlwaysPossible(word);
            final Set<Puzzle.Cube.Face> faces = paths.stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());

            solutions.add(new PuzzleSolution(word, faces, isBonus));
        }

        return solutions;
    }

    /**
     * Recursively explores all possible paths starting from the given face, backtracking
     * when a path yields no valid solutions.
     *
     * @param face     the face to continue exploring the current path from
     * @param trieNode the node in the dictionary trie representing the current path explored so far
     */
    private void explorePaths(Puzzle.Cube.Face face, TrieNode trieNode) {

        // Check if the current face continues a valid path in our dictionary
        final TrieNode childTrieNode = trieNode.getChild(face.getLetter());
        if (childTrieNode == null) {
            return;
        }

        path.add(face);

        if (childTrieNode.isEnd()) {
            recordFoundWord();
        }

        // There may be cubes in front of the current face blocking its visibility.
        // Therefore, for the current path to be valid (i.e. using only faces visible to the player),
        // we must remove these cubes from consideration when searching for neighbouring faces.
        final Set<Puzzle.Cube> cubesBlockingFace = getCubesBlockingFace(face);
        unusableCubes.addAll(cubesBlockingFace);

        for (final Puzzle.Cube.Face neighbour : face.getNeighbours()) {
            if (!unusableCubes.contains(neighbour.getCube()) && !path.contains(neighbour)) {
                explorePaths(neighbour, childTrieNode);
            }
        }

        unusableCubes.removeAll(cubesBlockingFace);
        path.removeLast();
    }

    /**
     * Records the current path as a valid word found during path exploration.
     */
    private void recordFoundWord() {
        final String word = convertPathToWord(path);
        final List<Puzzle.Cube.Face> pathCopy = List.copyOf(path);
        pathsForWord.computeIfAbsent(word, k -> new ArrayList<>()).add(pathCopy);

        for (final Puzzle.Cube.Face faceInPath : path) {
            wordsIncludingCube.computeIfAbsent(faceInPath.getCube(), k -> new HashSet<>()).add(word);
        }
    }

    /**
     * Get all cubes in the given puzzle that directly block visibility of the given face.
     * These are the cubes that must be removed before the face is fully visible and considered
     * "in play".
     *
     * @param face the face to check the visibility of
     * @return the set of cubes that block visibility of the face
     */
    private Set<Puzzle.Cube> getCubesBlockingFace(Puzzle.Cube.Face face) {
        if (!cubesBlockingFace.containsKey(face)) {
            final Puzzle.Cube cube = face.getCube();
            final Set<Puzzle.Cube> cubes = new HashSet<>();

            switch (face.getSide()) {
                case TOP -> {
                    cubes.addAll(getCubesBlockingCoordinate(cube.getX(), cube.getY() + 1, cube.getZ()));
                    cubes.addAll(getCubesBlockingCoordinate(cube.getX() + 1, cube.getY() + 1, cube.getZ()));
                    cubes.addAll(getCubesBlockingCoordinate(cube.getX(), cube.getY() + 1, cube.getZ() + 1));
                    cubes.addAll(getCubesBlockingCoordinate(cube.getX() + 1, cube.getY() + 1, cube.getZ() + 1));
                }
                case LEFT -> {
                    cubes.addAll(getCubesBlockingCoordinate(cube.getX(), cube.getY(), cube.getZ() + 1));
                    cubes.addAll(getCubesBlockingCoordinate(cube.getX() + 1, cube.getY(), cube.getZ() + 1));
                    cubes.addAll(getCubesBlockingCoordinate(cube.getX() + 1, cube.getY() + 1, cube.getZ() + 1));
                    cubes.addAll(getCubesBlockingCoordinate(cube.getX(), cube.getY() + 1, cube.getZ() + 1));
                }
                case RIGHT -> {
                    cubes.addAll(getCubesBlockingCoordinate(cube.getX() + 1, cube.getY(), cube.getZ()));
                    cubes.addAll(getCubesBlockingCoordinate(cube.getX() + 1, cube.getY(), cube.getZ() + 1));
                    cubes.addAll(getCubesBlockingCoordinate(cube.getX() + 1, cube.getY() + 1, cube.getZ() + 1));
                    cubes.addAll(getCubesBlockingCoordinate(cube.getX() + 1, cube.getY() + 1, cube.getZ()));
                }
            }

            cubesBlockingFace.put(face, Set.copyOf(cubes));
        }
        return cubesBlockingFace.get(face);
    }

    /**
     * Finds cubes that occlude visibility along an isometric ray from the given coordinate pointed
     * towards the camera/screen. In isometric projection, occlusion happens along diagonal rays
     * with equal x, y, and z increments.
     *
     * @param x the starting x coordinate of the ray
     * @param y the starting y coordinate of the ray
     * @param z the starting z coordinate of the ray
     * @return all cubes blocking the ray
     */
    private List<Puzzle.Cube> getCubesBlockingCoordinate(int x, int y, int z) {
        final List<Puzzle.Cube> cubes = new ArrayList<>();
        for (int i = 0; puzzle.isWithinBounds(x + i, y + i, z + i); i++) {
            puzzle.getCubeAtCoordinate(x + i, y + i, z + i).ifPresent(cubes::add);
        }
        return cubes;
    }

    /**
     * Determines if it is possible to uncover a path that spells the given word, without
     * removing any cubes that themselves depend on the removal of:
     * <ul>
     *      <li>
     *          cubes in said path, or
     *      </li>
     *      <li>
     *          cubes in the paths of any other words that must be found in order to
     *          remove cubes blocking said path.
     *      </li>
     * </ul>
     * If true, then it is guaranteed that this word can always be formed, regardless of
     * the order in which cubes are removed. Otherwise, this word is considered a bonus word.
     *
     * @param word the word to check
     * @return true if it is possible to uncover a path that spells the word, false if not
     */
    private boolean isWordAlwaysPossible(String word) {
        wordsBeingUncovered.add(word);

        final List<List<Puzzle.Cube.Face>> paths = Objects.requireNonNull(pathsForWord.get(word));
        final boolean isPossible = paths.stream().anyMatch(this::isPathAlwaysPossible);

        wordsBeingUncovered.remove(word);

        return isPossible;
    }

    /**
     * Determines if it is possible to uncover the given path, following the criteria
     * outlined in {@link #isWordAlwaysPossible(String)}.
     *
     * @param path the path to check
     * @return true if the path can be uncovered, false if not
     */
    private boolean isPathAlwaysPossible(List<Puzzle.Cube.Face> path) {
        final Set<Puzzle.Cube> cubesToRemove = new HashSet<>();
        for (final Puzzle.Cube.Face face : path) {
            cubesToRemove.addAll(cubesBlockingFace.getOrDefault(face, Collections.emptySet()));
        }
        return cubesToRemove.stream().allMatch(this::canRemoveCube);
    }

    /**
     * Determines if it is possible to remove the given cube by finding all words with a
     * solution including that cube. If removing the cube requires a word whose solution
     * depends on the removal of that cube itself, then it is not possible to remove the
     * cube (circular dependency) and this method returns false.
     *
     * @param cube the cube to check
     * @return true if the cube can be removed, false if not
     */
    private boolean canRemoveCube(Puzzle.Cube cube) {
        return wordsIncludingCube.getOrDefault(cube, Collections.emptySet()).stream()
                .allMatch(word -> !wordsBeingUncovered.contains(word) && isWordAlwaysPossible(word));
    }

    /**
     * {@return the word formed by the given path}
     */
    private static String convertPathToWord(List<Puzzle.Cube.Face> path) {
        final char[] wordChars = new char[path.size()];
        for (int i = 0; i < wordChars.length; i++) {
            wordChars[i] = path.get(i).getLetter();
        }
        return new String(wordChars);
    }

    private void loadWordTrie() {
        final URL wordList = getClass().getClassLoader().getResource(WORD_LIST_RESOURCE_NAME);
        try (final Stream<String> lines = Files.lines(Path.of(Objects.requireNonNull(wordList).toURI()))) {
            lines.forEach(wordTrie::insert);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
