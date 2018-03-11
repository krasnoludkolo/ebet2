package pl.krasnoludkolo.ebet2.domain.results;

import pl.krasnoludkolo.ebet2.domain.results.api.UserResultDTO;

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

    int getPoints() {
        return pointCounter.getCount();
    }

    String getName() {
        return name;
    }

    public UserResultDTO toDTO() {
        return new UserResultDTO(name, pointCounter.getCount());

    }
}
