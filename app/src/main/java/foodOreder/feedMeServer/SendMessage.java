package foodOreder.feedMeServer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import foodOreder.feedMeServer.Common.Common;
import foodOreder.feedMeServer.Model.MyResponse;
import foodOreder.feedMeServer.Model.Notification;
import foodOreder.feedMeServer.Model.Sender;
import foodOreder.feedMeServer.Remote.APIService;
import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendMessage extends AppCompatActivity {

    MaterialEditText edtMessage,edtTitle;
    FButton btnSend;

    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        mService = Common.getFCMService();

        edtMessage = (MaterialEditText) findViewById(R.id.editMessage);
        edtTitle = (MaterialEditText) findViewById(R.id.editTitle);

        btnSend = (FButton) findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create message
                Notification notification = new Notification(edtTitle.getText().toString(),edtMessage.getText().toString());

                Sender toTopic =new Sender();
                toTopic.to = new StringBuilder("/topics/").append(Common.topicName).toString();
                toTopic.notification = notification;

                mService.sendNotification(toTopic)
                        .enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                if(response.isSuccessful())
                                {
                                    Toast.makeText(SendMessage.this,"Message Sent",Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Toast.makeText(SendMessage.this,""+t.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }
}
