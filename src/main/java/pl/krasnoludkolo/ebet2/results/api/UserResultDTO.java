package pl.krasnoludkolo.ebet2.results.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResultDTO implements Comparable<UserResultDTO> {

    private final String name;
    private final int pointCounter;

    @Override
    public int compareTo(UserResultDTO other) {
        return Integer.compare(pointCounter, other.pointCounter);
    }
}
