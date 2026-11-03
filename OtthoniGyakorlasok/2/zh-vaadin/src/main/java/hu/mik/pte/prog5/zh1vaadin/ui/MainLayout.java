package hu.mik.pte.prog5.zh1vaadin.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.spring.security.AuthenticationContext;
import hu.mik.pte.prog5.zh1vaadin.service.SecurityService;
import hu.mik.pte.prog5.zh1vaadin.ui.view.ListView;
import hu.mik.pte.prog5.zh1vaadin.ui.view.RegisterView;
import jakarta.annotation.security.PermitAll;

@PermitAll
public class MainLayout extends AppLayout {

    private final AuthenticationContext authenticationContext;

    public MainLayout(AuthenticationContext authenticationContext, SecurityService securityService){
        this.authenticationContext = authenticationContext;

        HorizontalLayout bar = new HorizontalLayout();
        bar.setWidthFull();
        bar.getStyle().set("margin", "auto");
        bar.setAlignItems(FlexComponent.Alignment.CENTER);

        if(authenticationContext.isAuthenticated()){
            Span user = new Span(getTranslation("header.username") + " " + authenticationContext.getPrincipalName().orElse(""));
            Button logOutBtn = new Button(this.getTranslation("header.menu.logout") , buttonClickEvent -> this.authenticationContext.logout());
            Button listBtn = new Button(this.getTranslation("header.menu.pet.list"), e -> UI.getCurrent().navigate(ListView.class));

            bar.add(user, listBtn, logOutBtn);
            if(authenticationContext.hasRole("REGISTER")){
                Button regBtn = new Button(this.getTranslation("header.menu.pet.register"), buttonClickEvent -> UI.getCurrent().navigate(RegisterView.class));
                bar.add(regBtn);
            }
        }

        addToNavbar(bar);
    }



}
