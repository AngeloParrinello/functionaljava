package workingsoftware;

import java.io.File;
import java.util.List;

/*
 * 1. una singola funzione che esprime una esigenza del business
 * 2. deep vs shallow modules
 * 3. possibilità di ricombinare tutti pezzi come fossero lego
 */
public class Composition_003 {

    public static void main(String[] args) {
        new SubitoPublisher().publish();
        new AlVolantePublisher().publish();
    }

    static abstract class Publisher {

        private final String code;

        Publisher(String code) {
            this.code = code;
        }

        public void publish() {
            List<Vehicle> vehicles = loadVehicles(code);
            List<Vehicle> validVehicles = vehicles.stream().filter(this::isValid).toList();
            File file = buildFile(validVehicles);
            System.out.println("Uploaded "+validVehicles.size()+" vehicles in file = " + file);
        }

        protected abstract boolean isValid(Vehicle vehicle);

        protected abstract List<Vehicle> loadVehicles(String code);

        protected abstract File buildFile(List<Vehicle> vehicles);
    }

    static class SubitoPublisher extends AbstractCommonFormat {

        SubitoPublisher() {
            super("subito");
        }

        @Override
        protected boolean isValid(Vehicle vehicle) {
            return true;
        }

        @Override
        protected List<Vehicle> loadVehicles(String code) {
            return List.of(new Vehicle(), new Vehicle());
        }
    }

    static class AlVolantePublisher extends AbstractCustomVehicleLoad {

        AlVolantePublisher() {
            super("alvolante");
        }

        @Override
        protected boolean isValid(Vehicle vehicle) {
            return true;
        }

        @Override
        protected File buildFile(List<Vehicle> vehicles) {
            return new File("alvolante.txt");
        }
    }

    static abstract class AbstractCommonFormat extends Publisher {
        AbstractCommonFormat(String code) {
            super(code);
        }

        @Override
        protected File buildFile(List<Vehicle> vehicles) {
            return new File("commonformat.txt");
        }
    }

    static abstract class AbstractCustomVehicleLoad extends Publisher {

        AbstractCustomVehicleLoad(String code) {
            super(code);
        }

        @Override
        protected List<Vehicle> loadVehicles(String code) {
            return List.of(new Vehicle(), new Vehicle(), new Vehicle());
        }
    }

    record Vehicle() {

    }
}
