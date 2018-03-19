package pl.krasnoludkolo.ebet2.results;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@AllArgsConstructor
@Getter
class UserResultEntity {

    @Id
    private String name;
    private int pointCounter;
    @ManyToOne
    private LeagueResultsEntity leagueResultsEntity;

}
