package Game.Map;
import javafx.scene.image.Image;

public class Map2 extends Map{
    public Map2(){
        super(new Image(Map.class.getResource("/Image/Map2.png").toExternalForm()),
                Map.class.getResourceAsStream("/Map/Map2"));
    }
}
