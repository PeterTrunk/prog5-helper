package hu.mik.pte.prog5.zh1vaadin.ui.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
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

/**
 * 6-1. feladat: allat regisztralo kepernyo, bindolassal, kotelezo mezokkel.
 * 6-2. feladat: a '/register' utvonalon, a MainLayout-ba foglalva.
 * 7. feladat: lokalizalt kepernyocim.
 * 8-2. feladat: a kepernyo csak 'REGISTER' jogosultsaggal latogathato.
 */
@Route(value = "register", layout = MainLayout.class)
@RolesAllowed("REGISTER")
public class RegisterView extends VerticalLayout implements HasDynamicTitle {

    private final PetService petService;

    private final TextField name = new TextField(this.getTranslation("pet.name"));
    private final ComboBox<PetSpecies> species = new ComboBox<>(this.getTranslation("pet.species"));

    private final Binder<Pet> binder = new Binder<>(Pet.class);

    public RegisterView(PetService petService) {
        this.petService = petService;

        // 6-1. feladat: a fajlista a PetService-bol, dinamikusan a PetSpecies enum alapjan
        this.species.setItems(petService.listSpecies());
        this.species.setItemLabelGenerator(s -> this.getTranslation("pet.species." + s.name()));

        // 6-1. feladat: bindolas a form es a modell kozott, minden mezo kotelezo
        this.binder.forField(this.name).asRequired().bind("name");
        this.binder.forField(this.species).asRequired().bind("species");

        Button submit = new Button(this.getTranslation("register.submit"), e -> this.save());

        this.add(new FormLayout(this.name, this.species), submit);
    }

    private void save() {
        Pet pet = new Pet();
        if (this.binder.writeBeanIfValid(pet)) {
            this.petService.save(pet);
            UI.getCurrent().navigate(ListView.class);
        }
    }

    @Override
    public String getPageTitle() {
        return this.getTranslation("title.pet.register");
    }
}
