package hu.mik.pte.prog5.crud_mvc.service;

import hu.mik.pte.prog5.crud_mvc.model.Car;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class CarService {

    private final Map<Long, Car> carList = new HashMap<Long, Car>();
    private final AtomicLong idSeq = new AtomicLong(0);

    private final DataService dataService;

    @PostConstruct
    private void init() {
        this.dataService.getValues().stream().forEach(item -> {
            this.save(this.create(item.getMake(), item.getModel(), item.getManufactureYear()));
        });

//        this.save(this.create("Skoda", "Octavia", 2021));
//        this.save(this.create("Suzuki", "Swift", 2020));

    }

    public Car create(String make, String model, Integer manufactureYear) {
        Car car = new Car();
        car.setMake(make);
        car.setModel(model);
        car.setManufactureYear(manufactureYear);
        car.setOwner("user");
        return car;
    }

    public Car save(Car car) {
        if (car.getId() == null) {
            car.setId(this.idSeq.incrementAndGet());
        }
        this.carList.put(car.getId(), car);
        return car;
    }

    //    @PreAuthorize("hasRole('ADMIN')")
//    @PostAuthorize("hasRole('ADMIN')")
//    @PreFilter("hasRole('ADMIN')")
//    @PostFilter("filterObject.owner == authentication.name or hasRole('ADMIN') or hasAuthority('EDIT_FORM')")
//    @Secured("ROLE_ADMIN")
//    @RolesAllowed("ADMIN")
//    @PreAuthorize("hasRole('ADMIN')")
    public Collection<Car> list() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        var authorities = auth.getAuthorities();

        return new ArrayList<>(this.carList.values());
    }

    public Car findById(Long id) {
        return this.carList.get(id);
    }

    public void deleteById(Long id) {
        this.carList.remove(id);
    }
}
