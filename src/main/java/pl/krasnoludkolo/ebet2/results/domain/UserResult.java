package pl.krasnoludkolo.ebet2.results.domain;

import pl.krasnoludkolo.ebet2.results.api.UserResultDTO;

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
}
