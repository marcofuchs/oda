package kefu;

import java.util.Comparator;

/**
 *
 * @author andreas Ein Comparator für Crossings, damit diese in der
 * Priorityqueue sortiert werden können
 */
public class CrossingComparator implements Comparator<Crossing> {
    @Override
    public int compare(Crossing x, Crossing y) {
        if (x.getMaxDurationToReachCrossing() < y.getMaxDurationToReachCrossing()) {
            return -1;
        } else if (x.getMaxDurationToReachCrossing() > y.getMaxDurationToReachCrossing()) {
            return 1;
        }
        return 0;
    }
}
