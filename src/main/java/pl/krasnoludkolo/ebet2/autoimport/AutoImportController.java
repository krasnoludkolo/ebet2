package pl.krasnoludkolo.ebet2.autoimport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.krasnoludkolo.ebet2.autoimport.api.ExternalSourceConfiguration;

import java.util.Map;
import java.util.UUID;

@Controller()
@RequestMapping("api")
class AutoImportController {

    private AutoImportFacade facade;

    @Autowired
    AutoImportController(AutoImportFacade facade) {
        this.facade = facade;
    }

    @PostMapping("/autoimport")
    public ResponseEntity<UUID> initializeLeague(@RequestBody Map<String, String> configMap) {
        ExternalSourceConfiguration config = new ExternalSourceConfiguration();
        configMap.forEach(config::putParameter);
        String clientShortcut = configMap.get("shortcut");
        UUID uuid = facade.initializeLeague(config, clientShortcut);
        return new ResponseEntity<>(uuid, HttpStatus.CREATED);
    }

    @PostMapping("/autoupdate")
    public HttpStatus addLeagueToAutoUpdate(@RequestBody UUID leagueUUID) {
        facade.addLeagueToAutoUpdater(leagueUUID);
        return HttpStatus.NO_CONTENT;
    }
}
