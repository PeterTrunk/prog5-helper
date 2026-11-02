package hu.mik.pte.prog5.zh1vaadin.service;

import hu.mik.pte.prog5.zh1vaadin.entity.Pet;
import hu.mik.pte.prog5.zh1vaadin.entity.PetSpecies;
import hu.mik.pte.prog5.zh1vaadin.repository.PetRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 3. feladat: a PetService implementacioja.
 * <p>
 * A dependency injection az interface-re (PetRepository) hivatkozik, a konkret
 * implementaciot csak a bean NEVE ('mock') valasztja ki - az implementacios
 * osztaly sehol nem szerepel importkent.
 * <p>
 * A {@link SecurityService} interface-t is implementalja, hogy a 8-3. feladat
 * SpEL kifejezesebol elerheto legyen a getRolesAsString() metodus.
 */
@Service("petService")
public class PetServiceImpl implements PetService, SecurityService {

    private final PetRepository petRepository;

    public PetServiceImpl(@Qualifier("mock") PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    /**
     * 8-3. feladat (bonusz): csak azokat az allatokat adja vissza, amelyek fajahoz
     * a bejelentkezett felhasznalonak van jogosultsaga.
     * <p>
     * Tipusok: a getRolesAsString() String-eket ad vissza (ROLE_ prefix nelkul),
     * a filterObject.species viszont PetSpecies enum - ezert kell a .name() hivas.
     */
    @Override
    @PostFilter("@petService.getRolesAsString().contains(filterObject.species.name())")
    public List<Pet> listAll() {
        return this.petRepository.findAll();
    }

    /**
     * 8-2. feladat: allat regisztralasahoz 'REGISTER' jogosultsag kell.
     */
    @Override
    @PreAuthorize("hasRole('REGISTER')")
    public Pet save(Pet pet) {
        return this.petRepository.save(pet);
    }

    /**
     * 6-1. feladat: a faj lista dinamikusan a PetSpecies enumbol all elo.
     * 8-2. feladat: ez is a regisztraciohoz fuzodo metodus.
     */
    @Override
    @PreAuthorize("hasRole('REGISTER')")
    public List<PetSpecies> listSpecies() {
        return List.of(PetSpecies.values());
    }
}
