package pl.krasnoludkolo.ebet2.infrastructure;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class TimeProvider {

    private Clock clock;

    public static TimeProvider fromSystem() {
        return new TimeProvider(Clock.systemDefaultZone());
    }

    public static TimeProvider fromClock(Clock clock) {
        return new TimeProvider(clock);
    }

    private TimeProvider(Clock clock) {
        this.clock = clock;
    }

    public void setNow() {
        clock = Clock.systemDefaultZone();
    }

    public void setFixed(LocalDateTime fixed) {
        this.clock = Clock.fixed(fixed.toInstant(ZoneOffset.UTC), ZoneId.systemDefault());
    }

    public LocalDateTime now() {
        return LocalDateTime.now(clock);
    }
}
