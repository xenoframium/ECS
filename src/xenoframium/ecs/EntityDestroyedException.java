package xenoframium.ecs;

/**
 * Created by chrisjung on 28/09/17.
 */
public class EntityDestroyedException extends RuntimeException {
    public EntityDestroyedException() {
        super("Attempted to access destroyed entity.");
    }
}
