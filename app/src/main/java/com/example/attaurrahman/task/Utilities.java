package com.example.attaurrahman.task;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

/**
 * Created by kashif on 1/10/18.
 */

public class Utilities {
    private static final int REQUEST_LOCATION = 1;

    static double lattitude;
    static double longitude;

    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    public static Button button;
    public static ImageView imageView;
    public static TextView textView;
    public static EditText editText;
    public static LinearLayout linearLayout;
    public static RadioButton radioButton;
    public static RadioGroup radioGroup;
    public static TextWatcher textWatcher;
    private static final Pattern EMAIL_PATTERN = Pattern
            .compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");


    public static SharedPreferences.Editor putValueInEditor(Context context) {
        sharedPreferences = getSharedPreferences(context);
        editor = sharedPreferences.edit();
        return editor;
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        //sharedPreferences = context.getSharedPreferences(Configuration.MY_PREF, 0);
        return context.getSharedPreferences(Configurations.MY_PREF, 0);
    }


    public static Button buttonDeclaration(int buttonID, View view) {
        button = (Button) view.findViewById(buttonID);
        return button;
    }

    public static ImageView imageViewDeclaration(int imageViewID, View view) {
        imageView = (ImageView) view.findViewById(imageViewID);
        return imageView;
    }

    public static TextView textViewDeclaration(int textViewID, View view) {
        textView = (TextView) view.findViewById(textViewID);

        return textView;
    }

    public static EditText editTextDeclaration(int edtihTextID, View view) {

        editText = (EditText) view.findViewById(edtihTextID);

        return editText;
    }

    public static LinearLayout linearLayoutDeclaration(int linearLayoutID, View view) {
        linearLayout = (LinearLayout) view.findViewById(linearLayoutID);
        return linearLayout;
    }

    public static RadioButton radioButtonDeclaration(int radioButtonID, View view) {
        radioButton = (RadioButton) view.findViewById(radioButtonID);
        return radioButton;
    }

    public static RadioGroup radioGroupDeclaration(int radioGroupID, View view) {
        radioGroup = (RadioGroup) view.findViewById(radioGroupID);
        return radioGroup;
    }

    public static TextWatcher textWatcherDeclaration(int textWatcherID, View view) {
        textWatcher = (TextWatcher) view.findViewById(textWatcherID);
        return textWatcher;
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }


    private static class Configurations {
        public static final String MY_PREF = "My_Pref";
    }


    public static byte[] getImageBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public static void getLocation(Context context, LocationManager locationManager, View view) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);


            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();

                lattitude = latti;
                longitude = longi;
                Utilities.putValueInEditor(context).putString("lattitude", String.valueOf(latti)).commit();
                Utilities.putValueInEditor(context).putString("longitude", String.valueOf(longi)).commit();
                //Utilities.imageViewDeclaration(R.id.iv_current_check_location, view).setVisibility(View.VISIBLE);


            } else if (location1 != null) {
                double latti = location1.getLatitude();
                double longi = location1.getLongitude();

                lattitude = latti;
                longitude = longi;
                Toast.makeText(context, String.valueOf(latti) + "2", Toast.LENGTH_SHORT).show();

            } else if (location2 != null) {
                double latti = location2.getLatitude();
                double longi = location2.getLongitude();
                lattitude = latti;
                longitude = longi;

                Toast.makeText(context, String.valueOf(latti) + "3", Toast.LENGTH_SHORT).show();


            } else {

                Toast.makeText(context, "Unble to Trace your location", Toast.LENGTH_SHORT).show();

            }
        }
    }

}
