package pl.krasnoludkolo.ebet2.results;

import io.vavr.collection.Map;
import io.vavr.collection.TreeMap;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.infrastructure.error.ResponseResolver;
import pl.krasnoludkolo.ebet2.results.api.RegisterLeagueDTO;
import pl.krasnoludkolo.ebet2.results.api.ResultError;

import java.util.UUID;

@RestController
@RequestMapping("api/register")
public class LeagueRegistrationController {


    private final ResultFacade resultFacade;

    @Autowired
    LeagueRegistrationController(ResultFacade resultFacade) {
        this.resultFacade = resultFacade;
    }

    @PostMapping("/league")
    public ResponseEntity registerLeague(@RequestBody RegisterLeagueDTO registerLeagueDTO) {
        String leagueName = registerLeagueDTO.getLeagueName();

        ExternalSourceConfiguration config = createConfig(registerLeagueDTO);
        Either<ResultError, UUID> uuid = resultFacade.registerLeague(leagueName, config);

        return ResponseResolver.resolve(uuid);
    }

    private ExternalSourceConfiguration createConfig(RegisterLeagueDTO registerLeagueDTO) {
        Map<String, String> configMap = TreeMap.ofAll(registerLeagueDTO.getConfig());
        String clientShortcut = registerLeagueDTO.getClientShortcut();
        return new ExternalSourceConfiguration(configMap, clientShortcut);
    }

}
