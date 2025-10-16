package Game.Map;
import Game.Brick.Brick;
import Game.Brick.Brick1Hp;
import Game.Brick.Brick2Hp;
import Game.Brick.Brick3Hp;
import Game.GameObject;
import Game.Renderer;

import java.util.ArrayList;
import java.util.List;

public class Map {
    protected int numOfBricks;

    public Map(int numOfBricks){
        this.numOfBricks=numOfBricks;
    }

    public int getNumOfBricks() {
        return numOfBricks;
    }

    public void addBricks(List<Brick> objs){}
}
