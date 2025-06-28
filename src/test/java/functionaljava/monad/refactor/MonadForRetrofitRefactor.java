package functionaljava.monad.refactor;

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.unauthorized;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import functionaljava.monad.SampleApi;
import functionaljava.monad.SampleApi.PublishedVehicle;
import functionaljava.monad.SampleApi.Token;
import functionaljava.monad.SampleApi.Vehicle;
import java.io.IOException;
import java.util.function.Function;
import org.apache.hc.client5.http.HttpResponseException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Response;

// 15-20 minuti
public class MonadForRetrofitRefactor {

    @Test
    public void runMain() {

        // .map V -> M(R)
        // .flatMap M(V) -> M(R)
//
//        var opt = Optional.of("5");
//        opt.map(Integer::parseInt)
//            .flatMap(n -> Optional.of(n + 1));

        PublicationResult publishedVehicleId = publishVehicle(new Vehicle());
        System.out.println("Published Vehicle Result: " + publishedVehicleId);
    }

    // Union type in Java! Allows only two results: Success or Failure
    sealed interface HttpResult<T> permits Success, Failure {

        default <R> HttpResult<R> map(Function<T, R> mapper) {
            return this.fold(t -> new Success<>(mapper.apply(t)), Failure::new);
        }

        default <R> HttpResult<R> flatMap(Function<T, HttpResult<R>> mapper) {
            return this.fold(mapper, Failure::new);
        }

        <R> R fold(Function<T, R> success, Function<Exception, R> failure);

        static <R> HttpResult<R> from(Call<R> call) {
            try {
                Response<R> response = call.execute();
                if (response.isSuccessful()) {
                    return new Success<>(response.body());
                } else {
                    return new Failure<>(new HttpResponseException(response.code(), response.errorBody().string()));
                }
            } catch (IOException e) {
                return new Failure<>(e);
            }
        }

    }

    record Failure<T>(Exception exception) implements HttpResult<T> {

        @Override
        public <R> R fold(Function<T, R> success, Function<Exception, R> failure) {
            return failure.apply(exception);
        }
    }

    record Success<T>(T value) implements HttpResult<T> {

        @Override
        public <R> R fold(Function<T, R> success, Function<Exception, R> ignored) {
            return success.apply(value);
        }
    }


    private static PublicationResult publishVehicle(Vehicle vehicle) {
        SampleApi client = SampleApi.build();

        return HttpResult
            .from(client.login("username", "wrongpassword"))
            .map(Token::getAccessToken)
            .flatMap(accessToken -> HttpResult.from(client.publish(accessToken, vehicle)))
            .fold(PublicationResult::ok, PublicationResult::error);
//
//
//        Call<Token> tokenCall = client.login("username", "secret");
//        try {
//            Response<Token> tokenResponse = tokenCall.execute();
//
//            if (tokenResponse.isSuccessful()) {
//                Token tokenBody = tokenResponse.body();
//                if (tokenBody != null) {
//                    // we can finally upload our vehicle!
//                    // TODO: tokenBody.string() may throw exception!
//                    Call<PublishedVehicle> publishedVehicleCall = client.publish(tokenBody.getAccessToken(), vehicle);
//                    Response<PublishedVehicle> vehicleResponse = publishedVehicleCall.execute();
//
//                    if (vehicleResponse.isSuccessful()) {
//                        PublishedVehicle publishedVehicle = vehicleResponse.body();
//                        if (publishedVehicle != null) {
//                            return PublicationResult.ok(publishedVehicle.getId());
//                        }
//                    }
//                } else {
//                    // TODO: OH NO!!
//                    return PublicationResult.error(tokenResponse.errorBody().string());
//                }
//            } else {
//                // TODO: OH NO!!
//                try (ResponseBody body = tokenResponse.errorBody()) {
//                    if (body != null) {
//                        return PublicationResult.error(body.string());
//                    } else {
//                        return PublicationResult.error("something went wrong");
//                    }
//                }
//            }
//
//            return PublicationResult.error("oh no!");
//        } catch (IOException e) {
//            return PublicationResult.error(e.getMessage());
//        }
    }

    record PublicationResult(String vehicleId, String error) {

        public static PublicationResult ok(PublishedVehicle publishedVehicle) {
            return new PublicationResult(publishedVehicle.getId(), null);
        }

        public static PublicationResult error(Exception failure) {
            return new PublicationResult(null, failure.getMessage());
        }

        static PublicationResult ok(String vehicleId) {
            return new PublicationResult(vehicleId, null);
        }

        static PublicationResult error(String error) {
            return new PublicationResult(null, error);
        }
    }

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(options().port(8080));

    @Before
    public void setup() {
        stubFor(post("/api/v1/login?username=username&password=secret").willReturn(ok().withBody("""
            {
                "accessToken": "1234"
            }
            """)));

        stubFor(post("/api/v1/login?username=username&password=wrongpassword").willReturn(unauthorized().withBody("""
            {
                "error": "unauthorized"
            }
            """)));

        stubFor(post("/api/v1/publish").willReturn(ok().withBody("""
            {
                "id": "5678"
            }
            """)));
    }
}
