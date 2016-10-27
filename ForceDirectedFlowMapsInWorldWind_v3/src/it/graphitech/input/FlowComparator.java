package it.graphitech.input;
import java.util.Comparator;


public class FlowComparator implements Comparator<FlowSource> {
    @Override
    public int compare(FlowSource o1, FlowSource o2) {
        return Integer.signum((int)(o2.flowMagnitude-o1.flowMagnitude));
    }
}