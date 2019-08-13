package com.allenday.image;

class SearchResult {
    private final String id;
    private final Double score;

    public SearchResult(String id, Double score) {
        this.id = id;
        this.score = score;
    }

    public Double getScore() {
        return score;
    }

    public String getId() {
        return id;
    }
}
