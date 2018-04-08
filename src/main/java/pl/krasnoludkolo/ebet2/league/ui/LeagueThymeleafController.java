package pl.krasnoludkolo.ebet2.league.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.LeagueDetailsDTO;

import java.util.List;

@Controller
class LeagueThymeleafController {

    private final LeagueFacade leagueFacade;

    @Autowired
    LeagueThymeleafController(LeagueFacade leagueFacade) {
        this.leagueFacade = leagueFacade;
    }

    @GetMapping("/leagues")
    public ModelAndView getAllLeaguesList() {
        List<LeagueDetailsDTO> leagueList = leagueFacade.getAllLeaguesDetails().asJava();
        ModelAndView modelAndView = new ModelAndView("leagues");
        modelAndView.addObject("leagueList", leagueList);
        return modelAndView;
    }

}
