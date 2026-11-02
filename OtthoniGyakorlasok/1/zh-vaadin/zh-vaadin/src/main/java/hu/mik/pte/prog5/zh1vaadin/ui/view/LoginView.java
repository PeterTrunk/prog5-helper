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

@Route(value = "login")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver, HasDynamicTitle {

    /*
    Az alkalmazás jelenleg az alapértelmezett SpringSecurity login képernyőt használja. Készítse el a
    LoginView osztályt. Ehhez a Vaadin beépített bejelentkező komponensét használja! Állítsa be, hogy
    bejelentkezés ezen a képernyőn keresztül történjen. A bejelentkezési oldal legyen a ‘/login’
    útvonalon. Figyeljen arra, hogy hiba esetén kerüljön jelölésre, hogy hiba történt!
     */
    private final LoginForm loginForm = new LoginForm();

    public LoginView() {
        this.loginForm.setAction("login");
        this.loginForm.setForgotPasswordButtonVisible(false);

        this.add(this.loginForm);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            //TODO hibakezelés:
            this.loginForm.setError(true);
        }
    }

    @Override
    public String getPageTitle(){
        return this.getTranslation("title.login");
    }
}
