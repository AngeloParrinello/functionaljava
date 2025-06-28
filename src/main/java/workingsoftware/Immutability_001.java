package workingsoftware;

import java.io.File;

/*
 * 1. elimina intere classi di errori
 * 2. facilita la costruzione di oggetti validi
 * 3. evidenzia le dipendenze
 * 4. il dominio immutabile, i side-effects all'esterno
 * 5. è più probabile leggere dati che scriverli
 * 6. usare i tipi per rappresentare cambiamenti di stato
 */
public class Immutability_001 {

    public static void main(String[] args) {
        FtpSettings ftpSettings = new FtpSettings()
                .setUsername("user")
                .setPassword("password")
                .setHost("ftp.example.com")
                .setPort(21);
        File file = new File("path/to/file.txt");
        FtpUploader uploader = new FtpUploader(ftpSettings, file);
        // Here we take nothing as input and no output is produced ... probably a side-effect is involved
        // we want to minimize side-effects, so we use a method that does not return anything
        // side effects should stay outside the core logic of the application
        uploader.upload();
    }

    static class FtpUploader {
        private final FtpSettings ftpSettings;
        private final File file;

        FtpUploader(FtpSettings ftpSettings, File file) {
            this.ftpSettings = ftpSettings;
            this.file = file;
        }

        public void upload() {
            System.out.println("FTP Upload to: " + ftpSettings.host + ":" + ftpSettings.port + " with username: " + ftpSettings.username + " and password: " + ftpSettings.password + " file: " + file.getAbsolutePath());
        }
    }

    static class FtpSettings {
        private String username;
        private String password;
        private String host;
        private int port;

        public String getUsername() {
            return username;
        }

        public FtpSettings setUsername(String username) {
            this.username = username;
            return this;
        }

        public String getPassword() {
            return password;
        }

        public FtpSettings setPassword(String password) {
            this.password = password;
            return this;
        }

        public String getHost() {
            return host;
        }

        public FtpSettings setHost(String host) {
            this.host = host;
            return this;
        }

        public int getPort() {
            return port;
        }

        public FtpSettings setPort(int port) {
            this.port = port;
            return this;
        }
    }

}
