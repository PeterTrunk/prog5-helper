package hu.mik.pte.prog5.crud_mvc.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import hu.mik.pte.prog5.crud_mvc.model.Car;
import hu.mik.pte.prog5.crud_mvc.service.CarService;
import hu.mik.pte.prog5.crud_mvc.views.layout.MainLayout;
import jakarta.annotation.security.PermitAll;

@Route(value = "cars", layout = MainLayout.class) // With empty value, it would be /carlist
@RouteAlias(value = "", layout = MainLayout.class) //
//@Route("cars") // /cars
@PermitAll
public class CarListView extends VerticalLayout implements HasDynamicTitle {

    private final CarService carService;
    private final Grid<Car> grid = new Grid<>(Car.class, false);


    public CarListView(CarService carService) {
        this.carService = carService;

        Button newCar = new Button(this.getTranslation("car.new"), e -> UI.getCurrent().navigate(CarFormView.class));

        this.grid.addColumn(Car::getMake).setHeader(this.getTranslation("car.make"));
        this.grid.addColumn(Car::getModel).setHeader(this.getTranslation("car.model"));
        this.grid.addColumn(Car::getManufactureYear).setHeader(this.getTranslation("car.manufactureYear"));

        this.grid.addComponentColumn(i -> this.createActions(i));
//        this.grid.addComponentColumn(this::createActions);

        this.grid.setEmptyStateText(this.getTranslation("car.list.empty"));

        this.add(newCar, grid);
        this.grid.setItems(this.carService.list());
        refresh();

    }

    private HorizontalLayout createActions(Car car) {
        Button edit = new Button(this.getTranslation("car.edit"),
                e -> UI.getCurrent().navigate("cars/" + car.getId() + "/edit"));
        Button delete = new Button(this.getTranslation("car.delete"), e -> {
            ConfirmDialog dialog = new ConfirmDialog();
            dialog.setHeader(this.getTranslation("car.delete"));
            dialog.setText(this.getTranslation("car.delete"));

            dialog.setCancelable(true);
            dialog.setCancelText(this.getTranslation("car.cancel"));

            dialog.addConfirmListener(ev ->
                    {
                        this.carService.deleteById(car.getId());
                        this.refresh();
                    }

            );

            dialog.open();
        });

        return new HorizontalLayout(edit, delete);
    }


    private void refresh() {
        this.grid.setItems(this.carService.list());
    }

    @Override
    public String getPageTitle() {
        return this.getTranslation("car.list.title");
    }

}
