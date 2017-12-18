package xenoframium.ecs;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisjung on 18/12/17.
 */
public final class Space {
    private EntityManager manager;
    private Set<Entity> entities = new HashSet<>();

    private boolean isDestroyed = false;

    Space(EntityManager manager) {
        this.manager = manager;
    }

    public static class EntityNotFoundInSpaceException extends RuntimeException {
        private EntityNotFoundInSpaceException() {}
    }

    public static class SpaceAlreadyDestroyedException extends RuntimeException {
        private SpaceAlreadyDestroyedException() {}
    }

    private void throwIfEntityNotInSpace(Entity e) {
        if (!entities.contains(e)) {
            throw new EntityNotFoundInSpaceException();
        }
    }

    public Entity createEntity() {
        Entity e = manager.createEntity();
        entities.add(e);

        return e;
    }

    public void transferEntity(Entity e, Space newSpace) {
        throwIfEntityNotInSpace(e);

        entities.remove(e);
        newSpace.entities.add(e);
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public void destroy() {
        if (isDestroyed) {
            throw new SpaceAlreadyDestroyedException();
        }

        for (Entity e : entities) {
            if (!e.isDestroyed()) {
                e.destroy();
            }
        }

        isDestroyed = true;
    }

    public Set<Entity> getEntities() {
        return Collections.unmodifiableSet(entities);
    }

    public Set<Entity> getEntities(EntityFilter filter) {
        Set<Entity> matchingEntities = manager.getEntities(filter);
        Set<Entity> result = new HashSet<>();
        for (Entity e : matchingEntities) {
            if (entities.contains(e)) {
                result.add(e);
            }
        }

        return Collections.unmodifiableSet(result);
    }
}
