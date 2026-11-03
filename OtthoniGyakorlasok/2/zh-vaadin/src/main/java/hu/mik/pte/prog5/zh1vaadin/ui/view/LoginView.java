package hu.mik.pte.prog5.zh1vaadin.ui.view;

import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;

@Route(value = "login")
public class LoginView extends VerticalLayout implements BeforeEnterObserver, HasDynamicTitle {

    private final LoginForm loginForm = new LoginForm();

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            this.loginForm.setError(true);
        }
    }

    public LoginView(){
        this.loginForm.setAction("login");
        this.loginForm.setForgotPasswordButtonVisible(false);

        setAlignItems(Alignment.CENTER);

        this.add(this.loginForm);
    }

    @Override
    public String getPageTitle() {
        return this.getTranslation("title.login");
    }
}
