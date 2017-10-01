package xenoframium.ecs;

/**
 * Created by chrisjung on 1/10/17.
 */
public interface Observable {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
}
