package xenoframium.ecs;

/**
 * Created by chrisjung on 3/10/17.
 */
public class DeltaTimeAggregator {
    public final double updateInterval;
    private double aggregateTime = 0;
    public DeltaTimeAggregator(double updateInterval) {
        this.updateInterval = updateInterval;
    }

    public boolean shouldUpdate() {
        return aggregateTime > updateInterval;
    }

    public void addTime(double time) {
        aggregateTime += time;
    }

    public void reset() {
        aggregateTime = aggregateTime % updateInterval;
    }
}
