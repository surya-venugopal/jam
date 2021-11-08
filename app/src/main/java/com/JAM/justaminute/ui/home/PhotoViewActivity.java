package com.JAM.justaminute.ui.home;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.JAM.justaminute.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

public class PhotoViewActivity extends AppCompatActivity implements View.OnTouchListener {
    PhotoView photoView;
    ConstraintLayout constraintLayout;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoview);
        photoView=findViewById(R.id.image);
        constraintLayout=findViewById(R.id.con1);
        try {
            Intent intent=getIntent();
            String id=intent.getStringExtra("photo");
            Picasso.get().load(id).into(photoView);
        }
        catch (Exception e){
            finish();
        }

        constraintLayout.setOnTouchListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.photo_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId())
        {
            case R.id.rotate:
                Toast.makeText(this, "rotate", Toast.LENGTH_SHORT).show();
                photoView.animate().rotationBy(90f);
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN)
        {
            toggleActionBar();
        }
        return false;
    }
    private void toggleActionBar()
    {
        ActionBar actionBar=getActionBar();
        if(actionBar!=null)
        {
            if(actionBar.isShowing())
            {
                actionBar.hide();
            }
            else
            {
                actionBar.show();
            }
        }

    }
}
