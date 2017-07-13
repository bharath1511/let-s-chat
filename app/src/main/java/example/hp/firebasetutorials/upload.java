package example.hp.firebasetutorials;

import android.app.ProgressDialog;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class upload extends AppCompatActivity {

  EditText title;
    private ImageView img;
    private Button select,upload;
    private  DatabaseReference dref;
    private  StorageReference sref;
    private  FirebaseAuth auth;
    private Uri imagepath;
    private ProgressDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        img= (ImageView) findViewById(R.id.imageView);
        select= (Button) findViewById(R.id.select);
        upload= (Button) findViewById(R.id.upload1);
        title= (EditText) findViewById(R.id.editText);
        dialog=new ProgressDialog(this);
        select.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(upload.this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);

                }
                else
                {
                    callgallery();
                }
            }
        });
        dref= FirebaseDatabase.getInstance().getReference().child("userdetails").push();
        sref= FirebaseStorage.getInstance().getReference();
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String mname=title.getText().toString();
                if(mname.isEmpty())
                {return;

                }
              dref.child("imagename").setValue(mname);
                Toast.makeText(upload.this, "updated info", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==100)
        {
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                callgallery();
            }
        }
    }
    public void callgallery()
    {
        Intent i=new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i,101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101&&resultCode==RESULT_OK)
        {
            imagepath=data.getData();
            img.setImageURI(imagepath);
            StorageReference filepath=sref.child("userimages").child(imagepath.getLastPathSegment());
            dialog.setMessage("uploading image");
            dialog.show();
            filepath.putFile(imagepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri download=taskSnapshot.getDownloadUrl();
                    dref.child("image_url").setValue(download.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(upload.this, "updated image path", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialog.cancel();
                }
            });

        }


    }
}
