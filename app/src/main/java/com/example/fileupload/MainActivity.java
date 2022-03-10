package com.example.fileupload;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpHeaders;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.entity.UrlEncodedFormEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.EntityUtils;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
     ImageView takePhoto;
    static final int CAPTURE_IMAGE_REQUEST = 0;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int MY_GALLERY_REQUEST_CODE = 10;
    private static final String IMAGE_DIRECTORY_NAME = "VLEMONN";
    String mCurrentPhotoPath;
    int resStatus;
    String msg;
    Bitmap bt = null;
    File photoFile = null;
    File file1;
    Button Uploadohoto;
    String baseFront;
    TextView test;
    int uploadflag = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Uploadohoto = (Button) findViewById(R.id.enterYourdetailscontinueetailscontinue);
        Uploadohoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SaveAadhar().execute();
            }
        });
        takePhoto = (ImageView)  findViewById(R.id.takePhoto);
        test =(TextView) findViewById(R.id.test);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(MainActivity.this,"Choose Icon");
            }
        });

    }
    private void selectImage(Context context, String title) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @SuppressLint("UseCheckPermission")
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {

                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        checkPermission(Manifest.permission.CAMERA,
                                MY_CAMERA_REQUEST_CODE);
                    }else{
                        getCapture();

                    }

                } else if (options[item].equals("Choose from Gallery")) {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                MY_GALLERY_REQUEST_CODE);
                    }else {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, 1);
                    }

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] { permission },
                    requestCode);
        }
        else {
            Toast.makeText(MainActivity.this,
                    "Permission already granted",
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void getCapture() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            captureImage();
        }
        else
        {
            captureImage2();
        }
    }


    private void captureImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                            MainActivity.this.getPackageName() + ".provider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
                }
            } catch (Exception ex) {
                displayMessage(getBaseContext(),ex.getMessage().toString());
            }
        }

    }
    private void captureImage2() {
        try {

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            photoFile = createImageFile4();
            if(photoFile!=null)
            {
                Log.i("gok",photoFile.getAbsolutePath());
                Uri photoURI  = Uri.fromFile(photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, CAPTURE_IMAGE_REQUEST);
            }
        }
        catch (Exception e)
        {
            displayMessage(MainActivity.this,"Camera is not available."+e.toString());
        }
    }

    private void displayMessage(Context context, String message) {
        //Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }
    private File createImageFile4() {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                displayMessage(MainActivity.this,"Unable to create directory.");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    class SaveAadhar extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... strings) {
            PostAadhar(strings);
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            if(resStatus == 0){
                displayMessage(MainActivity.this,msg);
            }else{
                displayMessage(MainActivity.this,msg);
            }

        }

    }

    private void PostAadhar(String[] strings) {
        try
        {
            Log.d("gokulnathan","yes");
            String url = "https://dev-eduvanz.ind2s.sfdc-y37hzm.force.com/services/apexrest/HerokuFileUpload";
            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost(url);
          String boundary = "-------------" + System.currentTimeMillis();
            httpPost.setHeader("Content-type", "multipart/form-data; boundary="+boundary);
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE,"application/json");
            httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer "+"00D71000000IU7h!AQEAQJmqmfgS_tvkfoPxiGqlOXIrozzijKtc2aslTk6vYLMWgAv72sGjcfwMEhp1t2OxdzZUPPe0Tlu8nS.xQ5JITfoWYJRN");
            Log.d("response","post data");

           MultipartEntityBuilder builder = MultipartEntityBuilder.create();

             builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            //MultipartEntity builder = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            if (file1 != null) {
                //builder.addBinaryBody("image", file1);
            }

            if(uploadflag == 1)
            {
                builder.addTextBody("doctype","Aadhar Front");
                builder.addTextBody("base64", baseFront);
            }else{
                builder.addTextBody("doctype","Aadhar Back");
                builder.addTextBody("base64", baseFront);
            }
            builder.addTextBody("parent_id","00171000005MEj0AAG");
            builder.addTextBody("fname", "eduvan-web-1642500056727");

            builder.setBoundary(boundary);
            // error
            httpPost.setEntity((HttpEntity) builder.build());
              //eroor class cast exception


           HttpResponse response = httpClient.execute(httpPost);
            int status = response.getStatusLine().getStatusCode();
            if(status == 200) {
                HttpEntity entity1 = response.getEntity();

                String data = EntityUtils.toString(entity1);
                Log.d("response",data);
                JSONObject jsono = new JSONObject(data);
                resStatus = jsono.getInt("status");
                String message = jsono.getString("msg");
                msg =message;

            }else{
                msg = "Failed to connect with server";
            }

        }
        catch(Exception e)
        {
            System.out.println(e);
        }


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == Activity.RESULT_OK) {
                        bt = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                        takePhoto.setImageBitmap(bt);
                        file1 = photoFile;
                    }
                    break;
                case 1:
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String rname = ImageFilePath.getPath(MainActivity.this,selectedImage);

                        if (selectedImage != null) {
                            try{
                                //

                                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),selectedImage);
                                // initialize byte stream
                                ByteArrayOutputStream stream=new ByteArrayOutputStream();
                                // compress Bitmap
                                bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
                                // Initialize byte array
                                byte[] bytes=stream.toByteArray();
                                // get base64 encoded string
                                baseFront= Base64.encodeToString(bytes,Base64.DEFAULT);
                               // test.setText(baseFront);
                                //
                                bt = getBitmapFromUri(selectedImage);
                            }catch (Exception er)
                            {

                            }
                            takePhoto.setImageBitmap(bt);
                            file1 = new File(rname);
                        }

                    }
                    break;
            }
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
}