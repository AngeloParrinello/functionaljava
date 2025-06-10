package workingsoftware.refactor;

import java.io.File;
import java.util.List;

public class Composition {

    public static void main(String[] args) {

        new Publisher("subito",
            v -> true,
            code -> List.of(new Vehicle(), new Vehicle()),
            vehicles -> new File("subito.txt"));

        new Publisher("alVolante",
            v -> true,
            code -> List.of(new Vehicle(), new Vehicle()),
            vehicles -> new File("subito.txt"));
    }

    static interface Rule {
        boolean isValid(Vehicle vehicle);
    }

    static interface VehicleLoader {
        List<Vehicle> loadVehicles(String code);
    }

    static interface FileBuilder {
        File build(List<Vehicle> vehicles);
    }


    static class Publisher {

        private final String code;
        private final Rule rule;
        private final VehicleLoader loadVehicles;
        private final FileBuilder fileBuilder;

        Publisher(String code, Rule rule, VehicleLoader loadVehicles, FileBuilder fileBuilder) {
            this.code = code;
            this.rule = rule;
            this.loadVehicles = loadVehicles;
            this.fileBuilder = fileBuilder;
        }

        public void publish() {
            List<Vehicle> vehicles = loadVehicles.loadVehicles(code);
            File file = fileBuilder.build(vehicles.stream().filter(rule::isValid).toList());
            System.out.println("Upload file = " + file);
        }
    }

    record Vehicle() {

    }
}
