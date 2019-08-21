package android.adwait.com.rest_api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static final String BASE_URL = "api.razorpay.com/v1/";
    private static Retrofit retrofit = null;


    public static Retrofit getClient(String key) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://" + key + BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
