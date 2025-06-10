package org.example;

import java.util.function.Function;

public class Rules {

    public static void main(String[] args) {
        Rule rule = (Vehicle vehicle) -> vehicle.age() >= 18;
        Vehicle v = new Vehicle(18, 10, new MappedValue("10", "14"), new Company(1, 2));

        System.out.println("Vehicle is valid: " + IntRule.PRICE_B2C.required().greaterThan(15).lowerThan(20).isValid(v));
    }

    record Vehicle(int price, int age, MappedValue priceB2c, Company company) {

    }

    record Company (int customer, int phone) {

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

    record IntRule(Function<Vehicle, Integer> field, Rule rule) implements Rule {

        public static final IntRule PRICE = forField(Vehicle::price);
        public static final IntRule AGE = forField(Vehicle::age);

        public static final IntRule PRICE_B2C = forMappedValue(Vehicle::priceB2c);

        public static final IntRule COMPANY_CUSTOMER = forCompanyField(Company::customer);
        public static final IntRule COMPANY_PHONE = forCompanyField(Company::phone);


        static private IntRule forField(Function<Vehicle, Integer> field) {
            return new IntRule(field, (vehicle) -> true);
        }

        static private IntRule forMappedValue(Function<Vehicle, MappedValue> field) {
            return new IntRule((t) -> field.apply(t).toInt(), (vehicle) -> true);
        }

        static private IntRule forCompanyField(Function<Company, Integer> field) {
            return new IntRule((t) -> field.apply(t.company()), (vehicle) -> true);
        }

        IntRule required() {
            return new IntRule(field, (vehicle) -> rule.isValid(vehicle) && field.apply(vehicle) > 0);
        }

        IntRule greaterThan(int value) {
            return new IntRule(field, (vehicle) -> rule.isValid(vehicle) && field.apply(vehicle) > value);
        }

        IntRule lowerThan(int value) {
            return new IntRule(field, (vehicle) -> rule.isValid(vehicle) && field.apply(vehicle) < value);
        }

        @Override
        public boolean isValid(Vehicle vehicle) {
            return rule.isValid(vehicle);
        }
    }

    interface Rule {

        boolean isValid(Vehicle vehicle);
    }

}
