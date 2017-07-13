package example.hp.firebasetutorials;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class dashboard extends AppCompatActivity {
    EditText editpassword,changeusername;
    Button changepassword,signout,showdata,upload;
    TextView show;
    private FirebaseAuth auth;
    String uname;
    DatabaseReference ref;
    ValueEventListener mlis;

private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        auth=FirebaseAuth.getInstance();
        editpassword= (EditText) findViewById(R.id.editpassword);
        changeusername= (EditText) findViewById(R.id.changeusername);
        changepassword= (Button) findViewById(R.id.changepassword);
        signout= (Button) findViewById(R.id.signout);
        upload= (Button) findViewById(R.id.upload);
        showdata= (Button) findViewById(R.id.showdata);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(Html.fromHtml("<font color=#FFFFFF>" +"Settings" + "</font>"));
        }

        show= (TextView) findViewById(R.id.show);
        if(auth.getCurrentUser()!=null)
        {DatabaseReference dref = FirebaseDatabase.getInstance().getReference();
            String userid=auth.getCurrentUser().getEmail().replace("@","").replace(".","");
             ref=dref.child("users").child(userid);
            ref.addValueEventListener(mlis=new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    uname= dataSnapshot.child("Name").getValue(String.class);
                    show.setText("welcome " +uname);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }else
        {
            startActivity(new Intent(dashboard.this,MainActivity.class));
            this.finish();
        }
        changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editpassword.getText().toString().isEmpty())
                {Toast.makeText(dashboard.this, "please fill  the Field", Toast.LENGTH_SHORT).show();
                    return;
                }
                changepass(editpassword.getText().toString());


            }
        });
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(changeusername.getText().toString().isEmpty())
                {Toast.makeText(dashboard.this, "please fill  the Field", Toast.LENGTH_SHORT).show();
                    return;
                }
                DatabaseReference dref = FirebaseDatabase.getInstance().getReference();
                String userid=auth.getCurrentUser().getEmail().replace("@","").replace(".","");
                dref.child("users").child(userid).child("Name").setValue(changeusername.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(dashboard.this, "User name changed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             startActivity(new Intent(dashboard.this,friendschat.class));
            }
        });
        showdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(dashboard.this,chat.class));
            }
        });


    }
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    public void changepass(String pass)
    {
        auth.getCurrentUser().updatePassword(pass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(dashboard.this,"password change succcesful", Toast.LENGTH_SHORT).show();
                }else
                {
                    Toast.makeText(dashboard.this,"password change unsucccesful", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
