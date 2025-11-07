package Game.Map;
import javafx.scene.image.Image;

import java.io.File;
import java.util.List;

public class Map1 extends Map{
    public Map1(){
        super(new Image(Map.class.getResource("/Image/Map1.png").toExternalForm()),
                Map.class.getResourceAsStream("/Map/Map1"));
    }
}
