package functionaljava.monad;

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.unauthorized;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import functionaljava.monad.SampleApi.PublishedVehicle;
import functionaljava.monad.SampleApi.Token;
import functionaljava.monad.SampleApi.Vehicle;
import java.io.IOException;
import okhttp3.ResponseBody;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Response;

/*
 * 1. Monads: .map e .flatMap
 * 2. Spingiamo in basso la complessità: avremo più spesso a che fare con chiamate retrofit che con l'implementazione della monad.
 */
public class MonadForRetrofit_004 {

    @Test
    public void runMain() {
        PublicationResult publishedVehicle = publishVehicle(new Vehicle(), "secret");
        System.out.println("Published Vehicle Result: " + publishedVehicle);

        PublicationResult failingVehicle = publishVehicle(new Vehicle(), "wrongpassword");
        System.out.println("Published Vehicle Result: " + failingVehicle);

        Assertions.assertThat(publishedVehicle.vehicleId()).isEqualTo("5678");
        Assertions.assertThat(publishedVehicle.error()).isNull();
        Assertions.assertThat(failingVehicle.vehicleId()).isNull();
        Assertions.assertThat(failingVehicle.error()).contains("unauthorized");
    }

    private static PublicationResult publishVehicle(Vehicle vehicle, String password) {
        SampleApi client = SampleApi.build();

        Call<Token> tokenCall = client.login("username", password);
        try {
            Response<Token> tokenResponse = tokenCall.execute();

            if (tokenResponse.isSuccessful()) {
                // TODO: OH NO!!
                Token tokenBody = tokenResponse.body();
                if (tokenBody != null) {
                    // we can finally upload our vehicle!
                    // TODO: tokenBody.string() may throw exception!
                    Call<PublishedVehicle> publishedVehicleCall = client.publish(tokenBody.getAccessToken(), vehicle);
                    Response<PublishedVehicle> vehicleResponse = publishedVehicleCall.execute();

                    if (vehicleResponse.isSuccessful()) {
                        PublishedVehicle publishedVehicle = vehicleResponse.body();
                        if (publishedVehicle != null) {
                            return PublicationResult.ok(publishedVehicle.getId());
                        }
                    }
                } else {
                    // TODO: OH NO!!
                    return PublicationResult.error(tokenResponse.errorBody().string());
                }
            } else {
                // TODO: OH NO!!
                try (ResponseBody body = tokenResponse.errorBody()) {
                    if (body != null) {
                        return PublicationResult.error(body.string());
                    } else {
                        return PublicationResult.error("something went wrong");
                    }
                }
            }

            return PublicationResult.error("oh no!");
        } catch (IOException e) {
            return PublicationResult.error(e.getMessage());
        }
    }

    record PublicationResult(String vehicleId, String error) {

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
