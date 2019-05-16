package pl.krasnoludkolo.ebet2.results;

import io.haste.TimeSource;
import io.vavr.collection.List;
import pl.krasnoludkolo.ebet2.external.ExternalFacade;
import pl.krasnoludkolo.ebet2.external.api.MatchInfo;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.league.api.MatchDTO;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

final class ResultsUpdater {

    private final ScheduledExecutorService executorService;
    private final Repository<UpdateDetails> repository;
    private final ExternalFacade externalFacade;
    private final TimeSource timeSource;
    private final ResultFacade resultFacade;

    ResultsUpdater(ScheduledExecutorService executorService, Repository<UpdateDetails> repository, ExternalFacade externalFacade, TimeSource timeSource, ResultFacade resultFacade) {
        this.executorService = executorService;
        this.repository = repository;
        this.externalFacade = externalFacade;
        this.timeSource = timeSource;
        this.resultFacade = resultFacade;
    }

    MatchDTO schedule(MatchDTO match) {
        Duration offset = Duration.between(match.getMatchStartDate(), timeSource.now()).plusHours(2);
        executorService.schedule(() -> tryUpdate(match), offset.getSeconds(), TimeUnit.SECONDS);
        return match;
    }

    private void reschedule(MatchDTO match) {
        Duration offset = Duration.between(timeSource.now(), timeSource.now().plusMinutes(1));
        executorService.schedule(() -> tryUpdate(match), offset.getSeconds(), TimeUnit.SECONDS);
    }

    private void tryUpdate(MatchDTO match) {
        externalFacade.downloadLeague(match.getLeagueUUID())
                .map(list -> {
                    MatchInfo info = findCorrespondingMatch(match, list);
                    if (info.isFinished()) {
                        resultFacade.registerMatchResult(match.getUuid(), info.getResult());
                    } else {
                        reschedule(match);

                    }
                    return list;
                });
    }

    private MatchInfo findCorrespondingMatch(MatchDTO matchDTO, List<MatchInfo> matchInfoList) {
        return matchInfoList
                .find(matchInfo -> correspondingMatch(matchDTO, matchInfo))
                .getOrElseThrow(IllegalStateException::new);
    }

    private boolean correspondingMatch(MatchDTO matchDTO, MatchInfo matchInfo) {
        return isSameMatch(matchDTO, matchInfo);
    }

    private boolean isSameMatch(MatchDTO matchDTO, MatchInfo matchInfo) {
        return Objects.equals(matchDTO.getHost(), matchInfo.getHostName())
                && Objects.equals(matchDTO.getGuest(), matchInfo.getGuestName())
                && matchDTO.getRound() == matchInfo.getRound();
    }


}
