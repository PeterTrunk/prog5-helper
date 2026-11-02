package hu.mik.pte.prog5.zh1vaadin.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.security.AuthenticationContext;
import hu.mik.pte.prog5.zh1vaadin.ui.view.ListView;
import hu.mik.pte.prog5.zh1vaadin.ui.view.RegisterView;

@AnonymousAllowed
public class MainLayout extends AppLayout implements RouterLayout {
    private final AuthenticationContext authenticationContext;

    public MainLayout(AuthenticationContext authenticationContext){
        this.authenticationContext = authenticationContext;

        HorizontalLayout bar = new HorizontalLayout();
        bar.setWidthFull();
        bar.setSizeFull();
        bar.getStyle().set("margin", "0 auto");
        bar.setAlignItems(FlexComponent.Alignment.CENTER);
        if(this.authenticationContext.isAuthenticated()){
            String username = this.authenticationContext.getPrincipalName().orElse("");
            Button logoutBtn = new Button(this.getTranslation("header.menu.logout"), e -> this.authenticationContext.logout());
            Button listBtn = new Button(this.getTranslation("header.menu.pet.list"), e -> UI.getCurrent().navigate(ListView.class));

            Button regBtn = new Button(this.getTranslation("header.menu.pet.register"), e -> UI.getCurrent().navigate(RegisterView.class));
            bar.add(new Span(this.getTranslation("header.username") + " "+username), logoutBtn, listBtn, regBtn);
        }

        addToNavbar(bar);
    }
}
