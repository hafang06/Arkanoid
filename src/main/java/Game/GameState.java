package Game;

import Game.PowerUp.PowerUp;
import Game.PowerUp.PowerUpManager;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private PowerUpManager powerUpManager;
    private double paddleX, paddleY;
    private int paddleSpeed;
    private int score;
    private int lives;

    private int level;

    public List<BrickState> bricks = new ArrayList<>();
    public List<Ball> balls = new ArrayList<>();

    public static class BrickState{
        private double x, y;
        private boolean destroyed;
        private int hitPoints;

        public BrickState(double x, double y, boolean destroyed, int hitPoints){
            this.x = x;
            this.y = y;
            this.destroyed = destroyed;
            this.hitPoints = hitPoints;
        }


        //Getter and Setter below
        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public boolean isDestroyed() {
            return destroyed;
        }

        public void setDestroyed(boolean destroyed) {
            this.destroyed = destroyed;
        }

        public int getHitPoints() {
            return hitPoints;
        }

        public void setHitPoints(int hitPoints) {
            this.hitPoints = hitPoints;
        }
    }

    public PowerUpManager getPowerUpManager() {
        return powerUpManager;
    }

    public void setPowerUpManager(PowerUpManager powerUpManager) {
        this.powerUpManager = powerUpManager;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getPaddleSpeed() {
        return paddleSpeed;
    }

    public void setPaddleSpeed(int paddleSpeed) {
        this.paddleSpeed = paddleSpeed;
    }

    public double getPaddleY() {
        return paddleY;
    }

    public void setPaddleY(double paddleY) {
        this.paddleY = paddleY;
    }

    public double getPaddleX() {
        return paddleX;
    }

    public void setPaddleX(double paddleX) {
        this.paddleX = paddleX;
    }
}
