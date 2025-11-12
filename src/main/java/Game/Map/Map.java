package Game.Map;

import Game.Brick.*;
import Game.GameObject;
import Game.Renderer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Map {
    protected List<Brick> bricks;
    protected transient Image BackGround;
    protected InputStream path;

    public Map(Image BackGround, InputStream path){
        this.BackGround = BackGround;
        this.path = path;
        this.bricks = new ArrayList<>();
        loadBricks(path);
    }
    public List<Brick> getBricks() {
        return bricks;
    }

    public void loadBricks(InputStream path) {
        Scanner sc = new Scanner(path);

        int x = sc.nextInt();
        int y = sc.nextInt();
        sc.nextLine();
        while (sc.hasNextLine()) {
            String s = sc.nextLine();
            for (char c : s.toCharArray()) {
                switch (c) {
                    case '1':
                        bricks.add(new Brick1Hp(x,y));
                        x += 50;
                        break;
                    case '2':
                        bricks.add(new Brick2Hp(x,y));
                        x += 50;
                        break;
                    case '3':
                        bricks.add(new Brick3Hp(x,y));
                        x += 50;
                        break;
                    case '4':
                        bricks.add(new Brick4Hp(x,y));
                        x += 50;
                        break;
                    case '5':
                        bricks.add(new Brick5Hp(x,y));
                        x += 50;
                        break;
                    case '6':
                        bricks.add(new unBreakBrick(x, y));
                        x += 50;
                        break;
                    case '-':
                        x += 50;
                        break;
                }
            }
            x = 0;
            y += 20;
        }
    }

    public Image getBackGround() {
        return BackGround;
    }

    public void render(GraphicsContext gc) {
            gc.drawImage(BackGround,0,0,800,600);
    }
}
