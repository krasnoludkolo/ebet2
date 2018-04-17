package pl.krasnoludkolo.ebet2.results;

import pl.krasnoludkolo.ebet2.results.api.UserResultDTO;

import java.util.Objects;

class UserResult implements Comparable<UserResult> {

    private final String name;
    private final PointCounter pointCounter;


    static UserResult fromEntity(UserResultEntity entity) {
        String name = entity.getName();
        PointCounter counter = PointCounter.withCount(entity.getPointCounter());
        return new UserResult(name, counter);
    }

    static UserResult create(String name) {
        return new UserResult(name, PointCounter.create());
    }

    private UserResult(String name, PointCounter pointCounter) {
        this.name = name;
        this.pointCounter = pointCounter;
    }

    void addPoint() {
        pointCounter.addPoint();
    }

    boolean hasName(String name) {
        return Objects.equals(this.name, name);
    }

    UserResultDTO toDTO() {
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

    public UserResultEntity toEntity(LeagueResultsEntity entity) {
        return new UserResultEntity(name, pointCounter.getCount(), entity);
    }

    @Override
    public int compareTo(UserResult userResult) {
        return Integer.compare(userResult.pointCounter.getCount(), pointCounter.getCount());
    }

}
