package pl.krasnoludkolo.ebet2.external;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

class AutoUpdaterScheduler {

    private static final Logger LOGGER = Logger.getLogger(ExternalFacade.class.getName());

    private final ExternalFacade externalFacade;
    private List<UUID> leaguesToUpdate = new ArrayList<>();

    AutoUpdaterScheduler(ExternalFacade externalFacade) {
        this.externalFacade = externalFacade;
        long hoursToNight = calculateHoursToNight(LocalTime.now());
//        LOGGER.log(Level.INFO, "hoursToNight: " + hoursToNight);
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4);
        scheduledExecutorService.scheduleAtFixedRate(this::updateAllLeagues, hoursToNight, 24, TimeUnit.HOURS);
    }

    private long calculateHoursToNight(LocalTime now) {
        LocalTime night = LocalTime.of(1, 0);
        return 24 - Duration.between(night, now).toHours();
    }

    void addLeagueToAutoUpdate(UUID leagueUUID) {
        LOGGER.log(Level.INFO, "Add league to auto update with UUID: " + leagueUUID.toString());
        leaguesToUpdate.add(leagueUUID);
    }

    private void updateAllLeagues() {
        LOGGER.log(Level.INFO, "Start league auto-update: ");
        for (UUID leagueUUID : leaguesToUpdate) {
            LOGGER.log(Level.INFO, "Start updating league with UUID: " + leagueUUID.toString());
            externalFacade.updateLeague(leagueUUID);
            LOGGER.log(Level.INFO, "Update finished");
        }
    }


}
