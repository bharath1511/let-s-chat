package example.hp.firebasetutorials;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class chat extends AppCompatActivity {
    TextView person_name,person_email;
   private RecyclerView recyclerView;
    ProgressDialog dialog;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef,dref,ref;
    ValueEventListener vlis;
    FirebaseAuth.AuthStateListener mstate;
    FirebaseAuth auth;
    public FirebaseRecyclerAdapter<Show_Chat_Activity_Data_Items, Show_Chat_ViewHolder> mFirebaseAdapter;
    ProgressBar progressBar;
    TextView nousers;
    LinearLayoutManager mLinearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        firebaseDatabase = FirebaseDatabase.getInstance();
        nousers= (TextView) findViewById(R.id.textView);
auth=FirebaseAuth.getInstance();
        this.setTitle(" All Users");
        mstate=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(auth.getCurrentUser()==null)
                {

                    startActivity(new Intent(chat.this,MainActivity.class));
                    chat.this.finish();
                }
            }
        };


        myRef =  FirebaseDatabase.getInstance().getReference().child("users");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChildren())
                {
                    recyclerView.setVisibility(View.INVISIBLE);
                    nousers.setVisibility(View.VISIBLE);

                    if(dialog!=null)
                    {
                        dialog.cancel();
                    }
                }else
                {
                    nousers.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

       // Toast.makeText(this, myRef.child("blosterbharathgmailcom").child("Name").getKey(), Toast.LENGTH_SHORT).show();

        dialog =new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("loading ......");
        dialog.setCancelable(false);
        dialog.show();



        //Recycler View
        recyclerView = (RecyclerView)findViewById(R.id.show_chat_recyclerView);
//        recyclerView.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(chat.this);


        mFirebaseAdapter = new FirebaseRecyclerAdapter<Show_Chat_Activity_Data_Items, Show_Chat_ViewHolder>(
                Show_Chat_Activity_Data_Items.class, R.layout.show_chat_single_item, Show_Chat_ViewHolder.class, myRef) {

            public void populateViewHolder(final Show_Chat_ViewHolder viewHolder, Show_Chat_Activity_Data_Items model, final int position) {
                //Log.d("LOGGED", "populateViewHolder Called: ");
                if(dialog!=null) {
                    dialog.cancel();
                }
                if (!model.getName().equals("Null")) {
                    viewHolder.Person_Name(model.getName());
                    viewHolder.Person_Image(model.getImage_Url());
                    //viewHolder.Person_Email(model.getEmail());
                    if(model.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                    {
                        //viewHolder.itemView.setVisibility(View.GONE);
                        viewHolder.Layout_hide();

                        //recyclerView.getChildAdapterPosition(viewHolder.itemView.getRootView());
                        // viewHolder.itemView.set;


                    }
                    else
                        viewHolder.Person_Email(model.getEmail());
                }
               viewHolder.add.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {

                      PopupMenu popup=new PopupMenu(chat.this,viewHolder.add);
                       popup.inflate(R.menu.addmenu);
                       popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                           @Override
                           public boolean onMenuItemClick(MenuItem item) {
                               dialog =new ProgressDialog(chat.this);
                               dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                               dialog.setMessage("adding ......");
                               dialog.setCancelable(false);
                               dialog.show();
                                ref = mFirebaseAdapter.getRef(position);
                               ref.addValueEventListener(vlis=new ValueEventListener() {
                                   @Override
                                   public void onDataChange(DataSnapshot dataSnapshot) {

                                       String retrieve_name = dataSnapshot.child("Name").getValue(String.class);
                                       String retrieve_Email = dataSnapshot.child("Email").getValue(String.class);
                                       String retrieve_url = dataSnapshot.child("Image_Url").getValue(String.class);
                                       dref = FirebaseDatabase.getInstance().getReference();
                                       String userid=retrieve_Email.replace("@","").replace(".","");
                                       auth=FirebaseAuth.getInstance();
                                       String authuserid=auth.getCurrentUser().getEmail().replace("@","").replace(".","");
                                       DatabaseReference ref=dref.child("friends").child(authuserid).child(userid);
                                       ref.child("Email").setValue(retrieve_Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               if(!task.isSuccessful())
                                               {
                                                   Toast.makeText(chat.this, "try again to add", Toast.LENGTH_SHORT).show();

                                               }
                                           }
                                       });
                                       ref.child("Name").setValue(retrieve_name).addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {

                                               if(!task.isSuccessful())
                                               {
                                                   Toast.makeText(chat.this, "try again to add", Toast.LENGTH_SHORT).show();

                                               }else if(task.isSuccessful())
                                               {
                                                   Toast.makeText(chat.this, "succesfully added", Toast.LENGTH_SHORT).show();
                                               }
                                           }
                                       });;
                                       ref.child("Image_Url").setValue(retrieve_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               if(!task.isSuccessful())
                                               {
                                                   Toast.makeText(chat.this, "try again to add", Toast.LENGTH_SHORT).show();

                                               }
                                               if(dialog!=null)
                                               {
                                                   dialog.cancel();
                                               }
                                           }
                                       });

                                   }

                                   @Override
                                   public void onCancelled(DatabaseError databaseError) {

                                   }
                               });
                               return false;
                           }
                       });
                  popup.show(); }
               });

                //OnClick Item
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(final View v) {

                        DatabaseReference ref = mFirebaseAdapter.getRef(position);
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                String retrieve_name = dataSnapshot.child("Name").getValue(String.class);
                                String retrieve_Email = dataSnapshot.child("Email").getValue(String.class);
                                String retrieve_url = dataSnapshot.child("Image_URL").getValue(String.class);



                                Intent intent = new Intent(getApplicationContext(), chatconversation.class);
                                intent.putExtra("image_id", retrieve_url);
                                intent.putExtra("email", retrieve_Email);
                                intent.putExtra("name", retrieve_name);

                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                mFirebaseAdapter.notifyDataSetChanged();
                int position =
                        mLinearLayoutManager.findFirstVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                recyclerView.scrollToPosition(position);

            }
        });
//        mLinearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLinearLayoutManager);

        recyclerView.setAdapter(mFirebaseAdapter);



    }


    //View Holder For Recycler View
    public static class Show_Chat_ViewHolder extends RecyclerView.ViewHolder {
         TextView person_name, person_email;
          ImageView person_image,add;
          LinearLayout layout;
         LinearLayout.LayoutParams params;

        public Show_Chat_ViewHolder(View itemView) {
            super(itemView);
            person_name = (TextView) itemView.findViewById(R.id.chat_person_name);
            person_email = (TextView) itemView.findViewById(R.id.chat_person_email);
            add = (ImageView) itemView.findViewById(R.id.adding);
            person_image = (ImageView) itemView.findViewById(R.id.chat_person_image);
            layout = (LinearLayout)itemView.findViewById(R.id.show_chat_single_item_layout);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }


        public void Person_Name(String title) {

            person_name.setText(title);

        }
        public void Layout_hide() {
            params.height = 0;

            layout.setLayoutParams(params);

        }


        public void Person_Email(String title) {
            person_email.setText(title);
        }


        public void Person_Image(String url) {

            if (!url.equals("Null")) {
                Glide.with(itemView.getContext())
                        .load(url)
                        .crossFade()
                        .thumbnail(0.5f)
                        .placeholder(R.drawable.chat)

                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(person_image);
            }

        }


    }
    @Override
    protected void onStop() {
        super.onStop();
        if(ref!=null)
        {


        ref.removeEventListener(vlis);
    }
        auth.removeAuthStateListener(mstate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.usersmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.usersupdateprofile)
        {
            startActivity(new Intent(chat.this,dashboard.class));

        }else if(item.getItemId()==R.id.userssignout)
        {     auth.signOut();


        }
        else  if(item.getItemId()==R.id.usersfriends)
        {
            startActivity(new Intent(chat.this,friendschat.class));
            chat.this.finish();

        }
        return super.onOptionsItemSelected(item);
    }
    protected void onStart() {
        super.onStart();

            auth.addAuthStateListener(mstate);
    }
}

