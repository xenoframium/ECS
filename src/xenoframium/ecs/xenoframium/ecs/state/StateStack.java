package xenoframium.ecs.xenoframium.ecs.state;

import java.util.ArrayList;

/**
 * Created by chrisjung on 18/12/17.
 */
public class StateStack implements GameStateManager {
    public static class NoStateException extends RuntimeException {
        private NoStateException(String msg) {
            super(msg);
        }
    }

    ArrayList<GameState> stack = new ArrayList<>();

    public StateStack() {
        push(new EmptyGameState());
    }

    public void push(GameState state) {
        if (state == null) {
            throw new NullStateException("Pushing a null state is forbidden.");
        }
        state.init();
        stack.add(state);
    }

    public void pop() {
        if (stack.size() == 0) {
            throw new NoStateException("Attempted to pop off an empty StateStack.");
        }
        GameState state = stack.get(stack.size()-1);
        state.destroy();
        stack.remove(stack.size()-1);
    }

    public GameState peek() {
        if (stack.size() == 0) {
            throw new NoStateException("Attempted to peek on an empty StateStack.");
        }
        return stack.get(stack.size()-1);
    }

    @Override
    public void update(double delta, double time) {
        if (stack.size() == 0) {
            throw new NoStateException("Updating requires a state but this StateStack is empty.");
        }
        peek().update(delta, time);
    }

    @Override
    public void render() {
        if (stack.size() == 0) {
            throw new NoStateException("Rendering requires a state but this StateStack is empty.");
        }
        peek().render();
    }
}
