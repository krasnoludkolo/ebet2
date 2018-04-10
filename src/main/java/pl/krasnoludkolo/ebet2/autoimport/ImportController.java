package pl.krasnoludkolo.ebet2.autoimport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.krasnoludkolo.ebet2.autoimport.api.ExternalSourceConfiguration;

import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("api")
class ImportController {

    private final AutoImportFacade autoImportFacade;

    @Autowired
    ImportController(AutoImportFacade autoImportFacade) {
        this.autoImportFacade = autoImportFacade;
    }

    //TODO async
    @PostMapping("/import")
    public HttpEntity<UUID> importLeague(@RequestBody Map config) {
        String clientShortcut = (String) config.get("clientShortcut");
        Map<String, String> clientConfig = (Map<String, String>) config.get("clientConfig");
        String leagueId = clientConfig.get("leagueId");
        String name = clientConfig.get("name");
        ExternalSourceConfiguration externalSourceConfiguration = new ExternalSourceConfiguration();
        externalSourceConfiguration.putParameter("leagueId", leagueId);
        externalSourceConfiguration.putParameter("name", name);
        UUID leagueUUID = autoImportFacade.initializeLeague(externalSourceConfiguration, clientShortcut);
        return new ResponseEntity<>(leagueUUID, HttpStatus.OK);
    }


}
