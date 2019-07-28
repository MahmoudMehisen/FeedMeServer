package foodOreder.feedMeServer;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

import foodOreder.feedMeServer.Common.Common;
import foodOreder.feedMeServer.Model.DataMessage;
import foodOreder.feedMeServer.Model.MyResponse;
import foodOreder.feedMeServer.Model.Notification;
import foodOreder.feedMeServer.Model.Sender;
import foodOreder.feedMeServer.Model.Token;
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
                DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
                tokens.orderByChild("serverToken").equalTo(false)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                                {
                                    Token token = postSnapShot.getValue(Token.class);

                                    Map<String,String> dataSend = new HashMap<>();
                                    dataSend.put("title",edtTitle.getText().toString());
                                    dataSend.put("message",edtMessage.getText().toString());
                                    DataMessage dataMessage = new DataMessage(token.getToken(),dataSend);

                                    mService.sendNotification(dataMessage);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        });

    }
}
