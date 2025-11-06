package Game.Map;
import javafx.scene.image.Image;

import java.util.List;

public class Map5 extends Map{
    public Map5(){
        super(new Image(Map.class.getResource("/Image/Map5.jpg").toExternalForm()),
                Map.class.getResourceAsStream("/Map/Map5"));
    }
}
