package hu.mik.pte.prog5.crud_mvc.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.PermitAll;

import java.util.Locale;

//@Route() // //vaadinbasics
//@Route("basics") // /basics
//@Route("basics/:id") // /basics/42
//@Route("basics/:id?") // /basics vagy /basics/42
//@Route("basics/:id([0-9]+)") // /basics/42 (regexxel)
@Route("basics/:id?/edit") // /basics/42/edit
//@Route("basics/:id?/edit") // /basics/edit

// @RouteAlias("") több url, ezzel pl root is ide
@RoutePrefix("admin")
@PermitAll
//@PageTitle("Cím")
public class VaadinBasics extends VerticalLayout implements BeforeEnterObserver, HasDynamicTitle {

    private final Span inputText = new Span();

    TextField textField = new TextField("Name");


    public VaadinBasics() {

        //this.add("Asd");

        Button button = new Button(this.getTranslation("button.save"));
        TextField textField = new TextField("Teszt");

        button.addClickListener(event -> {
            Notification notification = Notification.show(this.getTranslation("text.value-in-field", textField.getValue()));
        });

        ComboBox<Locale> language = new ComboBox<>();
        language.setItems(Locale.of("hu"), Locale.ENGLISH);
        language.setItemLabelGenerator(l -> l.getDisplayLanguage(l));
        language.setValue(UI.getCurrent().getLocale());

        language.addValueChangeListener(e -> {
            VaadinSession.getCurrent().setLocale(e.getValue());
            UI.getCurrent().getPage().reload();
        });


        this.add(button, textField, this.inputText, textField, language);

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        var val = event.getRouteParameters().getLong("id").orElse(null);

        this.inputText.setText("Value is " + (val == null ? "unknown" : val.toString()));
    }

    @Override
    public String getPageTitle() {
        return this.getTranslation("title");
    }
}
