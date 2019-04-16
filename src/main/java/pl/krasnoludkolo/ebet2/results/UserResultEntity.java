package pl.krasnoludkolo.ebet2.results;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
class UserResultEntity {

    @Id
    private String uuid;
    private UUID userUUID;
    private int pointCounter;
    @ManyToOne(fetch = FetchType.EAGER)
    private RoundResultsEntity roundResultsEntity;

    UserResultEntity(UUID userUUID, int pointCounter, RoundResultsEntity roundResultsEntity) {
        this.userUUID = userUUID;
        this.pointCounter = pointCounter;
        this.roundResultsEntity = roundResultsEntity;
        uuid = roundResultsEntity.getUuid() + "_" + userUUID;
    }
}
