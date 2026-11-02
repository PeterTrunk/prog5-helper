package hu.mik.pte.prog5.zh1vaadin.ui.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import hu.mik.pte.prog5.zh1vaadin.entity.Pet;
import hu.mik.pte.prog5.zh1vaadin.entity.PetSpecies;
import hu.mik.pte.prog5.zh1vaadin.service.PetService;
import hu.mik.pte.prog5.zh1vaadin.ui.MainLayout;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;

import java.util.UUID;

@Route(value = "register", layout = MainLayout.class)
@RolesAllowed("REGISTER")
public class RegisterView extends VerticalLayout implements HasDynamicTitle {

    private final PetService petService;

    private final Binder<Pet> binder = new Binder<>(Pet.class);
    //private final BeanValidationBinder
    private final Pet pet = new Pet();
    ComboBox<PetSpecies> species = new ComboBox<>();

    private final TextField nameField = new TextField(getTranslation("pet.name"));

    public RegisterView(PetService petService) {
        this.petService = petService;
        FormLayout form = new FormLayout();

        this.setSizeFull();
        this.setMaxWidth("900px");

        this.species.setItems(petService.listSpecies());
        this.species.setItemLabelGenerator(s -> this.getTranslation("pet.species."+s.name()));

        this.binder.forField(this.nameField).asRequired().bind(Pet::getName, Pet::setName);
        this.binder.forField(this.species).asRequired().bind(Pet::getSpecies, Pet::setSpecies);

        Button regBtn = new Button(this.getTranslation("register.submit"), e -> this.register());

        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        form.setWidth("100%");
        form.setMaxWidth("300px");
        form.add(this.nameField, this.species);
        this.add(form, regBtn);
    }

    private void register() {

        if(this.binder.writeBeanIfValid(this.pet)){
            petService.save(pet);
            UI.getCurrent().navigate(ListView.class);
        }
    }

    @Override
    public String getPageTitle(){
        return this.getTranslation("title.pet.register");
    }
}
