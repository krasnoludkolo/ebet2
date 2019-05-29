package pl.krasnoludkolo.ebet2.results;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
class UpdateDetailsEntity {

    @Id
    private UUID matchUUID;
    private LocalDateTime scheduledTime;
    private int attempt;

}