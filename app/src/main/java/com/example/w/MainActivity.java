package com.example.w;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final int RESULT_SPEECH = 1;
    private static final int NEW_PICTURE = 100;
    static final int REQUEST_IMAGE_CAPTURE = 101;
    ImageView vc;

    boolean hasCameraFlash = false;
    boolean flashOn = false;


    ArrayList<String> cmd = new ArrayList<>();

    LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vc = findViewById(R.id.vc);

        hasCameraFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        vc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                try {
                    startActivityForResult(intent, RESULT_SPEECH);


                }
                catch (ActivityNotFoundException e) {
                    Toast.makeText(MainActivity.this, "not clear", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RESULT_SPEECH) {
            if (resultCode == RESULT_OK && data != null) {
                cmd = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                if (cmd.contains("Browser")) {           //
                    Intent browse = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.google.co.in/"));
                    startActivity(browse);
                } else if (cmd.contains("contacts")) {
                    Intent cont = new Intent(Intent.ACTION_PICK);
                    cont.setType(ContactsContract.Contacts.CONTENT_TYPE);
                    startActivity(cont);
                } else if (cmd.contains("camera")) {          //
                    Intent cam = new Intent();
                    cam.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    dispatchTakePictureIntent();
                    startActivityForResult(cam, NEW_PICTURE);
                } else if (cmd.contains("gallery")) {
                    Intent gal = new Intent();
                    gal.setType("image/*");
                    //gal.setAction(Intent.ACTION_GET_CONTENT);
                    gal.setAction(Intent.ACTION_VIEW);
                    startActivity(gal);
                } else if (cmd.contains("maps")) {
                    Intent map = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:"));
                    startActivity(map);
                } else if (cmd.contains("call")) {
                    Intent cal = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:+91"));
                    startActivity(cal);
                }
                else if(cmd.contains("flash")){
                    if(hasCameraFlash) {
                        flashOn = true;
                        try {
                            flashLightOn();
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }

                }
                else if (cmd.contains("Instagram")) {
                    Uri uri = Uri.parse("http://instagram.com/");
                    Intent insta = new Intent(Intent.ACTION_VIEW, uri);
                    insta.setPackage("com.instagram.android");
                    try {
                        startActivity(insta);
                    }
                    catch(ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(("http://instagram.com/"))));
                    }
                }
                else if (cmd.contains("YouTube")) {
                    Uri uri = Uri.parse("http://youtube.com/");
                    Intent yt = new Intent(Intent.ACTION_VIEW, uri);
                    yt.setPackage("com.youtube.android");
                    try {
                        startActivity(yt);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(("http://youtube.com/"))));
                    }
                }
                else if (cmd.contains("Facebook")) {
                    Uri uri = Uri.parse("http://facebook.com/");
                    Intent fb = new Intent(Intent.ACTION_VIEW, uri);
                    fb.setPackage("com.facebook.katana");
                    try {
                        startActivity(fb);
                    }
                    catch(ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(("http://facebook.com/"))));
                    }
                }
                else if (cmd.contains("Twitter")) {
                    Uri uri = Uri.parse("http://twitter.com/");
                    Intent twt = new Intent(Intent.ACTION_VIEW, uri);
                    twt.setPackage("com.twitter.android");
                    try {
                        startActivity(twt);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(("http://twitter.com/"))));
                    }
                }
                else if (cmd.contains("teams")) {
                    Uri uri = Uri.parse("http://teams.com/");
                    Intent teams = new Intent(Intent.ACTION_VIEW, uri);
                    teams.setPackage("com.microsoft.teams");
                    try {
                        startActivity(teams);
                    }
                    catch(ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(("http://teams.com/"))));
                    }
                }
                else if (cmd.contains("email")) {
                    Intent gm=new Intent(Intent.ACTION_SEND);
                    String[] recipients={"mailto@gmail.com"};
                    gm.putExtra(Intent.EXTRA_EMAIL, recipients);
                    gm.putExtra(Intent.EXTRA_SUBJECT,"Subject text here...");
                    gm.putExtra(Intent.EXTRA_TEXT,"Body of the content here...");
                    gm.putExtra(Intent.EXTRA_CC,"mailcc@gmail.com");
                    gm.setType("text/html");
                    gm.setPackage("com.google.android.gm");
                    startActivity(Intent.createChooser(gm, "Send mail"));

                }
                else if (cmd.contains("WhatsApp")) {
                    Intent wp = new Intent();
                    wp.setAction(Intent.ACTION_SEND);
                    wp.putExtra(Intent.EXTRA_TEXT, "Type text here.");
                    wp.setType("text/plain");
                    wp.setPackage("com.whatsapp");
                    startActivity(Intent.createChooser(wp, ""));
                    startActivity(wp);
                }
                else if (cmd.contains("Snapchat")) {
                    try {
                        Intent snpcht = new Intent(Intent.ACTION_VIEW, Uri.parse("https://snapchat.com/add/" /* + SNapId */));
                        snpcht.setPackage("com.snapchat.android");
                        startActivity(snpcht);
                    } catch (Exception e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://snapchat.com/add/" )));
                    }
                }
                else if (cmd.contains("swiggy")) {
                    Uri uri = Uri.parse("http://swiggy.com/");
                    Intent swgy = new Intent(Intent.ACTION_VIEW, uri);
                    swgy.setPackage("com.swiggy.android");
                    try {
                        startActivity(swgy);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(("http://swiggy.com/"))));
                    }
                }
                else if (cmd.contains("Zomato")) {
                    Uri uri = Uri.parse("http://zomato.com/");
                    Intent zmto = new Intent(Intent.ACTION_VIEW, uri);
                    zmto.setPackage("com.zomato.android");
                    try {
                        startActivity(zmto);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(("http://zomato.com/"))));
                    }
                }
                else if (cmd.contains("ajio")) {
                    Uri uri = Uri.parse("http://ajio.com/");
                    Intent swgy = new Intent(Intent.ACTION_VIEW, uri);
                    swgy.setPackage("com.ril.ajio");
                    try {
                        startActivity(swgy);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(("http://ajio.com/"))));
                    }
                }
                else if (cmd.contains("Amazon")) {
                    Uri uri = Uri.parse("http://amazon.in/");
                    Intent amzn = new Intent(Intent.ACTION_VIEW, uri);
                    amzn.setPackage("in.amazon.mShop.and.roid.shopping");
                    try {
                        startActivity(amzn);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(("http://amazon.in/"))));
                    }
                }
                else if (cmd.contains("Flipkart")) {
                    Uri uri = Uri.parse("http://flipkart.com/");
                    Intent amzn = new Intent(Intent.ACTION_VIEW, uri);
                    amzn.setPackage("com.flipkart.android");
                    try {
                        startActivity(amzn);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(("http://flipkart.com/"))));
                    }
                }

                else if(cmd.contains("music")) {
                    Intent sptfy = new Intent(Intent.ACTION_VIEW, Uri.parse("spotify:play"));
                    startActivity(sptfy);
                }

                else if(cmd.contains("voice text")) {
                    Intent vt = new Intent(MainActivity.this,spktxt.class);
                    startActivity(vt);
                }
                else if (cmd.contains("SOS")) {
                    getlocation();
                }

            }
        }

        else if(requestCode == NEW_PICTURE) {
            if(resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                Log.d("tag","Absolute Url of Image is " + Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
            }
        }

    }



    private void flashLightOn() throws CameraAccessException {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String cameraId = cameraManager.getCameraIdList()[0];
        cameraManager.setTorchMode(cameraId,true);
        Toast.makeText(this, "FLash light On", Toast.LENGTH_SHORT).show();
    }

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        // File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void dispatchTakePictureIntent() {
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
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.vivi.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }



    @SuppressLint("MissingPermission")
    private void getlocation() {
        try {

            locationManager = (LocationManager) getApplication().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, MainActivity.this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, MainActivity.this);
            Toast.makeText(this, "location got", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            Toast.makeText(this, "location error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(this, ""+location.getLatitude()+" , "+location.getLongitude(), Toast.LENGTH_SHORT).show();
        try {
            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
            String address = addresses.get(0).getAddressLine(0);
            sendsms(address);

        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }

    private void sendsms(String msg) {
        try {
            String smstext = "\nIm in location :\n";
            smstext = smstext.concat(msg);
            SmsManager smsManager = SmsManager.getDefault();
            String number2 = "9659365376";
            smsManager.sendTextMessage(number2, null, smstext, null, null);
            String number3 = "984243952";
            smsManager.sendTextMessage(number3, null, smstext, null, null);
            Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show();
        }
        catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }
}