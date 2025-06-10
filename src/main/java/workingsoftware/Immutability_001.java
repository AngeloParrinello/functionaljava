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
        FtpUploader uploader = new FtpUploader();
        uploader.upload();
    }

    static class FtpUploader {
        private FtpSettings ftpSettings;
        private File file;

        public void upload() {
            System.out.println("FTP Upload to: "+ftpSettings.host+":"+ftpSettings.port+" with username: "+ftpSettings.username+" and password: "+ftpSettings.password+" file: "+file.getAbsolutePath());
        }

        public FtpUploader setFtpSettings(FtpSettings ftpSettings) {
            this.ftpSettings = ftpSettings;
            return this;
        }

        public FtpUploader setFile(File file) {
            this.file = file;
            return this;
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
