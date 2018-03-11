package pl.krasnoludkolo.ebet2.domain.results;

class PointCounter {

    private int count = 0;

    void addPoint() {
        count++;
    }

    int getCount() {
        return count;
    }

}
