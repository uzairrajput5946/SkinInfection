package com.example.skininfectiondetection;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
int CAMERA_PIC=1;
int SELECT_PIC=2;

    int PICK_IMAGE_REQUEST = 111;
    String URL ="http://66.71.52.154:3000/upload_image";
    Bitmap bitmap;
    ProgressDialog progressDialog;
    ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         image=(ImageView)findViewById(R.id.image);
        TextView text=(TextView)findViewById(R.id.result) ;
        Button takepicture=(Button)findViewById(R.id.button1);
        takepicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkSelfPermission( android.Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_PIC);
                }
                else {
                    requestPermissions(new String[]{android.Manifest.permission.CAMERA},100);
                }

            }
        });
        Button select=(Button) findViewById(R.id.button2);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,SELECT_PIC);
            }
        });

        Button upload=(Button) findViewById(R.id.button3);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Uploading, please wait...");
                progressDialog.show();

                //converting image to base64 string
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                encryption myCrypt = new encryption();
                String encryptedString;
                /* Encrypt */
                try {
                    // encryptedString = new String(myCrypt.encrypt(imageString), StandardCharsets.UTF_8);
                     encryptedString = Base64.encodeToString(myCrypt.encrypt(imageString), Base64.DEFAULT);
//Log.i("abhinav","here");
//Log.i("abhinav","start"+encryptedString);

                    //encryptedString = (String) myCrypt.encrypt(imageString) ;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }


                JSONObject jsonObject = new JSONObject();
                String imgname = String.valueOf(Calendar.getInstance().getTimeInMillis());
                try {
                    //jsonObject.put("name", imgname);
                    jsonObject.put("image", encryptedString);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                JsonObjectRequest request = new JsonObjectRequest( Request.Method.POST,URL,jsonObject, new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject s) {
                        progressDialog.dismiss();

                        try {
                            text.setText(s.getString("result"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }


                    }
                },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MainActivity.this, "Some error occurred -> "+volleyError, Toast.LENGTH_LONG).show();;
                    }
                })

                ;

                RequestQueue rQueue = Volley.newRequestQueue(MainActivity.this);
                rQueue.add(request);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==CAMERA_PIC && resultCode==RESULT_OK)
        {
            bitmap=(Bitmap) data.getExtras().get("data");
            image.setImageBitmap(bitmap);
        }
        if(requestCode==SELECT_PIC && resultCode==RESULT_OK)
        {
                Uri selectedImage =  data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                if (selectedImage != null) {
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        bitmap= BitmapFactory.decodeFile(picturePath);
                        image.setImageBitmap(bitmap);
                        cursor.close();
                    }
                }

            }

        super.onActivityResult(requestCode, resultCode, data);
    }


}