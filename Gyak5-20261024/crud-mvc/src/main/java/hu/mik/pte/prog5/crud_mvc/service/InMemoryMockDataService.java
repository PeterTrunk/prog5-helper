package hu.mik.pte.prog5.crud_mvc.service;

import hu.mik.pte.prog5.crud_mvc.model.Car;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service(value = "mock")
@Profile("mock")
public class InMemoryMockDataService implements DataService {
    @Override
    public Collection<Car> getValues() {
        return List.of(
                new Car("Ford", "Focus", 2021),
                new Car("Ford", "Fiesta", 2020),
                new Car("Ford", "Diego", 2022));
    }
}
