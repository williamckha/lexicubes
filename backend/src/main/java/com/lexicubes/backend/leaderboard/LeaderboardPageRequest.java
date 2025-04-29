package com.lexicubes.backend.leaderboard;

import com.lexicubes.backend.common.PageRequest;

import java.util.Objects;

public class LeaderboardPageRequest extends PageRequest {

    public enum SortOption {
        NUM_POINTS,
        NUM_REQUIRED_WORDS_FOUND,
        NUM_BONUS_WORDS_FOUND
    }

    private final SortOption sort;

    public LeaderboardPageRequest(int pageNumber, int pageSize, SortOption sort) {
        super(pageNumber, pageSize);
        this.sort = sort;
    }

    public SortOption getSort() {
        return sort;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), sort);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) &&
                obj instanceof LeaderboardPageRequest other &&
                sort == other.sort;
    }
}
