package xenoframium.ecs;

/**
 * Created by chrisjung on 18/12/17.
 */
public final class Entity {
    private static int numEntities = 0;

    private final int id;
    private final EntityManager manager;

    boolean isDestroyed = false;

    Entity(EntityManager manager) {
        id = numEntities;
        numEntities++;
        this.manager = manager;
    }

    public boolean hasComponent(Class<? extends Component> componentClass) {
        return manager.hasComponent(this, componentClass);
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        return manager.getComponent(this, componentClass);
    }

    public <T extends Component> void addComponent(T component) {
        manager.addComponent(this, component);
    }

    public void removeComponent(Class<? extends Component> componentClass) {
        manager.removeComponent(this, componentClass);
    }

    public void destroy() {
        manager.destroyEntity(this);
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }
}
