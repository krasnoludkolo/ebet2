package pl.krasnoludkolo.ebet2.external;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api")
class AutoUpdateController {

    private ExternalFacade facade;

    @Autowired
    AutoUpdateController(ExternalFacade facade) {
        this.facade = facade;
    }

    @PostMapping("/autoupdate")
    public HttpStatus addLeagueToAutoUpdate(@RequestBody UUID leagueUUID) {
        facade.addLeagueToAutoUpdater(leagueUUID);
        return HttpStatus.OK;
    }

    @PostMapping("/update")
    public HttpStatus manuallyUpdateLeague(@RequestBody UUID leagueUUID) {
        facade.updateLeague(leagueUUID);
        return HttpStatus.OK;
    }
}
