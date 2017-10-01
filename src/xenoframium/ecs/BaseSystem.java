package xenoframium.ecs;

/**
 * Created by chrisjung on 28/09/17.
 */
public interface BaseSystem {
    void notifyEntityAddition(Entity e);
    void notifyEntityRemoval(Entity e);
    void update(EntityManager em, long deltaT, long time);
}
