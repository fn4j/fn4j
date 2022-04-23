package fn4j.http.client.apachehc;

public final class TickTock {
    private final long duration;

    private TickTock(Tick tick) {
        duration = System.currentTimeMillis() - tick.start;
    }

    public static Tick tick() {
        return new Tick();
    }

    public long duration() {
        return duration;
    }

    public static final class Tick {
        private final long start;

        private Tick() {
            start = System.currentTimeMillis();
        }

        public TickTock tock() {
            return new TickTock(this);
        }
    }
}