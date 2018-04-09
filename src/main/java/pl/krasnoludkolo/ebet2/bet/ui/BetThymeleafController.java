package pl.krasnoludkolo.ebet2.bet.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import pl.krasnoludkolo.ebet2.bet.BetFacade;
import pl.krasnoludkolo.ebet2.bet.api.NewBetDTO;

import javax.validation.Valid;
import java.util.UUID;

@Controller
class BetThymeleafController {

    private final BetFacade betFacade;

    @Autowired
    BetThymeleafController(BetFacade betFacade) {
        this.betFacade = betFacade;
    }

    @GetMapping("/league/{leagueUUID}/match/{matchUUID}/newbet")
    public ModelAndView getNewBetPage() {
        ModelAndView modelAndView = new ModelAndView("newbet");
        modelAndView.addObject("newBet", new NewBetDTO());
        return modelAndView;
    }

    @PostMapping("/league/{leagueUUID}/match/{matchUUID}/newbet")
    public String addNewBet(@ModelAttribute("newBet") @Valid NewBetDTO newBet, BindingResult bindingResult, @PathVariable UUID matchUUID, @PathVariable UUID leagueUUID) {
        NewBetDTO bet = new NewBetDTO(newBet.getBetTyp(), newBet.getUsername(), matchUUID);
        betFacade.addBetToMatch(bet);
        return "redirect:/league/" + leagueUUID.toString();
    }
}
