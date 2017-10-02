package xenoframium.ecs;

import java.util.*;

/**
 * Created by chrisjung on 28/09/17.
 */
public final class EntityManager {
    private int entityCounter = 0;
    private long lastTime = -1;
    private Map<Entity, HashMap<Class, Component>> entityToComponents = new HashMap<>();
    private Set<BaseSystem> systems = new HashSet<>();
    private Map<BaseSystem, HashSet<Entity>> systemNotifiedEntities = new HashMap<>();
    private Map<BaseSystem, Class<? extends Component>[]> systemRequiredComponents = new HashMap<>();
    private Map<BaseSystem, ArrayList<BaseSystem>> systemPredecs = new HashMap<>();

    private void throwIfDestroyed(Entity e) {
        if (!entityToComponents.containsKey(e)) {
            throw new EntityDestroyedException();
        }
    }

    public Entity createEntity() {
        Entity e = new Entity(entityCounter++, this);
        entityToComponents.put(e, new HashMap<Class, Component>());
        return e;
    }

    public void destroyEntity(Entity e) {
        if (!entityToComponents.containsKey(e)) {
            return;
        }
        for (BaseSystem system : systems) {
            if (systemNotifiedEntities.get(system).contains(e)) {
                system.notifyEntityRemoval(e);
                systemNotifiedEntities.get(system).remove(e);
            }
        }
        entityToComponents.remove(e);
    }

    public <T extends Component> void addComponents(Entity e, T... components) {
        for (T component : components) {
            throwIfDestroyed(e);
            entityToComponents.get(e).put(component.getClass(), component);
        }

        Set<BaseSystem> vis = new HashSet<>();
        for (BaseSystem system : systems) {
            if (!vis.contains(system)) {
                topSortAdditions(system, vis, e);
            }
        }

    }

    public boolean hasComponents(Entity e, Class<? extends Component>... components) {
        throwIfDestroyed(e);
        for (Class<? extends Component> component : components) {
            if (!entityToComponents.get(e).containsKey(component)) {
                return false;
            }
        }
        return true;
    }

    public <T extends Component> T getComponent(Entity e, Class<T> componentClass) {
        throwIfDestroyed(e);
        return (T) entityToComponents.get(e).get(componentClass);
    }

    public void removeComponents(Entity e, Class<? extends Component>... componentClasses) {
        for (Class<? extends Component> clazz : componentClasses) {
            entityToComponents.get(e).remove(clazz);
        }
        for (BaseSystem system : systems) {
            if (systemNotifiedEntities.get(system).contains(e) && !hasComponents(e, systemRequiredComponents.get(system))) {
                system.notifyEntityRemoval(e);
                systemNotifiedEntities.get(system).remove(e);
            }
        }
    }

    public void subscribeSystem(BaseSystem system, Class<? extends Component>... requiredComponents) {
        systems.add(system);
        systemNotifiedEntities.put(system, new HashSet<>());
        systemRequiredComponents.put(system, requiredComponents);
        systemPredecs.put(system, new ArrayList<>());
        for (Entity e : entityToComponents.keySet()) {
            if (e.hasComponents(systemRequiredComponents.get(system))) {
                system.notifyEntityAddition(e);
                systemNotifiedEntities.get(system).add(e);
            }
        }
    }

    public void unsubscribeSystem(BaseSystem system) {
        systems.remove(system);
        systemNotifiedEntities.remove(system);
        systemRequiredComponents.remove(system);
        systemPredecs.remove(system);
        for (Entity e : entityToComponents.keySet()) {
            system.notifyEntityRemoval(e);
        }
    }

    public <T extends BaseSystem> void addSystemPredecessors(T system, BaseSystem... predecessors) {
        for (BaseSystem pre : predecessors) {
            systemPredecs.get(system).add(pre);
        }
    }

    private void topSortAdditions(BaseSystem system, Set<BaseSystem> vis, Entity e) {
        for (BaseSystem predec : systemPredecs.get(system)) {
            if (!vis.contains(predec)) {
                vis.add(predec);
                topSortAdditions(predec, vis, e);
            }
        }
        if (!systemNotifiedEntities.get(system).contains(e)) {
            if (e.hasComponents(systemRequiredComponents.get(system))) {
                system.notifyEntityAddition(e);
                systemNotifiedEntities.get(system).add(e);
            }
        }
    }

    private void topSortSystems(BaseSystem system, Set<BaseSystem> vis, long deltaT) {
        for (BaseSystem predec : systemPredecs.get(system)) {
            if (!vis.contains(predec)) {
                vis.add(predec);
                topSortSystems(predec, vis, deltaT);
            }
        }
        system.update(this, deltaT, lastTime);
    }

    public void updateSystems() {
        long currentTime = java.lang.System.currentTimeMillis();
        if (lastTime == -1) {
            lastTime = currentTime;
        }
        long deltaT = currentTime - lastTime;
        lastTime = currentTime;
        Set<BaseSystem> vis = new HashSet<>();
        for (BaseSystem system : systems) {
            if (!vis.contains(system)) {
                topSortSystems(system, vis, deltaT);
            }
        }
    }
}
