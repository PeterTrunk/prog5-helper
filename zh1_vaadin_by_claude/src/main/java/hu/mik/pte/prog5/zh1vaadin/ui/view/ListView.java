package hu.mik.pte.prog5.zh1vaadin.ui.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import hu.mik.pte.prog5.zh1vaadin.entity.Pet;
import hu.mik.pte.prog5.zh1vaadin.service.PetService;
import hu.mik.pte.prog5.zh1vaadin.service.SecurityService;
import hu.mik.pte.prog5.zh1vaadin.ui.MainLayout;
import jakarta.annotation.security.PermitAll;

/**
 * 5-1. feladat: allatok listazasa Grid-del.
 * 5-2. feladat: a '/list' utvonalon, a MainLayout-ba foglalva.
 * 7. feladat: lokalizalt kepernyocim.
 */
@Route(value = "list", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class) // bejelentkezes utan a '/' is ide vezet
@PermitAll
public class ListView extends VerticalLayout implements HasDynamicTitle, SecurityService {

    private final Grid<Pet> grid = new Grid<>(Pet.class, false);

    public ListView(PetService petService) {
        // 5-1. feladat: az oszlopok sorrendje szamit - azonosito, nev, faj

        // Az azonosito oszlop csak ADMIN jogosultsagu felhasznalonak latszik
        if (this.getRolesAsString().contains("ADMIN")) {
            this.grid.addColumn(Pet::getUid).setHeader(this.getTranslation("pet.id"));
        }
        this.grid.addColumn(Pet::getName).setHeader(this.getTranslation("pet.name"));
        this.grid.addColumn(pet -> this.getTranslation("pet.species." + pet.getSpecies().name()))
                .setHeader(this.getTranslation("pet.species"));

        this.grid.setItems(petService.listAll());

        this.setSizeFull();
        this.add(this.grid);
    }

    @Override
    public String getPageTitle() {
        return this.getTranslation("title.pet.list");
    }
}
