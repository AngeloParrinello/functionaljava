package functionaljava.monad;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SampleApi {

    static SampleApi build() {
        return new Retrofit.Builder().baseUrl("http://localhost:8080").addConverterFactory(GsonConverterFactory.create()).build().create(SampleApi.class);
    }

    @POST("/api/v1/login")
    Call<Token> login(@Query("username") String username, @Query("password") String password);

    @POST("/api/v1/publish")
    Call<PublishedVehicle> publish(@Header("auth") String auth, @Body Vehicle vehicle);

    public static class Token {

        private String accessToken;

        public String getAccessToken() {
            return accessToken;
        }

        public Token setAccessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }
    }

    public static class Vehicle {

        private String plate;
        private String model;
        private String brand;
        private String color;

        public String getPlate() {
            return plate;
        }

        public Vehicle setPlate(String plate) {
            this.plate = plate;
            return this;
        }

        public String getModel() {
            return model;
        }

        public Vehicle setModel(String model) {
            this.model = model;
            return this;
        }

        public String getBrand() {
            return brand;
        }

        public Vehicle setBrand(String brand) {
            this.brand = brand;
            return this;
        }

        public String getColor() {
            return color;
        }

        public Vehicle setColor(String color) {
            this.color = color;
            return this;
        }
    }

    public static class PublishedVehicle {
        private String id;

        public String getId() {
            return id;
        }

        public PublishedVehicle setId(String id) {
            this.id = id;
            return this;
        }
    }
}
