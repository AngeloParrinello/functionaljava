package org.example;

import static org.example.Templating.Rule.and;
import static org.example.Templating.Rule.or;

import java.util.Arrays;
import java.util.function.Function;

/*

1. lot of similar functions to verify Vehicle fields
2. use a single RequiredField rule with a template method
3. slowly create a DSL with "required" "startsWith"
4. warn about taking it too far
5. static methods "and" and "or"
6. eventually collect results?

 */

public class Templating {

    record Company(String name, String address) {

    }

    record Vehicle(String type, String plate, String color, String model, String manufacturer, Company company) {

    }

    interface Rule {

        boolean isValid(Vehicle vehicle);

        static Rule and(Rule... rules) {
            return (Vehicle v) -> Arrays.stream(rules).allMatch(r -> r.isValid(v));
        }

        static Rule or(Rule... rules) {
            return (Vehicle v) -> Arrays.stream(rules).anyMatch(r -> r.isValid(v));
        }
    }

    static class RequiredPlate implements Rule {

        @Override
        public boolean isValid(Vehicle vehicle) {
            return vehicle.plate() != null;
        }
    }

    static class RequiredModel implements Rule {

        @Override
        public boolean isValid(Vehicle vehicle) {
            return vehicle.model() != null;
        }
    }

    static class RequiredManufacturer implements Rule {

        @Override
        public boolean isValid(Vehicle vehicle) {
            return vehicle.manufacturer() != null;
        }
    }

    static class RequiredField implements Rule {

        public static RequiredField MANUFACTURE = new RequiredField(Vehicle::manufacturer);
        public static RequiredField PLATE = new RequiredField(Vehicle::plate);
        public static RequiredField COLOR = new RequiredField(Vehicle::color);

        public static RequiredField COMPANY_ADDRESS = new RequiredField(companyField(Company::address));

        private final Function<Vehicle, String> field;
        private final Function<Vehicle, Boolean> selector;

        private RequiredField(Function<Vehicle, String> field) {
            this(field, v -> true);
        }

        private RequiredField(Function<Vehicle, String> field, Function<Vehicle, Boolean> selector) {
            this.field = field;
            this.selector = selector;
        }

        static Function<Vehicle, String> companyField(Function<Company, String> field) {
            return (Vehicle v) -> field.apply(v.company());
        }

        @Override
        public boolean isValid(Vehicle vehicle) {
            if (selector.apply(vehicle)) {
                return field.apply(vehicle) != null;
            }
            return true;
        }

        public RequiredField onlyFor(String values) {
            // partial application: stiamo "fissando" il valore "values"
            return new RequiredField(field, v -> values.contains(v.type()));
        }
    }

    public static void main(String[] args) {

        Rule rules = and(
            RequiredField.COLOR.onlyFor("NEW"),
            RequiredField.PLATE,
            or(
                RequiredField.MANUFACTURE,
                RequiredField.COMPANY_ADDRESS
            )
        );

        Vehicle vehicle = new Vehicle("NEW", null, null, null, null, new Company("acme", "12345"));

        boolean isValid = rules.isValid(vehicle);

        System.out.println(isValid);
    }
}