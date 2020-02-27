package ad.adwait.mcom.rest_api;

import ad.adwait.mcom.admin.model.ADCreateAccountRequest;
import ad.adwait.mcom.admin.model.ADCreateAccountResponse;
import ad.adwait.mcom.admin.model.ADTransferRequest;
import ad.adwait.mcom.donation.model.ADCreateOrderRequest;
import ad.adwait.mcom.donation.model.ADCreateOrderResponse;
import ad.adwait.mcom.donation.model.ADPaymentMethodResponse;
import ad.adwait.mcom.donation.model.ADSignVerifyRequest;
import ad.adwait.mcom.donation.model.ADSignVerifyResponse;
import ad.adwait.mcom.subscription.model.ADSubscriptionRequest;
import ad.adwait.mcom.subscription.model.ADSubscriptionResponse;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

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
    @GET("api/payment/{payment_id}")
    Call<ADPaymentMethodResponse> getPaymentMethod(@Path("payment_id") String paymentId);

    @Headers("Content-Type: application/json")
    @POST("api/transfer")
    Call<JsonElement> transferAmount(@Body ADTransferRequest request);

    @Headers("Content-Type: application/json")
    @POST("api/subscription")
    Call<ADSubscriptionResponse> createSubscription(@Body ADSubscriptionRequest request);

    @Headers("Content-Type: application/json")
    @GET("api/subscription/{subscription_id}")
    Call<ADSubscriptionResponse> getSubscription(@Path("subscription_id") String subscriptionId);

    @Headers("Content-Type: application/json")
    @DELETE("api/subscription/{subscription_id}")
    Call<ADSubscriptionResponse> cancelSubscription(@Path("subscription_id") String subscriptionId);
}
