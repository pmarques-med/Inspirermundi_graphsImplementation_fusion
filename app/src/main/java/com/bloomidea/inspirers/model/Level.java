package com.bloomidea.inspirers.model;

/**
 * Created by michellobato on 17/03/17.
 */

public class Level {
    private int level;
    private int points;

    public Level(int level, int points) {
        this.level = level;
        this.points = points;
    }

    public int getLevel() {
        return level;
    }

    public int getPoints() {
        return points;
    }
}
