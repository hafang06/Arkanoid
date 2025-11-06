package Game.Map;
import javafx.scene.image.Image;

import java.util.List;

public class Map2 extends Map{
    public Map2(){
        super(new Image(Map.class.getResource("/Image/Map2.jpg").toExternalForm()),
                Map.class.getResourceAsStream("/Map/Map2"));
    }
}
