package people.performance;

import com.codahale.metrics.SlidingWindowReservoir;
import com.codahale.metrics.Timer;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

public class DataObjectMetric {

    @Getter
    private LongAdder initialCount = new LongAdder();

    @Getter
    private LongAdder savedCount = new LongAdder();

    @Getter
    private LongAdder foundCount = new LongAdder();

    @Getter
    private Timer saveTimer;

    @Getter
    private LongAdder cacheHitRation = new LongAdder();

    public DataObjectMetric() {
        saveTimer = new Timer(new SlidingWindowReservoir(1_000));
    }

}