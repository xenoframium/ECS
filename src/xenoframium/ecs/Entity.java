package xenoframium.ecs;

/**
 * Created by chrisjung on 28/09/17.
 */
public final class Entity {
    final int id;
    final EntityManager mgr;

    Entity(int id, EntityManager mgr) {
        this.id = id;
        this.mgr = mgr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entity entity = (Entity) o;

        if (id != entity.id) return false;
        return mgr.equals(entity.mgr);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + mgr.hashCode();
        return result;
    }

    public void destroyEntity() {
        mgr.destroyEntity(this);
    }

    public <T extends Component> void addComponents(T... components) {
        mgr.addComponents(this, components);
    }

    public void removeComponents(Class<? extends Component>... components) {
        mgr.removeComponents(this, components);
    }

    public boolean hasComponents(Class<? extends Component>... components) {
        return mgr.hasComponents(this, components);
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        return mgr.getComponent(this, componentClass);
    }
}
