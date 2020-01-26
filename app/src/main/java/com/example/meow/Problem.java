 package com.example.meow;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

 public class Problem extends AppCompatActivity {
     ImageView img;
     RelativeLayout rl;
     public String prob;
     private static final int ERROR_DIALOG_REQUEST = 9001;
     private static final String TAG = "ProblemActivity";

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_problem);


         img = (ImageView) findViewById(R.id.img);
         rl = (RelativeLayout) findViewById(R.id.rl);
     }
        public void getProblem(View view)
        {
         PopupMenu pm = new PopupMenu(Problem.this, img);
         pm.getMenuInflater().inflate(R.menu.menu_main, pm.getMenu());
         pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
             @Override
             public boolean onMenuItemClick(MenuItem item) {
                 switch (item.getItemId()) {
                     case R.id.puncture:
                         rl.setBackgroundColor(Color.RED);
                         prob="Puncture";
                         break;
                     case R.id.spark:
                         rl.setBackgroundColor(Color.BLUE);
                         prob="Spark Plug";

                         break;

                 }
                 if(isServicesOK()){
                     init();
                 }


                 return true;
             }
         });

         pm.show();
     }
     private void init(){
         Intent i=new Intent(Problem.this,PermissionsActivity.class);
                startActivity(i);
                 finish();
     }
     public boolean isServicesOK(){

         Log.d(TAG, "isServicesOK: checking google services version");

         int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(Problem.this);

         if(available == ConnectionResult.SUCCESS){
             //everything is fine and the user can make map requests
             Log.d(TAG, "isServicesOK: Google Play Services is working");
             return true;
         }
         else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
             //an error occured but we can resolve it
             Log.d(TAG, "isServicesOK: an error occured but we can fix it");
             Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(Problem.this, available, ERROR_DIALOG_REQUEST);
             dialog.show();
         }else{
             Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
         }
         return false;
     }
 }