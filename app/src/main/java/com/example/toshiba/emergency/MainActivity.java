package com.example.toshiba.emergency;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.toshiba.emergency.R.id.location;
import static com.example.toshiba.emergency.R.id.textLabel;


public class MainActivity extends AppCompatActivity {

    //Camera Initial Launch:: Permission Check
    //Saves permissions
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    public static final String ALLOW_KEY = "ALLOWED";
    public static final String CAMERA_PREF = "camera_pref";
    private ImageView img;
    private TextView loc;
    //private ImageView imageView;
    private Button azurecall;
    LocationManager mlocManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = (ImageView) findViewById(R.id.viewImage);
        loc = (TextView) findViewById(location);
        //imageView = (ImageView) findViewById(R.id.ImageLabel);
        //imageView.setImageResource(R.drawable.logo);
        azurecall = (Button) findViewById(R.id.emergencyCall);
        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


        if (savedInstanceState == null) {


            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (getFromPref(this, ALLOW_KEY)) {
                    showSettingsAlert();
                } else if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA)

                        != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.CAMERA)) {
                        showAlert();
                    } else {

                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);
                    }
                }
            } else {
                //has permissions already good to go
                openCamera();

            }
        }
    }


    public class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            String text = "Location: " + "Latitude: " + location.getLatitude() + " Longtitude: " + location.getLongitude();
            loc.setText(text);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    //save permission once allowed
    public static void saveToPreferences(Context context, String key, Boolean allowed) {
        SharedPreferences myPrefs = context.getSharedPreferences(CAMERA_PREF,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putBoolean(key, allowed);
        prefsEditor.apply();
    }

    public static Boolean getFromPref(Context context, String key) {
        SharedPreferences myPrefs = context.getSharedPreferences(CAMERA_PREF,
                Context.MODE_PRIVATE);
        return (myPrefs.getBoolean(key, false));
    }


    private void showAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Camera.");

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ALLOW",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);

                    }
                });
        alertDialog.show();
        //openCamera();
    }

    private void showSettingsAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Camera.");

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //finish();
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SETTINGS",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startInstalledAppDetailsActivity(MainActivity.this);
                    }
                });

        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                for (int i = 0, len = permissions.length; i < len; i++) {
                    String permission = permissions[i];

                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        boolean
                                showRationale =
                                ActivityCompat.shouldShowRequestPermissionRationale(
                                        this, permission);

                        if (showRationale) {
                            showAlert();
                        } else if (!showRationale) {
                            saveToPreferences(MainActivity.this, ALLOW_KEY, true);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static void startInstalledAppDetailsActivity(final Activity context) {
        if (context == null) {
            return;
        }

        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }


/*
    private void openCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        MainActivity.this.startActivityForResult(intent, Request_image);
    }
    */

    String mCurrentPhotoPath;
    Uri photoURI;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.d("check", "url: " + image.getAbsolutePath());
        return image;
    }

    static final int REQUEST_PHOTO = 1;


    private void openCamera() {
        //"android.media.action.IMAGE_CAPTURE"
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);


                try {
                    startActivityForResult(takePictureIntent, REQUEST_PHOTO);
                    Thread.sleep(6500);
                } catch (Exception e) {

                }

                Log.d("check", "url: " + mCurrentPhotoPath);

                //onActivityResult(REQUEST_PHOTO,RESULT_OK,takePictureIntent,mCurrentPhotoPath);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_PHOTO && resultCode == RESULT_OK) {
            Log.d("ok", "URL: " + mCurrentPhotoPath);
            //textLabel.setText(mCurrentPhotoPath);
        }

        Bitmap d = new BitmapDrawable(mCurrentPhotoPath).getBitmap();
        int nh = (int) (d.getHeight() * (512.0 / d.getWidth()));
        Bitmap scaled = Bitmap.createScaledBitmap(d, 512, nh, true);
        img.setImageBitmap(scaled);

        LocationListener mlocklistener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocklistener);
        Location last = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        final String location = "Latitude: " + last.getLatitude() + "\n" + " Longtitude: " + last.getLongitude();
        //loc.setText(location);

        azurecall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Thread thread2 = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HttpClient httpclient = new DefaultHttpClient();

                            try {

                                HttpPost request = new HttpPost("https://southcentralus.api.cognitive.microsoft.com/customvision/v1.0/Prediction/b9273b4c-3c93-467c-b06a-f0cd41b97a57/inline/image");
                                Log.d("OKAY", "Check2");
                                request.setHeader("Content-Type", "application/octet-stream");
                                request.setHeader("Prediction-key", "8c093ff068f147da9f1db0a95831ae43");
                                Log.d("OKAY", "Check3");
                                File file = new File(mCurrentPhotoPath);
                                FileEntity reqEntity = new FileEntity(file,"application/octet-stream");
                                Log.d("OKAY", "Check4");

                                request.setEntity(reqEntity);
                                Log.d("OKAY", "Check5");
                                HttpResponse response = httpclient.execute(request);
                                Log.d("OKAY", "Check6");
                                HttpEntity entity = response.getEntity();
                                Log.d("OKAY", "Check7");

                                Log.d("OKAY", "Check:" + response.getStatusLine());

                                if (entity != null) {
                                    Log.d("Lol", "WORKED:");

                                    JSONObject result = new JSONObject(EntityUtils.toString(entity));
                                    JSONArray tokenList= result.getJSONArray("Predictions");
                                    JSONObject oj = tokenList.getJSONObject(0);
                                    String token = oj.getString("Tag");
                                    String precision = oj.getString("Probability");

                                    Log.d("Token", "Check:" + token);

                                    HttpPost requests = new HttpPost("https://utils.lib.id/sms/");
                                    JSONObject object = new JSONObject();
                                    String message;

                                    Log.d("OKAY", "Check PLS");
                                    object.put("to","4168963183");

                                    String alert = "-";
                                    if(Double.parseDouble(precision) <0.5) {
                                        alert = " Most likely not an incident!";
                                    }

                                    object.put("body",token +"\n" + "At " + location + "\n" + "with Precision: " + precision + "\n" + alert);


                                    message = object.toString();
                                    requests.setEntity(new StringEntity(message,"UTF8"));
                                    requests.setHeader("Content-type","application/json");
                                    HttpResponse resp = httpclient.execute(requests);


                                    if (resp!= null) {
                                        Log.d("Worked", "WORKsss: ");
                                    }
                                }
                            } catch (Exception e) {
                                Log.d("DIDNT Lol", "Okay:" + mCurrentPhotoPath);
                                //Uri uri = Uri.parse("https://southcentralus.api.cognitive.microsoft.com/customvision/v1.0/Prediction/Emergency/inline/"+ mCurrentPhotoPath);
                            }
                        }
                    });
                    thread2.start();
                Toast.makeText(MainActivity.this,"Sent SOS Alert!",Toast.LENGTH_LONG).show();
                }
            });
        }


}



