package Game.Map;

import Game.Brick.Brick;
import Game.Brick.Brick1Hp;
import Game.GameObject;

import java.util.List;

public class Map2 extends Map{
    public Map2(){
        super(1);
    }

    public void addBricks(List<Brick> objs){
        objs.add(new Brick1Hp(200,200));
    }
}
