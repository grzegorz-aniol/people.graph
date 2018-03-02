package people.performance;

import com.codahale.metrics.Timer;
import lombok.val;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

public class MetricsFormatter {

    public static String getMetricDescription(Timer timer, TemporalUnit unit) {
        val str = new StringBuffer();
        val snapshot = timer.getSnapshot();
        long avg = Math.round(snapshot.getMean());
        str.append("avg=").append(String.format("%.3f", Duration.of(avg, unit).getNano() * 1e-9)).append(", ");
        str.append("min=").append(String.format("%.3f", Duration.of(snapshot.getMin(), unit).getNano() * 1e-9)).append(", ");
        str.append("max=").append(String.format("%.3f", Duration.of(snapshot.getMax(), unit).getNano() * 1e-9)).append(", ");
        return str.toString();
    }

}
