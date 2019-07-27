package foodOreder.feedMeServer.Remote;

import foodOreder.feedMeServer.Model.DataMessage;
import foodOreder.feedMeServer.Model.MyResponse;
import foodOreder.feedMeServer.Model.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAYAOq9O4:APA91bFGqFJvAlS5nNRCCkCHgur2qLTsvOZp12EWbjW56VYwSahcyVVu5MvYMbHwkNKtdufE6UUxPPza-WwDilgV-4e4-xgmJoKdQ5DBNTDjVKxJTJC4ezBF9-WcmLRE--mE0P0Gmidu"

            }

    )


    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body DataMessage body);

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
