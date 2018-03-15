package pl.krasnoludkolo.ebet2.results.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResultDTO {

    private final String name;
    private final int pointCounter;

}
