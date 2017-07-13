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

public class signup extends AppCompatActivity {
EditText semail,spassword,username;
    Button register;
    TextView sforgot,slogin;
 private FirebaseAuth auth;
    private DatabaseReference dref;
    ProgressDialog dialog;
  private  FirebaseAuth.AuthStateListener mstate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        register= (Button) findViewById(R.id.register);
        slogin= (TextView) findViewById(R.id.slogin);
        sforgot= (TextView) findViewById(R.id.sforgot);
        semail= (EditText) findViewById(R.id.semail);
        username= (EditText) findViewById(R.id.username);
        spassword= (EditText) findViewById(R.id.spassword);
        this.setTitle("Sign Up");
        auth=FirebaseAuth.getInstance();
        mstate=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(auth.getCurrentUser()!=null)
                {dref = FirebaseDatabase.getInstance().getReference();
                    String userid=auth.getCurrentUser().getEmail().replace("@","").replace(".","");
                     DatabaseReference ref=dref.child("users").child(userid);
                    ref.child("Email").setValue(auth.getCurrentUser().getEmail());
                    ref.child("Name").setValue(username.getText().toString().trim());
                    ref.child("Image_Url").setValue("Null");


                    startActivity(new Intent(signup.this,chat.class));
                    signup.this.finish();
                }
            }
        };

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(semail.getText().toString().isEmpty()||spassword.getText().toString().isEmpty()||username.getText().toString().isEmpty())
                {
                    Toast.makeText(signup.this, "please fill all the Fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                signupuser(semail.getText().toString(),spassword.getText().toString());
            }
        });
        sforgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signup.this,forgotpassword.class));
            }
        });
        slogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signup.this,MainActivity.class));
                signup.this.finish();
            }
        });


    }
    public void signupuser(String user,String password)
    {dialog =new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("loading ......");
        dialog.setCancelable(false);
        dialog.show();
        auth.createUserWithEmailAndPassword(user,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if(!task.isSuccessful())
            {dialog.cancel();
                Toast.makeText(signup.this, "Error cannot register", Toast.LENGTH_SHORT).show();
            }
            else
            {


                dialog.cancel();
                Toast.makeText(signup.this, " registered successfully", Toast.LENGTH_SHORT).show();
            }
        }
    });

    }
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

