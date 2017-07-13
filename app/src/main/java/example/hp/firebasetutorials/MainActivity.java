package example.hp.firebasetutorials;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
   EditText email,password;
    TextView signup,forgot;
    Button login;
    private FirebaseAuth auth;
    FirebaseAuth.AuthStateListener mstate;
    FirebaseDatabase mDatabase;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth=FirebaseAuth.getInstance();


        login= (Button) findViewById(R.id.login);
        signup= (TextView) findViewById(R.id.signup);
        forgot= (TextView) findViewById(R.id.forgot);
        email= (EditText) findViewById(R.id.email);
        password= (EditText) findViewById(R.id.password);



        mstate=new FirebaseAuth.AuthStateListener() {
    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if(auth.getCurrentUser()!=null)
        {
            startActivity(new Intent(MainActivity.this,chat.class));
            MainActivity.this.finish();
        }
    }
};

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
if(email.getText().toString().isEmpty()||password.getText().toString().isEmpty())
{Toast.makeText(MainActivity.this, "please fill all the Fields", Toast.LENGTH_SHORT).show();
    return;
}
            loginuser(email.getText().toString(),password.getText().toString());

            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this,signup.class));
                MainActivity.this.finish();

            }
        });
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this,forgotpassword.class));


            }
        });





    }
    public void loginuser(String user, final String password1)
    { dialog =new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("loading ......");
        dialog.setCancelable(false);
        dialog.show();
        auth.signInWithEmailAndPassword(user,password1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful())
                {    dialog.cancel();

                    if(password1.length()<6)
                    {
                        Toast.makeText(MainActivity.this, "password too short", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(MainActivity.this,"not succcesful", Toast.LENGTH_SHORT).show();
                }else
                {dialog.cancel();

                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(mstate);
    }

    @Override
    protected void onStop() {
        super.onStop();

        auth.removeAuthStateListener(mstate);
    }
}
