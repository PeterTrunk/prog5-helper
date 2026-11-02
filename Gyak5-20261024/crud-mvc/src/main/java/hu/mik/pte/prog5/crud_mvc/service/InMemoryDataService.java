package hu.mik.pte.prog5.crud_mvc.service;

import hu.mik.pte.prog5.crud_mvc.model.Car;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service(value = "plain")
@Profile("plain")
public class InMemoryDataService implements DataService {
    @Override
    public Collection<Car> getValues() {
        return List.of(
                new Car("Skoda", "Octavia", 2021),
                new Car("Suzuki", "Swift", 2020),
                new Car("Toyota", "Corolla", 2022));
    }
}
