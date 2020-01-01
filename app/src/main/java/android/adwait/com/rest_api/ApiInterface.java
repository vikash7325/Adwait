package android.adwait.com.rest_api;

import android.adwait.com.admin.model.ADCreateAccountRequest;
import android.adwait.com.admin.model.ADCreateAccountResponse;
import android.adwait.com.admin.model.ADTransferRequest;
import android.adwait.com.donation.ADSubscriptionResponse;
import android.adwait.com.donation.model.ADCreateOrderRequest;
import android.adwait.com.donation.model.ADCreateOrderResponse;
import android.adwait.com.donation.model.ADPaymentMethodRequest;
import android.adwait.com.donation.model.ADPaymentMethodResponse;
import android.adwait.com.donation.model.ADSignVerifyRequest;
import android.adwait.com.donation.model.ADSignVerifyResponse;
import android.adwait.com.donation.model.ADStoredSubRequest;
import android.adwait.com.donation.model.ADSubscriptionRequest;
import android.adwait.com.subscription.model.ADStoredSubResponse;

import com.google.gson.JsonElement;

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

    @Headers("Content-Type: application/json")
    @POST("api/signature")
    Call<ADPaymentMethodResponse> getPaymentMethod(@Body ADPaymentMethodRequest request);

    @Headers("Content-Type: application/json")
    @POST("api/transfer")
    Call<JsonElement> transferAmount(@Body ADTransferRequest request);

    @Headers("Content-Type: application/json")
    @POST("api/subscription")
    Call<ADSubscriptionResponse> getSubscription(@Body ADSubscriptionRequest request);

    @Headers("Content-Type: application/json")
    @POST("api/subscription")
    Call<ADStoredSubResponse> getCompletedSubscription(@Body ADStoredSubRequest request);
}
