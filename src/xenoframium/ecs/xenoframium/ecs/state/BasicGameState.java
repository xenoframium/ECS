package xenoframium.ecs.xenoframium.ecs.state;

import xenoframium.ecs.BaseRenderer;
import xenoframium.ecs.BaseSystem;
import xenoframium.ecs.Space;

import java.util.*;

/**
 * Created by chrisjung on 18/12/17.
 */
public class BasicGameState implements GameState {
    private Map<Space, HashSet<BaseSystem>> spacesToSystems = new HashMap<>();
    private BaseRenderer renderer;

    public static class MissingSpaceException extends RuntimeException {
        private MissingSpaceException(String msg) {
            super(msg);
        }
    }

    public static class SystemNotFoundInSpaceException extends RuntimeException {
        private SystemNotFoundInSpaceException() {
            super("Attempted to access a System which is not bound to the corresponding Space.");
        }
    }

    public static class DuplicateSystemInSpaceException extends RuntimeException {
        private DuplicateSystemInSpaceException() {
            super("Duplicate System found while attaching new Systems to Space.");
        }
    }

    public static class DuplicateSpaceException extends RuntimeException {
        private DuplicateSpaceException() {
            super("Attempted to add the a Space when it was already added.");
        }
    }

    public static class NullRendererException extends RuntimeException {
        private NullRendererException() {
            super("Renderer cannot be null");
        }
    }

    public BasicGameState(BaseRenderer renderer, Space... spaces) {
        if (renderer == null) {
            throw new NullRendererException();
        }

        this.renderer = renderer;
        for (Space space : spaces) {
            spacesToSystems.put(space, new HashSet<>());
        }
    }

    private void throwIfSpaceNotFound(Space space) {
        if (!spacesToSystems.containsKey(space)) {
            throw new MissingSpaceException("Attempted to access non-existent space.");
        }
    }

    public void attachSystemsToSpace(Space space, BaseSystem... systems) {
        throwIfSpaceNotFound(space);

        for (BaseSystem system : systems) {
            if (spacesToSystems.containsKey(system)) {
                throw new DuplicateSystemInSpaceException();
            }

            spacesToSystems.get(space).add(system);
        }
    }

    public void removeSystemsFromSpace(Space space, BaseSystem... systems) {
        throwIfSpaceNotFound(space);

        for (BaseSystem system : systems) {
            if (!spacesToSystems.get(space).contains(system)) {
                throw new SystemNotFoundInSpaceException();
            }

            spacesToSystems.get(space).remove(system);
        }
    }

    public void addSpace(Space space) {
        if (spacesToSystems.containsKey(space)) {
            throw new DuplicateSpaceException();
        }

        spacesToSystems.put(space, new HashSet<>());
    }

    public void removeSpace(Space space) {
        throwIfSpaceNotFound(space);

        spacesToSystems.remove(space);
    }

    @Override
    public void init() {}

    @Override
    public void update(double delta, double time) {
        for (Map.Entry<Space, HashSet<BaseSystem>> entry : spacesToSystems.entrySet()) {
            Space space = entry.getKey();
            HashSet<BaseSystem> systems = entry.getValue();
            for (BaseSystem system : systems) {
                system.update(space, delta, time);
            }
        }
    }

    @Override
    public void render() {
        for (Space space : spacesToSystems.keySet()) {
            renderer.render(space);
        }
    }

    @Override
    public void destroy() {
        for (Space space : spacesToSystems.keySet()) {
            space.destroy();
        }
    }
}
