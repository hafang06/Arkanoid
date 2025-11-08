package Game;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundManager {
    public static MediaPlayer bgPlayer;
    public static MediaPlayer MPlayer;
    public static MediaPlayer SMPlayer;

    public static void playBackgroundMusic( boolean loop) {
        // dừng nhạc cũ nếu có
        if (bgPlayer != null) {
            bgPlayer.stop();
        }

        try {
            String path = SoundManager.class.getResource("/Music/01 Game Start.mp3").toExternalForm();
            Media media = new Media(path);
            bgPlayer = new MediaPlayer(media);
            bgPlayer.setCycleCount(loop ? MediaPlayer.INDEFINITE : 1);
            bgPlayer.play();
        } catch (Exception e) {
            System.out.println("Error playing background music: " + e.getMessage());
        }
    }

    public static void playGameMusic(boolean loop) {
        //stopMusic(); // dừng nhạc cũ

        try {
            String path = SoundManager.class.getResource("/Music/GameMusic.mp3").toExternalForm();
            Media media = new Media(path);
            MPlayer = new MediaPlayer(media);
            MPlayer.setCycleCount(loop ? MediaPlayer.INDEFINITE : 1);
            MPlayer.setVolume(0.3); // giảm âm lượng
            MPlayer.play();
        } catch (Exception e) {
            System.out.println("Error playing game music: " + e.getMessage());
        }
    }

    public static void PowerUpMusic() {
        //stopMusic(); // dừng nhạc cũ

        try {
            String path = SoundManager.class.getResource("/Music/PowerUp.mp3").toExternalForm();
            Media media = new Media(path);
            SMPlayer = new MediaPlayer(media);
            //SMPlayer.setVolume(0.3); // giảm âm lượng
            SMPlayer.play();
        } catch (Exception e) {
            System.out.println("Error playing game music: " + e.getMessage());
        }
    }

    public static void NextLevel() {
        //stopMusic(); // dừng nhạc cũ

        try {
            String path = SoundManager.class.getResource("/Music/NextLevel.mp3").toExternalForm();
            Media media = new Media(path);
            SMPlayer = new MediaPlayer(media);
            //SMPlayer.setVolume(0.3); // giảm âm lượng
            SMPlayer.play();
        } catch (Exception e) {
            System.out.println("Error playing game music: " + e.getMessage());
        }
    }

    public static void lostLive() {
        //stopMusic(); // dừng nhạc cũ

        try {
            String path = SoundManager.class.getResource("/Music/lostLive.mp3").toExternalForm();
            Media media = new Media(path);
            SMPlayer = new MediaPlayer(media);
            //SMPlayer.setVolume(0.3); // giảm âm lượng
            SMPlayer.play();
        } catch (Exception e) {
            System.out.println("Error playing game music: " + e.getMessage());
        }
    }

    public static void GameOver() {
        //stopMusic(); // dừng nhạc cũ

        try {
            String path = SoundManager.class.getResource("/Music/Game_Over.mp3").toExternalForm();
            Media media = new Media(path);
            SMPlayer = new MediaPlayer(media);
            //SMPlayer.setVolume(0.3); // giảm âm lượng
            SMPlayer.play();
        } catch (Exception e) {
            System.out.println("Error playing game music: " + e.getMessage());
        }
    }

    public static void stopMusic() {
        if (bgPlayer != null) {
            bgPlayer.stop();
        }
    }
    public static void stopMusic(MediaPlayer mp) {
        if (mp != null) {
            mp.stop();
        }
    }
}
