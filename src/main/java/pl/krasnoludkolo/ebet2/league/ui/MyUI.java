package pl.krasnoludkolo.ebet2.league.ui;

import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import io.vavr.collection.List;
import org.springframework.beans.factory.annotation.Autowired;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.LeagueDetailsDTO;

import javax.servlet.annotation.WebServlet;

@SpringUI
public class MyUI extends UI {

    private LeagueFacade leagueFacade;

    @Autowired
    public MyUI(LeagueFacade leagueFacade) {
        this.leagueFacade = leagueFacade;
    }


    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();
        List<LeagueDetailsDTO> allLeagues = leagueFacade.getAllLeaguesDetails();

        allLeagues.map(LeagueDetailsDTO::getName).forEach(s -> layout.addComponentsAndExpand(new Label(s)));

        layout.setHeight("300px");

        setContent(layout);
    }

    @WebServlet(urlPatterns = "/", name = "MyUIServlet", asyncSupported = true)
    public static class MyUIServlet extends SpringVaadinServlet {
    }
}
