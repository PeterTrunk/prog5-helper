package hu.mik.pte.prog5.zh1vaadin.ui;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.security.AuthenticationContext;
import hu.mik.pte.prog5.zh1vaadin.ui.view.ListView;
import hu.mik.pte.prog5.zh1vaadin.ui.view.RegisterView;

/**
 * 4. feladat: fejlec a bejelentkezett felhasznalo nevevel, a kepernyok kozotti
 * menuvel es egy kijelentkezes gombbal.
 * 8-2. feladat: a regisztracios menupont csak 'REGISTER' jogosultsaggal latszik.
 * <p>
 * A felepites a Gyak6 (crud-mvc-vaadin) MainLayout-jat koveti:
 * VerticalLayout + RouterLayout, kulon 'content' Div-vel es createHeader() metodussal.
 */
@AnonymousAllowed
public class MainLayout extends VerticalLayout implements RouterLayout {

    private final Div content = new Div();
    private final AuthenticationContext authenticationContext;

    public MainLayout(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;

        this.setSizeFull();
        this.setMaxWidth("900px");
        this.getStyle().set("margin", "0 auto");

        this.content.setWidthFull();
        this.add(this.createHeader(), this.content);
        this.setFlexGrow(1, this.content);
    }

    private Header createHeader() {
        HorizontalLayout bar = new HorizontalLayout();
        bar.setWidthFull();
        bar.setAlignItems(Alignment.CENTER);

        // A bejelentkezo kepernyon nincs ertelme menunek es kijelentkezesnek
        if (this.authenticationContext.isAuthenticated()) {
            HorizontalLayout menu = new HorizontalLayout(
                    new RouterLink(this.getTranslation("header.menu.pet.list"), ListView.class));

            // 8-2. feladat: a regisztracios menupont jogosultsaghoz kotott
            if (this.authenticationContext.hasRole("REGISTER")) {
                menu.add(new RouterLink(this.getTranslation("header.menu.pet.register"),
                        RegisterView.class));
            }
            bar.add(menu);
            bar.expand(menu);

            String username = this.authenticationContext.getPrincipalName().orElse("");
            Button logout = new Button(this.getTranslation("header.menu.logout"),
                    e -> this.authenticationContext.logout());
            bar.add(new Span(this.getTranslation("header.username") + " " + username), logout);
        }

        Header header = new Header(bar);
        header.setWidthFull();
        return header;
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        this.content.removeAll();
        if (content != null) {
            this.content.getElement().appendChild(content.getElement());
        }
    }
}
