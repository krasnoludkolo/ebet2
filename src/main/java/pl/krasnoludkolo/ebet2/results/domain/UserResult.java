package pl.krasnoludkolo.ebet2.results.domain;

import pl.krasnoludkolo.ebet2.results.api.UserResultDTO;

import java.util.Objects;

class UserResult {

    private final String name;
    private final PointCounter pointCounter;

    UserResult(String name) {
        this.name = name;
        pointCounter = new PointCounter();
    }

    void addPoint() {
        pointCounter.addPoint();
    }

    String getName() {
        return name;
    }

    public UserResultDTO toDTO() {
        return new UserResultDTO(name, pointCounter.getCount());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserResult that = (UserResult) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(pointCounter, that.pointCounter);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, pointCounter);
    }
}
