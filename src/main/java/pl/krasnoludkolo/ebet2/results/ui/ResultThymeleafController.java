package pl.krasnoludkolo.ebet2.results.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import pl.krasnoludkolo.ebet2.league.api.LeagueNotFound;
import pl.krasnoludkolo.ebet2.results.ResultFacade;
import pl.krasnoludkolo.ebet2.results.api.UserResultDTO;

import java.util.List;
import java.util.UUID;

@Controller
class ResultThymeleafController {

    private final ResultFacade resultFacade;

    @Autowired
    ResultThymeleafController(ResultFacade resultFacade) {
        this.resultFacade = resultFacade;
    }


    @GetMapping("/league/{uuid}/results")
    public ModelAndView getAllResultsInLeague(@PathVariable UUID uuid) {
        List<UserResultDTO> userResultList = resultFacade.getResultsForLeague(uuid).getOrElseThrow(LeagueNotFound::new).getUserResultDTOS();
        ModelAndView modelAndView = new ModelAndView("leagueResults");
        modelAndView.addObject("userResultList", userResultList);
        return modelAndView;
    }


}
