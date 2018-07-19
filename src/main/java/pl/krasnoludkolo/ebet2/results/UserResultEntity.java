package pl.krasnoludkolo.ebet2.results;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@NoArgsConstructor
@Getter
class UserResultEntity {

    @Id
    private String uuid;
    private String name;
    private int pointCounter;
    @ManyToOne(fetch = FetchType.EAGER)
    private RoundResultsEntity roundResultsEntity;

    UserResultEntity(String name, int pointCounter, RoundResultsEntity roundResultsEntity) {
        this.name = name;
        this.pointCounter = pointCounter;
        this.roundResultsEntity = roundResultsEntity;
        uuid = roundResultsEntity.getUuid() + "_" + name;
    }
}
