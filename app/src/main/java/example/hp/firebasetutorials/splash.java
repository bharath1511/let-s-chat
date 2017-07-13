package example.hp.firebasetutorials;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView i1= (ImageView) findViewById(R.id.imageView3);
        Animation me= AnimationUtils.loadAnimation(getBaseContext(),R.anim.anima);
        i1.startAnimation(me);
        me.setFillAfter(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(splash.this,MainActivity.class));
                splash.this.finish();
            }
        },2000);
    }
}
