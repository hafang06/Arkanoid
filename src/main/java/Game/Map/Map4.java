package Game.Map;
import javafx.scene.image.Image;

import java.util.List;

public class Map4 extends Map{
    public Map4(){
        super(new Image(Map.class.getResource("/Image/Map4.png").toExternalForm()),
                Map.class.getResourceAsStream("/Map/Map4"));
    }
}
