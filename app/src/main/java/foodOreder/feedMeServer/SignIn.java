package foodOreder.feedMeServer;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import foodOreder.feedMeServer.Common.Common;
import foodOreder.feedMeServer.Model.User;
import foodOreder.feedMeServer.Util.ProgressGenerator;

public class SignIn extends AppCompatActivity implements ProgressGenerator.OnCompleteListener {
    ActionProcessButton btnSignIn;
    ProgressGenerator progressGenerator;
    EditText editPhone, editPassword;
    FirebaseDatabase database;
    DatabaseReference table_user;
    Intent HomeIntent;
    TextView slogan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        progressGenerator = new ProgressGenerator(this);
        btnSignIn = (ActionProcessButton) findViewById(R.id.btnSignIn);
        editPassword = (EditText) findViewById(R.id.editPassword);
        editPhone = (EditText) findViewById(R.id.editPhone);
        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("Users");
        slogan = (TextView) findViewById(R.id.slogan);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/comics.ttf");
        slogan.setTypeface(face);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child(editPhone.getText().toString()).exists()) {
                            User user = dataSnapshot.child(editPhone.getText().toString()).getValue(User.class);
                            user.setPhone(editPhone.getText().toString());
                            if(Boolean.parseBoolean(user.getIsStaff()))
                            {
                                if (user.getPassword().equals(editPassword.getText().toString())) {
                                    progressGenerator.start(btnSignIn);
                                    btnSignIn.setEnabled(false);
                                    editPassword.setEnabled(false);
                                    editPhone.setEnabled(false);
                                    HomeIntent = new Intent(SignIn.this, Home.class);
                                    Common.CommonUser = user;


                                } else {
                                    Toast.makeText(getApplicationContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Please Login with staff account", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "User Not Exist", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });


    }

    @Override
    public void onComplete() {

        startActivity(HomeIntent);
        finish();
    }
}
