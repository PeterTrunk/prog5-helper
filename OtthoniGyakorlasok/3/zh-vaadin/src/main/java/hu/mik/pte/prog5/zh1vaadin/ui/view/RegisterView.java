package hu.mik.pte.prog5.zh1vaadin.ui.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import hu.mik.pte.prog5.zh1vaadin.entity.Pet;
import hu.mik.pte.prog5.zh1vaadin.entity.PetSpecies;
import hu.mik.pte.prog5.zh1vaadin.service.PetService;
import hu.mik.pte.prog5.zh1vaadin.ui.MainLayout;
import jakarta.annotation.security.RolesAllowed;


@Route(value = "register", layout = MainLayout.class)
@RolesAllowed("REGISTER")
public class RegisterView extends VerticalLayout implements HasDynamicTitle {

    private final PetService petService;

    private final Binder<Pet> binder = new Binder<>(Pet.class);

    ComboBox<PetSpecies> species = new ComboBox<>();

    private final TextField name = new TextField(getTranslation("pet.name"));

    private final Pet pet = new Pet();

    public RegisterView(PetService petService) {
        this.petService = petService;

        this.setSizeFull();
        this.setAlignItems(Alignment.CENTER);

        this.species.setItems(petService.listSpecies());
        this.species.setItemLabelGenerator(s-> this.getTranslation("pet.species." + s.name()));

        binder.forField(this.name).asRequired().bind(Pet::getName, Pet::setName);
        binder.forField(this.species).asRequired().bind(Pet::getSpecies, Pet::setSpecies);

        Button reg = new Button(this.getTranslation("register.submit"), buttonClickEvent -> this.register());

        this.add(this.name,this.species,reg);
    }

    private void register() {
        if(this.binder.writeBeanIfValid(this.pet)){
            this.petService.save(this.pet);
            UI.getCurrent().navigate(ListView.class);
        }

    }

    @Override
    public String getPageTitle() {
        return this.getTranslation("title.pet.register");
    }
}
