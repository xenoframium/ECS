package xenoframium.ecs.xenoframium.ecs.state;

/**
 * Created by chrisjung on 18/12/17.
 */
public class EmptyGameState implements GameState {
    @Override
    public void init() {}

    @Override
    public void update(double delta, double time) {}

    @Override
    public void render() {}

    @Override
    public void destroy() {}
}
