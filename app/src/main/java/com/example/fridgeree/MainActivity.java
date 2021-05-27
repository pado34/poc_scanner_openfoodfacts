package com.example.fridgeree;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private ImageView imageView;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PackageManager.PERMISSION_GRANTED);

        mQueue = Volley.newRequestQueue(this);
    }

    public void ScanButton(View view){

        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(intentResult!=null){
            if(intentResult.getContents()==null){
               textView.setText("Cancelled");
            }
            else{
                //textView.setText(intentResult.getContents());
                jsonParse(intentResult.getContents());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void jsonParse(String numeroean){
        String url = "https://world.openfoodfacts.org/api/v0/product/"+numeroean+".json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    JSONObject productname = response.getJSONObject("product");
                    String productnameString = productname.getString("product_name");
                    String imgurl = productname.getString("image_url");
                    textView.setText(productnameString);
                    Picasso.get().load(imgurl).into(imageView);
                }
                catch(Exception e){
                    textView.setText("error");
                }
                //JSONObject productname = response.getJSONObject("product");
                //String productnameString = productname.getString("product_name");
                //textView.setText(productnameString);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }
}