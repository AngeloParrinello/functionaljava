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
        Publisher.builder()
                .code("subito")
                .rule(v -> true)
                .loadVehicles(code -> List.of(new Vehicle(), new Vehicle()))
                .buildFile(vehicles -> new File("subito.txt"))
                .publish();

        new Publisher(
                "subito",
                v -> true,
                code -> List.of(new Vehicle(), new Vehicle()),
                vehicles -> new File("subito.txt")
        ).publish();

        new Publisher(
                "alVolante",
                v -> true,
                code -> List.of(new Vehicle(), new Vehicle(), new Vehicle()),
                vehicles -> new File("alvolante.txt")
        ).publish();
    }

    interface PortalRule {
        boolean isValid(Vehicle vehicle);
    }

    interface VehicleLoader {
        List<Vehicle> loadVehicles(String code);
    }

    interface FileBuilder {
        File build(List<Vehicle> vehicles);
    }

    static class Publisher {

        private final String code;
        private final PortalRule isValid;
        private final VehicleLoader loadVehicles;
        private final FileBuilder buildFile;

        Publisher(String code, PortalRule isValid, VehicleLoader loadVehicles, FileBuilder buildFile) {
            this.code = code;
            this.isValid = isValid;
            this.loadVehicles = loadVehicles;
            this.buildFile = buildFile;
        }

        public void publish() {
            List<Vehicle> vehicles = loadVehicles.loadVehicles(code);
            List<Vehicle> validVehicles = vehicles.stream().filter(isValid::isValid).toList();
            File file = buildFile.build(validVehicles);
            System.out.println("Uploaded " + validVehicles.size() + " vehicles in file = " + file);
        }

        // Qui però stiamo inserendo un forte vincolo di ordine nella costruzione dell'oggetto Publisher...
        public static B1 builder() {
            return code -> rule -> loadVehicles -> buildFile -> new Publisher(code, rule, loadVehicles, buildFile);
        }

        interface B1 {
            B2 code(String code);
        }

        interface B2 {
            B3 rule(PortalRule rule);
        }

        interface B3 {
            B4 loadVehicles(VehicleLoader loadVehicles);
        }

        interface B4 {
            Publisher buildFile(FileBuilder buildFile);
        }
    }

    record Vehicle() {

    }
}
