package people.performance;

import com.codahale.metrics.SlidingWindowReservoir;
import com.codahale.metrics.Timer;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicLong;

public class DataObjectMetric {

    @Getter
    private AtomicLong initialCount = new AtomicLong(0);

    @Getter
    private AtomicLong savedCount = new AtomicLong(0);

    @Getter
    private AtomicLong foundCount = new AtomicLong(0);

    @Getter
    private Timer saveTimer;

    @Getter
    private AtomicLong cacheHitRation = new AtomicLong(0);

    public DataObjectMetric() {
        saveTimer = new Timer(new SlidingWindowReservoir(1_000));
    }

}