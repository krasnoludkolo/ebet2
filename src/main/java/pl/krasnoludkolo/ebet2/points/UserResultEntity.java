package pl.krasnoludkolo.ebet2.points;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
class UserResultEntity {

    @Id
    private UUID uuid;
    private UUID userUUID;
    private int pointCounter;

    UserResultEntity(UUID userUUID, int pointCounter) {
        this.userUUID = userUUID;
        this.pointCounter = pointCounter;
        uuid = UUID.randomUUID();
    }
}
