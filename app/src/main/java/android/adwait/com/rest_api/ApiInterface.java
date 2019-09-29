package android.adwait.com.rest_api;

import android.adwait.com.admin.model.ADCreateAccountRequest;
import android.adwait.com.admin.model.ADCreateAccountResponse;
import android.adwait.com.donation.model.ADCreateOrderRequest;
import android.adwait.com.donation.model.ADCreateOrderResponse;
import android.adwait.com.donation.model.ADSignVerifyRequest;
import android.adwait.com.donation.model.ADSignVerifyResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {

    @Headers("Content-Type: application/json")
    @POST("api/accounts")
    Call<ADCreateAccountResponse> createAccount(@Body ADCreateAccountRequest request);

    @Headers("Content-Type: application/json")
    @POST("api/orders")
    Call<ADCreateOrderResponse> createOrder(@Body ADCreateOrderRequest request);

    @Headers("Content-Type: application/json")
    @POST("api/signature")
    Call<ADSignVerifyResponse> verifySignature(@Body ADSignVerifyRequest request);
}
