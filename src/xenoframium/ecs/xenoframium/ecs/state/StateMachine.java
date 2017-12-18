package xenoframium.ecs.xenoframium.ecs.state;

/**
 * Created by chrisjung on 18/12/17.
 */
public class StateMachine implements GameStateManager {
    private GameState currentState;

    public StateMachine() {
        setState(new EmptyGameState());
    }

    public void setState(GameState state) {
        if (state == null) {
            throw new NullStateException("Setting to a null GameState is forbidden.");
        }
        currentState = state;
    }

    public GameState getState() {
        return currentState;
    }

    @Override
    public void update(double delta, double time) {
        currentState.update(delta, time);
    }

    @Override
    public void render() {
        currentState.render();
    }
}
