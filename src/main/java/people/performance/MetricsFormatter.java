package people.performance;

import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

public class MetricsFormatter {

    public static String getMetricDescription(Timer timer, TemporalUnit unit) {
        StringBuffer str = new StringBuffer();
        Snapshot snapshot = timer.getSnapshot();
        long avg = Math.round(snapshot.getMean());
        str.append("avg=").append(String.format("%.3f ms", Duration.of(avg, unit).getNano() * 1e-6)).append(", ");
        str.append("min=").append(String.format("%.3f ms", Duration.of(snapshot.getMin(), unit).getNano() * 1e-6)).append(", ");
        str.append("max=").append(String.format("%.3f ms", Duration.of(snapshot.getMax(), unit).getNano() * 1e-6)).append(", ");
        return str.toString();
    }

}
