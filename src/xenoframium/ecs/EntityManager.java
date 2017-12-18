package xenoframium.ecs;

import org.lwjgl.glfw.GLFW;
import xenoframium.ecs.xenoframium.ecs.state.GameStateManager;

import java.util.*;
import java.util.Map.Entry;

/**
 * Created by chrisjung on 18/12/17.
 */
public final class EntityManager {
    private Map<Entity, HashMap<Class<?>, Component>> entityToComponents = new HashMap<>();
    private Map<EntityFilter, HashSet<Entity>> matchingEntities = new HashMap<>();

    private double lastTime = -1;

    private GameStateManager gameStateManager;

    public static class DestroyedEntityAccessException extends RuntimeException {
        private DestroyedEntityAccessException() {
            super("Attempted to access destroyed Entity");
        }
    }

    public static class DuplicateComponentAddedException extends RuntimeException {
        private DuplicateComponentAddedException(Class<? extends Component> componentClass) {
            super(String.format("Added duplicate component %s", componentClass.getName()));
        };
    }

    public static class MissingComponentAccessException extends RuntimeException {
        private MissingComponentAccessException(Class<? extends Component> componentClass) {
            super(String.format("Attempted to access missing component %s", componentClass.getName()));
        };
    }

    public static class NullStateManagerException extends RuntimeException {
        private NullStateManagerException() {
            super("Attempted to set StateManager to null.");
        }
    }

    public EntityManager(GameStateManager mgr) {
        if (mgr == null) {
            throw new NullStateManagerException();
        }
        gameStateManager = mgr;
    }

    private void throwIfEntityDestroyed(Entity e) {
        if (!entityToComponents.containsKey(e)) {
            throw new DestroyedEntityAccessException();
        }
    }

    private void updateEntity(Entity e) {
        for (Entry<EntityFilter, HashSet<Entity>> entry : matchingEntities.entrySet()) {
            EntityFilter filter = entry.getKey();
            HashSet<Entity> entities = entry.getValue();

            if (entities.contains(e)) {
                if (!filter.doesMatch(e)) {
                    entities.remove(e);
                }
            } else {
                if (filter.doesMatch(e)) {
                    entities.add(e);
                }
            }
        }
    }

    Entity createEntity() {
        Entity e = new Entity(this);
        entityToComponents.put(e, new HashMap<>());

        return e;
    }

    boolean hasComponent(Entity e, Class<? extends Component> component) {
        throwIfEntityDestroyed(e);

        return entityToComponents.get(e).containsKey(component);
    }

    <T extends Component> T getComponent(Entity e, Class<T> component) {
        throwIfEntityDestroyed(e);
        if (!hasComponent(e, component)) {
            throw new MissingComponentAccessException(component);
        }

        return (T) entityToComponents.get(e).get(component);
    }

    <T extends Component> void addComponent(Entity e, T component) {
        throwIfEntityDestroyed(e);
        if (hasComponent(e, component.getClass())) {
            throw new DuplicateComponentAddedException(component.getClass());
        }

        entityToComponents.get(e).put(component.getClass(), component);
        updateEntity(e);
    }

    void removeComponent(Entity e, Class<? extends Component> component) {
        throwIfEntityDestroyed(e);
        if (!hasComponent(e, component)) {
            throw new MissingComponentAccessException(component);
        }

        entityToComponents.get(e).remove(component);
        updateEntity(e);
    }

    void destroyEntity(Entity e) {
        throwIfEntityDestroyed(e);

        entityToComponents.remove(e);

        for (Set<Entity> entityList : matchingEntities.values()) {
            entityList.remove(e);
        }

        e.isDestroyed = true;
    }

    Set<Entity> getEntities(EntityFilter filter) {
        if (!matchingEntities.containsKey(filter)) {
            matchingEntities.put(filter, new HashSet<>());

            for (Entity e : entityToComponents.keySet()) {
                if (filter.doesMatch(e)) {
                    matchingEntities.get(filter).add(e);
                }
            }
        }

        return Collections.unmodifiableSet(matchingEntities.get(filter));
    }

    public Space createSpace() {
        return new Space(this);
    }

    public void update() {
        double currentTime = GLFW.glfwGetTime();
        if (lastTime == -1) {
            lastTime = currentTime;
        }
        double delta = currentTime - lastTime;

        gameStateManager.update(delta, currentTime);
    }

    public void render() {
        gameStateManager.render();
    }

    public void setStateManager(GameStateManager mgr) {
        if (mgr == null) {
            throw new NullStateManagerException();
        }
        this.gameStateManager = mgr;
    }

    public GameStateManager getStateManager() {
        return gameStateManager;
    }
}
