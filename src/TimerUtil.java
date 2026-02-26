public class TimerUtil {
    public static long nowNanos() {
        return System.nanoTime();
    }

    public static double elapsedMillis(long startNanos, long endNanos) {
        return (endNanos - startNanos) / 1_000_000.0;
    }
}
