package hu.mik.pte.prog5.zh1vaadin.ui.view;

import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import hu.mik.pte.prog5.zh1vaadin.ui.MainLayout;

/**
 * 2-1. feladat: bejelentkezo kepernyo a Vaadin beepitett LoginForm komponensevel,
 * a '/login' utvonalon.
 * 7. feladat: lokalizalt kepernyocim.
 */
@Route(value = "login", layout = MainLayout.class)
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver, HasDynamicTitle {

    private final LoginForm loginForm = new LoginForm();

    public LoginView() {
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.getForm().setTitle(this.getTranslation("title.login"));
        this.loginForm.setI18n(i18n);

        // A Spring Security UsernamePasswordAuthenticationFilter-e ide varja a POST-ot.
        this.loginForm.setAction("login");
        this.loginForm.setForgotPasswordButtonVisible(false);

        this.setSizeFull();
        this.setAlignItems(Alignment.CENTER);
        this.setJustifyContentMode(JustifyContentMode.CENTER);
        this.add(this.loginForm);
    }

    /**
     * 2-1. feladat: sikertelen bejelentkezes utan a Spring Security a
     * '/login?error' cimre iranyit - ezt jelezzuk a felhasznalonak.
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            this.loginForm.setError(true);
        }
    }

    @Override
    public String getPageTitle() {
        return this.getTranslation("title.login");
    }
}
