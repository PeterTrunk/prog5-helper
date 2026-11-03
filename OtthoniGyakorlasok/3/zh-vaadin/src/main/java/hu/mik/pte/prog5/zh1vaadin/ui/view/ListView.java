package hu.mik.pte.prog5.zh1vaadin.ui.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.security.AuthenticationContext;
import hu.mik.pte.prog5.zh1vaadin.entity.Pet;
import hu.mik.pte.prog5.zh1vaadin.service.PetService;
import hu.mik.pte.prog5.zh1vaadin.service.SecurityService;
import hu.mik.pte.prog5.zh1vaadin.ui.MainLayout;
import jakarta.annotation.security.PermitAll;

@Route(value = "list", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class ListView extends VerticalLayout implements HasDynamicTitle {

    private final Grid<Pet> grid = new Grid<>(Pet.class,false);
    private final AuthenticationContext authenticationContext;
    private final SecurityService securityService;
    private final PetService petService;

    public ListView(AuthenticationContext authenticationContext, SecurityService securityService, PetService petService){
        this.securityService = securityService;
        this.authenticationContext = authenticationContext;
        this.petService = petService;

        this.setSizeFull();

        if(securityService.getRolesAsString().contains("ADMIN")){
            this.grid.addColumn(Pet::getUid).setHeader(this.getTranslation("pet.id"));
        }
        this.grid.addColumn(Pet::getName).setHeader(this.getTranslation("pet.name"));
        this.grid.addColumn(Pet::getSpecies).setHeader(this.getTranslation("pet.species"));

        this.add(grid);
        this.grid.setItems(this.petService.listAll());
    }

    @Override
    public String getPageTitle() {
        return this.getTranslation("title.pet.list");
    }
}
