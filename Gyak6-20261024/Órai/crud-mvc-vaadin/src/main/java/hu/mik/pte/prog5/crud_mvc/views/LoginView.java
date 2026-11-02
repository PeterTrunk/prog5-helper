package hu.mik.pte.prog5.crud_mvc.views;

import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import hu.mik.pte.prog5.crud_mvc.views.layout.MainLayout;

@Route(value = "login", layout = MainLayout.class)
@AnonymousAllowed
public class LoginView extends VerticalLayout implements  HasDynamicTitle {

    private final LoginForm loginForm = new LoginForm();

    public LoginView() {
        this.loginForm.setAction("login");
        this.loginForm.setForgotPasswordButtonVisible(false);

        this.add(this.loginForm);
    }

    @Override
    public String getPageTitle() {
        return this.getTranslation("login.title");
    }

}
