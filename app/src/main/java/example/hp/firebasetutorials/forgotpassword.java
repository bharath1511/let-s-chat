package example.hp.firebasetutorials;

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
import com.google.firebase.auth.FirebaseAuth;

public class forgotpassword extends AppCompatActivity {
EditText vemail;
    Button vsend;
    TextView back;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        auth=FirebaseAuth.getInstance();
        back= (TextView) findViewById(R.id.back);
        vemail= (EditText) findViewById(R.id.verificationemail);
        vsend= (Button) findViewById(R.id.send);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(forgotpassword.this,MainActivity.class));
                forgotpassword.this.finish();


            }
        });
        vsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vemail.getText().toString().isEmpty())
                {Toast.makeText(forgotpassword.this, "please fill  the Field", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.sendPasswordResetEmail(vemail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       if(task.isSuccessful())
                       {
                           Toast.makeText(forgotpassword.this, "email sent", Toast.LENGTH_SHORT).show();
                       }else
                       {
                           Toast.makeText(forgotpassword.this, " email sending failed", Toast.LENGTH_SHORT).show();
                       }
                    }
                });


            }
        });


    }
}
