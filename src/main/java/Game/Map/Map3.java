package Game.Map;
import javafx.scene.image.Image;

import java.util.List;

public class Map3 extends Map{
    public Map3(){
        super(new Image(Map.class.getResource("/Image/Map3.png").toExternalForm()),
                Map.class.getResourceAsStream("/Map/Map3"));
    }
}
