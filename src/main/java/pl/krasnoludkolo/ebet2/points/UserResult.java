package pl.krasnoludkolo.ebet2.points;

import pl.krasnoludkolo.ebet2.results.api.UserResultDTO;

import java.util.Objects;
import java.util.UUID;

class UserResult implements Comparable<UserResult> {

    private final UUID userUUID;
    private final PointCounter pointCounter;


    static UserResult fromEntity(UserResultEntity entity) {
        UUID userUUID = entity.getUserUUID();
        PointCounter counter = PointCounter.withCount(entity.getPointCounter());
        return new UserResult(userUUID, counter);
    }

    static UserResult create(UUID userUUID) {
        return new UserResult(userUUID, PointCounter.create());
    }

    private UserResult(UUID userUUID, PointCounter pointCounter) {
        this.userUUID = userUUID;
        this.pointCounter = pointCounter;
    }

    UserResult addPoint() {
        return new UserResult(userUUID, pointCounter.addPoint());
    }

    boolean hasUUID(UUID id) {
        return Objects.equals(this.userUUID, id);
    }

    int getUserPointResult() {
        return pointCounter.getCount();
    }

    UserResultDTO toDTO() {
        return new UserResultDTO(userUUID, pointCounter.getCount());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserResult that = (UserResult) o;
        return Objects.equals(userUUID, that.userUUID) &&
                Objects.equals(pointCounter, that.pointCounter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userUUID, pointCounter);
    }

    UserResultEntity toEntity() {
        return new UserResultEntity(userUUID, pointCounter.getCount());
    }

    @Override
    public int compareTo(UserResult userResult) {
        return Integer.compare(userResult.pointCounter.getCount(), pointCounter.getCount());
    }

}
