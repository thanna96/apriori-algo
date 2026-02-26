public class Timer {

    private long startTime;
    private long endTime;

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public void stop() {
        endTime = System.currentTimeMillis();
    }

    public long getElapsedMillis() {
        return endTime - startTime;
    }

    @Override
    public String toString() {
        return getElapsedMillis() + " ms";
    }
}
