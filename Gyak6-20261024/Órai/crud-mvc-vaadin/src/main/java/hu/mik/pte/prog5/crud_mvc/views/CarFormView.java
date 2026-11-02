package hu.mik.pte.prog5.crud_mvc.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.*;
import hu.mik.pte.prog5.crud_mvc.model.Car;
import hu.mik.pte.prog5.crud_mvc.service.CarService;
import hu.mik.pte.prog5.crud_mvc.views.layout.MainLayout;
import jakarta.annotation.security.PermitAll;

@Route(value = "cars/new", layout = MainLayout.class)
@RouteAlias(value = "cars/:id/edit", layout = MainLayout.class)
@PermitAll
public class CarFormView extends VerticalLayout implements BeforeEnterObserver, HasDynamicTitle {
    private final CarService carService;

    private final BeanValidationBinder<Car> binder = new BeanValidationBinder<>(Car.class);
    //    private final Binder<Car> binder = new Binder<>(Car.class);

    private Car car;

    private final H1 title = new H1(getPageTitle());
    private final TextField make = new TextField(getTranslation("car.make"));
    private final TextField model = new TextField(getTranslation("car.model"));
    private final IntegerField manufactureYear = new IntegerField(getTranslation("car.manufactureYear"));


    public CarFormView(CarService carService) {
        this.carService = carService;

        this.binder.forField(make).bind("make");
//        this.binder.forField(make).bind(Car::getMake, Car::setMake);
//        this.binder.forField(model).bind(Car::getModel, Car::setModel);
        this.binder.forField(model).bind("model");
        this.binder.forField(manufactureYear).bind("manufactureYear");

        FormLayout formLayout = new FormLayout(this.make, this.model, this.manufactureYear);

//        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        Button saveButton = new Button(this.getTranslation("car.save"), e -> this.save());
        Button cancelButton = new Button(this.getTranslation("car.cancel"), e -> UI.getCurrent().navigate(CarListView.class));

        this.add(this.title, formLayout, new HorizontalLayout(saveButton, cancelButton));

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        this.car = event.getRouteParameters().getLong("id").map(this.carService::findById).orElseGet(Car::new);

        this.binder.readBean(this.car);
        this.title.setText(this.getPageTitle());
    }

    public void save() {
        if (this.binder.writeBeanIfValid(this.car)) {
            {
                this.carService.save(this.car);
                UI.getCurrent().navigate(CarListView.class);
            }
        }
    }

    @Override
    public String getPageTitle() {
        return this.getTranslation(this.car == null || this.car.getId() == null ? "car.new.title" : "car.edit.title");
    }
}
