package iss.workshop.team8flipgame.model;

public class Score {
    private long id;
    private String name;
    private int score;
    private String difficulty;

    public Score(int id, String name, int score) {
        this.id = id;
        this.name = name;
        this.score = score;
    }


    public Score(String name, int score, String difficulty) {
        this.name = name;
        this.score = score;
        this.difficulty = difficulty;
    }

    public Score() {
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
