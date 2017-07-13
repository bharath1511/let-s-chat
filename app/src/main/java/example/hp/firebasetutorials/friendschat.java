package example.hp.firebasetutorials;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

public class friendschat extends AppCompatActivity {
    TextView person_name,person_email;
    private RecyclerView recyclerView;
    private  FirebaseAuth.AuthStateListener mstate;
    TextView nofriends;
    ProgressDialog dialog;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef,dref,ref;
    ValueEventListener vlis;
    FirebaseAuth auth;
    View.OnClickListener lis;
    public FirebaseRecyclerAdapter<Show_Chat_Activity_Data_Items, friendschat.Show_friendsChat_ViewHolder> mFirebaseAdapter;
    ProgressBar progressBar;
    LinearLayoutManager mLinearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendschat );
        nofriends= (TextView) findViewById(R.id.friendstextView);
        auth=FirebaseAuth.getInstance();
        this.setTitle(" Friends");
        mstate=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(auth.getCurrentUser()==null)
                {

                    startActivity(new Intent(friendschat.this,MainActivity.class));
                    friendschat.this.finish();
                }
            }
        };


        firebaseDatabase = FirebaseDatabase.getInstance();
String id=FirebaseAuth.getInstance().getCurrentUser().getEmail().replace("@","").replace(".","");
        myRef =  FirebaseDatabase.getInstance().getReference().child("friends").child(id);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChildren())
                {
                    recyclerView.setVisibility(View.INVISIBLE);
                    nofriends.setVisibility(View.VISIBLE);

                    if(dialog!=null)
                    {
                        dialog.cancel();
                    }
                }else
                {
                    nofriends.setVisibility(View.GONE);
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
        recyclerView = (RecyclerView)findViewById(R.id.show_friendschat_recyclerView);
//        recyclerView.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(friendschat.this);


        mFirebaseAdapter = new FirebaseRecyclerAdapter<Show_Chat_Activity_Data_Items, friendschat.Show_friendsChat_ViewHolder>(
                Show_Chat_Activity_Data_Items.class, R.layout.friends_single_item, Show_friendsChat_ViewHolder.class, myRef) {

            public void populateViewHolder(final Show_friendsChat_ViewHolder viewHolder, Show_Chat_Activity_Data_Items model, int position) {
                //Log.d("LOGGED", "populateViewHolder Called: ");
                final int pos=position;
                if(dialog!=null) {
                    dialog.cancel();
                }
                if (!model.getName().equals("Null")) {
                    viewHolder.Person_Name(model.getName());
                    viewHolder.Person_Image(model.getImage_Url());
                    viewHolder.sub.setImageResource(R.drawable.ic_remove_circle_black_24dp);
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
                 lis=new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(v.getId()==viewHolder.sub.getId())
                        { PopupMenu popup=new PopupMenu(friendschat.this,viewHolder.sub);
                            popup.inflate(R.menu.removemenu);
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    dialog =new ProgressDialog(friendschat.this);
                                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    dialog.setMessage("removing ......");
                                    dialog.setCancelable(false);
                                    dialog.show();
                                    DatabaseReference ref = mFirebaseAdapter.getRef(pos);

                                    ref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(!task.isSuccessful())
                                            { if(dialog!=null)
                                            {
                                                dialog.cancel();
                                            }
                                                Toast.makeText(friendschat.this, "try again can't be removed now", Toast.LENGTH_SHORT).show();
                                            }else  if(task.isSuccessful())
                                            {
                                            }
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            mFirebaseAdapter.notifyItemRemoved(pos);
                                            mFirebaseAdapter.notifyItemRangeRemoved(pos,getItemCount());
                                            mFirebaseAdapter.notifyDataSetChanged();
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {

                                                    if(dialog!=null)
                                                    {
                                                        dialog.cancel();
                                                    }
                                                }
                                            },500);

                                            Toast.makeText(friendschat.this, "removed as friend", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    return false;
                                }
                            });
                            popup.show();

                        }else  if(v.getId()==viewHolder.itemView.getId())
                        {
                             ref = mFirebaseAdapter.getRef(pos);

                            ref.addValueEventListener(vlis=new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChildren()&&dataSnapshot.exists()){
                                        String retrieve_name = dataSnapshot.child("Name").getValue(String.class);
                                        String retrieve_Email = dataSnapshot.child("Email").getValue(String.class);
                                        String retrieve_url = dataSnapshot.child("Image_URL").getValue(String.class);



                                        if(retrieve_Email!=null&&retrieve_name!=null) {
                                            Intent intent = new Intent(getApplicationContext(), chatconversation.class);
                                            intent.putExtra("image_id", retrieve_url);
                                            intent.putExtra("email", retrieve_Email);
                                            intent.putExtra("name", retrieve_name);

                                            startActivity(intent);
                                        }                            }}

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                    }
                };

                viewHolder.sub.setOnClickListener(lis);

                //OnClick Item
                viewHolder.itemView.setOnClickListener(lis);
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
    public static class Show_friendsChat_ViewHolder extends RecyclerView.ViewHolder {
        TextView person_name, person_email;
        ImageView person_image,sub;
        LinearLayout layout;
        LinearLayout.LayoutParams params;

        public Show_friendsChat_ViewHolder(View itemView) {
            super(itemView);
            person_name = (TextView) itemView.findViewById(R.id.friendschat_person_name);
            person_email = (TextView) itemView.findViewById(R.id.friendschat_person_email);
            sub = (ImageView) itemView.findViewById(R.id.friendsadding);
            person_image = (ImageView) itemView.findViewById(R.id.friendschat_person_image);
            layout = (LinearLayout)itemView.findViewById(R.id.show_friendschat_single_item_layout);
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
        getMenuInflater().inflate(R.menu.friendsmenu,menu);
        return  super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.updateprofile)
        {
            startActivity(new Intent(friendschat.this,dashboard.class));

        }else if(item.getItemId()==R.id.Signout)
        {     auth.signOut();


        }
        else  if(item.getItemId()==R.id.users)
        {
            startActivity(new Intent(friendschat.this,chat.class));
            friendschat.this.finish();

        }
        return super.onOptionsItemSelected(item);
    }
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(mstate);
    }
}

