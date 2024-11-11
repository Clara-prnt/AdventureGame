package si.um.feri.clara;

import com.badlogic.gdx.math.Rectangle;

public class Entity {
    public enum Type {
        VILLAIN, CARROT, GOLDEN_CARROT, EGG
    }

    public Rectangle rectangle;
    public Type type;

    public Entity(Rectangle rectangle, Type type) {
        this.rectangle = rectangle;
        this.type = type;
    }
}
