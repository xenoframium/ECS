package xenoframium.ecs.xenoframium.ecs.state;

/**
 * Created by chrisjung on 18/12/17.
 */
public interface GameState {
    void init();
    void update(double delta, double time);
    void render();
    void destroy();
}
