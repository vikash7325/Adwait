package android.adwait.com.rest_api;

import android.adwait.com.admin.model.ADCreateAccountRequest;
import android.adwait.com.admin.model.ADCreateAccountResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {

    @Headers("Content-Type: application/json")
    @POST("beta/accounts")
    Call<ADCreateAccountResponse> createAccount(@Body ADCreateAccountRequest request);
}
