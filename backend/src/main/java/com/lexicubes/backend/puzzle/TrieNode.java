package com.lexicubes.backend.puzzle;

import org.jetbrains.annotations.Nullable;

/**
 * A node in a trie that stores strings containing only lowercase chars in the range [a-z].
 * Each node contains up to 26 children corresponding to the 26 lowercase English letters.
 */
public class TrieNode {

    private final TrieNode[] children = new TrieNode[26];
    private boolean isEnd = false;

    /**
     * Returns the child node associated with the given letter.
     *
     * @param letter the letter to look for (must be lowercase and in [a-z])
     * @return the child node corresponding to the letter, or null if the
     * given character is invalid or no such child exists
     */
    @Nullable
    public TrieNode getChild(char letter) {
        if (isCharacterInvalid(letter)) {
            return null;
        }

        final int index = letter - 'a';
        return children[index];
    }

    /**
     * Checks if this node represents the end of a string in the trie.
     *
     * @return true if this node is the end of a string
     */
    public boolean isEnd() {
        return isEnd;
    }

    /**
     * Inserts a string into the trie, assuming this node is the root.
     *
     * @param string the string to insert (must only contain lowercase letters in [a-z])
     */
    public void insert(String string) {
        if (isStringInvalid(string)) {
            throw new IllegalArgumentException("String contains invalid characters: " + string);
        }

        TrieNode node = this;
        for (char letter : string.toCharArray()) {
            final int index = letter - 'a';
            if (node.children[index] == null) {
                node.children[index] = new TrieNode();
            }
            node = node.children[index];
        }
        node.isEnd = true;
    }

    private boolean isStringInvalid(String string) {
        for (char letter : string.toCharArray()) {
            if (isCharacterInvalid(letter)) {
                return true;
            }
        }
        return false;
    }

    private boolean isCharacterInvalid(char letter) {
        return letter < 'a' || letter > 'z';
    }
}
