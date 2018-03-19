package pl.krasnoludkolo.ebet2.results;

import java.util.Objects;

class PointCounter {

    private int count;

    static PointCounter withCount(int count) {
        return new PointCounter(count);
    }

    static PointCounter create() {
        return new PointCounter(0);
    }

    private PointCounter(int count) {
        this.count = count;
    }

    void addPoint() {
        count++;
    }

    int getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointCounter that = (PointCounter) o;
        return count == that.count;
    }

    @Override
    public int hashCode() {

        return Objects.hash(count);
    }
}
