package xenoframium.ecs;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisjung on 18/12/17.
 */
public class BasicFilter implements EntityFilter {
    private Set<Class<? extends Component>> requiredComponents = new HashSet<>();

    public BasicFilter(Class<? extends Component>... requiredComponents) {
        this.requiredComponents.addAll(Arrays.asList(requiredComponents));
    }

    @Override
    public boolean doesMatch(Entity e) {
        for (Class<? extends Component> component : requiredComponents) {
            if (!e.hasComponent(component)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BasicFilter that = (BasicFilter) o;

        return requiredComponents.equals(that.requiredComponents);
    }

    @Override
    public int hashCode() {
        return requiredComponents.hashCode();
    }
}
