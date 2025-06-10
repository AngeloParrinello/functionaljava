package org.example;

import static org.example.Rules2.IntRule.PRICE;

import java.util.Map;
import java.util.function.Function;

public class Rules2 {

    public static void main(String[] args) {

        System.out.println("Vehicle is valid: " + PRICE
            .greaterThan(100)
            .lowerThan(300)
            .equalsTo(180)
            .isValid(new Vehicle(180, 10, new MappedValue("10", "14"), new Company(1, 2))));
    }

    interface IntRule extends Rule {

        static IntRule forField(Function<Vehicle, Integer> field) {
            return () -> Map.entry(field, (vehicle) -> true);
        }

        static IntRule forCompanyField(Function<Company, Integer> field) {
            return () -> Map.entry(field.compose(Vehicle::company), (vehicle) -> true);
        }

        private static IntRule forMappedValue(Function<Vehicle, MappedValue> field) {
            return () -> Map.entry(field.andThen(MappedValue::toInt), (vehicle) -> true);
        }

        IntRule PRICE = forField(Vehicle::price);
        IntRule COMPANY_PHONE = forCompanyField(Company::phone);
        IntRule PRICEB2C = forMappedValue(Vehicle::priceB2c);

        Map.Entry<Function<Vehicle, Integer>, Rule> field();

        default IntRule equalsTo(int value) {
            return () -> Map.entry(field().getKey(), vehicle -> this.isValid(vehicle) && field().getKey().apply(vehicle) == value);
        }

        default IntRule greaterThan(int value) {
            return () -> Map.entry(field().getKey(), vehicle -> this.isValid(vehicle) && field().getKey().apply(vehicle) > value);
        }

        default IntRule lowerThan(int value) {
            return () -> Map.entry(field().getKey(), vehicle -> this.isValid(vehicle) && field().getKey().apply(vehicle) < value);
        }

        @Override
        default boolean isValid(Vehicle vehicle) {
            return this.field().getValue().isValid(vehicle);
        }
    }

    interface Rule {

        boolean isValid(Vehicle vehicle);
    }

    static record Vehicle(int price, int age, MappedValue priceB2c, Company company) {

    }

    static record Company(int customer, int phone) {

    }

    record MappedValue(String original, String mapped) {

        public String value() {
            return mapped != null ? mapped : original;
        }

        Integer toInt() {
            try {
                return Integer.parseInt(value());
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }
}
