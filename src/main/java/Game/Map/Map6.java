package Game.Map;
import javafx.scene.image.Image;

public class Map6 extends Map{
    public Map6(){
        super(new Image(Map.class.getResource("/Image/Map6.png").toExternalForm()),
                Map.class.getResourceAsStream("/Map/Map6"));
    }
}
