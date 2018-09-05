package pl.krasnoludkolo.ebet2.results.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class UserResultDTO implements Comparable<UserResultDTO> {

    private final String name;
    private final int pointCounter;

    @Override
    public int compareTo(UserResultDTO other) {
        return Integer.compare(pointCounter, other.pointCounter);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserResultDTO that = (UserResultDTO) o;
        return pointCounter == that.pointCounter &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, pointCounter);
    }
}
