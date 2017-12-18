package xenoframium.ecs;

/**
 * Created by chrisjung on 18/12/17.
 */
public interface BaseSystem {
    void update(Space space, double delta, double time);
}
