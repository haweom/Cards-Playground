package pl.edu.pja.cardsplayground2.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import pl.edu.pja.cardsplayground2.controllers.html.HtmlController;

@Configuration
public class AppStartupConfig {
    private final HtmlController htmlController;

    @Autowired
    public AppStartupConfig(HtmlController htmlController) {
        this.htmlController = htmlController;
    }

    @PostConstruct
    public void init(){
        htmlController.dummyUsers();
    }
}
