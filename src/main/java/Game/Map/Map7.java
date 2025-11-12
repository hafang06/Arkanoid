package Game.Map;

import javafx.scene.image.Image;

public class Map7 extends Map{
    public Map7(){
        super(new Image(Map.class.getResource("/Image/Map7.png").toExternalForm()),
                Map.class.getResourceAsStream("/Map/Map7"));
    }
}
