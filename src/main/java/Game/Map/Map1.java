package Game.Map;

import Game.Brick.Brick;
import Game.Brick.Brick1Hp;
import Game.Brick.Brick2Hp;
import Game.Brick.Brick3Hp;
import Game.GameObject;

import java.util.List;

public class Map1 extends Map{

    public Map1(){
        super(24);
    }
    public void addBricks(List<Brick> objs){
        for(int i=0;i<8;i++){
            objs.add( new Brick3Hp(i*100,100));
        }
        for(int i=0;i<8;i++){
            objs.add(new Brick2Hp(50+i*100,120));
        }
        for(int i=0;i<8;i++){
            objs.add( new Brick1Hp(i*100,140));
        }
    }
}
