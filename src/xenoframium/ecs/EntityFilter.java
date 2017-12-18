package xenoframium.ecs;

/**
 * Created by chrisjung on 18/12/17.
 */
public interface EntityFilter {
    boolean doesMatch(Entity e);
}
