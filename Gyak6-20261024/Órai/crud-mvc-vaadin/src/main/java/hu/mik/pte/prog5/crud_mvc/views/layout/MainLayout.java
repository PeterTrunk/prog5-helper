package hu.mik.pte.prog5.crud_mvc.views.layout;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.security.AuthenticationContext;
import hu.mik.pte.prog5.crud_mvc.views.CarListView;

import java.util.Locale;

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
        this.add(this.createHeader(), this.content, this.createFooter());
        this.setFlexGrow(1, this.content);
    }

    private Header createHeader(){
        H3 title = new H3(new RouterLink(this.getTranslation("title"), CarListView.class));

        ComboBox<Locale> language = new ComboBox<>();
        language.setItems(Locale.of("hu"), Locale.of("en"));
        language.setItemLabelGenerator(l -> l.getDisplayLanguage(this.getLocale()));
        language.setValue(UI.getCurrent().getLocale());

        language.addValueChangeListener(e -> {
            VaadinSession.getCurrent().setLocale(e.getValue());
            UI.getCurrent().getPage().reload();
        });


        HorizontalLayout bar = new HorizontalLayout(title,language);
        bar.setWidthFull();
        bar.setAlignItems(Alignment.CENTER);
        bar.expand(title);
        if (this.authenticationContext.isAuthenticated()) {
            String username = this.authenticationContext.getPrincipalName().orElse("");
            Button logout = new Button(this.getTranslation("header.logout"), e -> this.authenticationContext.logout());
            bar.add(new Span(username), logout);
        }

        Header header = new Header(bar);
        header.setWidthFull();
        return header;
    }
    private Footer createFooter() {
        Footer footer = new Footer(new Span(this.getTranslation("app.footer")));
        footer.getStyle().set("color", "var(--lumo-secondary-text-color)");
        return footer;
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        this.content.removeAll();
        if (content != null) {
            this.content.getElement().appendChild(content.getElement());
        }
    }
}
