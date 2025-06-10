package org.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DD_Refactor {

    abstract class AbstractPublisher {
        protected Map<String, String> mappings = new HashMap<>();
        protected Map<String, String> ftpSettings = new HashMap<>();

        abstract String code();

        public void publish() {
            String code = code();
            List<CustomerAccount> accounts = loadAccounts(code);
            for(CustomerAccount account : accounts) {
                publishAccount(account);
            }
        }

        protected abstract void publishAccount(CustomerAccount account);

        protected final void loadMappings() {
            mappings = Map.of("RED", "Rosso", "GREEN", "Verde", "BLUE", "Blu");
        }

        protected final void loadFtpSettings() {
            ftpSettings = Map.of("host", "127.0.0.1", "port", "21");
        }

        private List<CustomerAccount> loadAccounts(String code) {
            return List.of(new CustomerAccount(1L), new CustomerAccount(2L));
        }

        protected List<Vehicle> loadVehicles(String code) {
            return List.of(new Vehicle("RED"), new Vehicle("GREEN"), new Vehicle("BLUE"));
        }
    }

    class SubitoPublisher extends AbstractPublisher {

        @Override
        String code() {
            return "subito";
        }

        @Override
        protected void publishAccount(CustomerAccount account) {
            loadMappings();
            loadFtpSettings();

            List<Vehicle> vehicles = loadVehicles(code());

            FTPClient ftpClient = new FTPClient(ftpSettings);
            ftpClient.upload(vehicles);
        }
    }

    record CustomerAccount(long id) {

    }

    record Vehicle(String color) {

    }

    class FTPClient {

        public FTPClient(Map<String, String> ftpSettings) {

        }

        public void upload(List<Vehicle> vehicles) {
        }
    }

}
