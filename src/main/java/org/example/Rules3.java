package org.example;

import java.util.function.Function;

public class Rules3 {

    public static void main(String[] args) {

        Function<Vehicle, Integer> getter = Vehicle::price;


//        System.out.println("Vehicle is valid: " + PRICE
//            .greaterThan(100)
//            .lowerThan(300)
//            .equalsTo(180)
//            .isValid(new Vehicle(180, 10, new MappedValue("10", "14"), new Company(1, 2))));
    }

    interface IntRule extends Rule {
        default IntRule greaterThan(Function<Vehicle, Integer> getter, int value) {
            return (vehicle) -> getter.apply(vehicle) > value;
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
