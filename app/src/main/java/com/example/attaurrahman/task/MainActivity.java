package com.example.attaurrahman.task;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    TextView mStatusView, tvNoiseDetector, tvLocation, tvTimeStamp;
    MediaRecorder mRecorder;
    Thread runner;
    private static double mEMA = 0.0;
    static final private double EMA_FILTER = 0.6;
    String strNoise, strLatLon, strtime;
    File rootFile, CsvFile;
    FileWriter writer;
    Boolean aBoolean;
    double lattitude;
    double longitude;
    LocationManager locationManager;
    private static final int REQUEST_LOCATION = 1;
    Typeface typeface, typeface2;
    //Delimiter used in CSV file
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";

    //CSV file header
    private static final String FILE_HEADER = "id,firstName,lastName,gender,age";

    final Runnable updater = new Runnable() {

        public void run() {
            updateTv();
        }

        ;
    };


    final Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStatusView = findViewById(R.id.status);
        tvNoiseDetector = findViewById(R.id.tv_noise_detector);
        tvLocation = findViewById(R.id.tv_location);
        tvTimeStamp = findViewById(R.id.tv_time_stamp);



        typeface = Typeface.createFromAsset(this.getAssets(), "billabong.ttf");
        typeface2 = Typeface.createFromAsset(this.getAssets(), "SanFrancisco.otf");
        tvTimeStamp.setTypeface(typeface2);
        tvLocation.setTypeface(typeface2);
        mStatusView.setTypeface(typeface2);
        tvNoiseDetector.setTypeface(typeface);
        if (runner == null) {
            runner = new Thread() {
                public void run() {
                    while (runner != null) {
                        try {
                            Thread.sleep(1000);
                            Log.i("Noise", "Tock");
                        } catch (InterruptedException e) {
                        }
                        ;
                        mHandler.post(updater);
                    }
                }
            };
            runner.start();
            Log.d("Noise", "start runner()");
        }
    }

    public void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

        } else {
            startRecorder();
        }
    }

    public void onPause() {
        super.onPause();
        stopRecorder();
    }

    public void startRecorder() {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            try {
                mRecorder.prepare();
            } catch (java.io.IOException ioe) {
                android.util.Log.e("[Monkey]", "IOException: " + android.util.Log.getStackTraceString(ioe));

            } catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " + android.util.Log.getStackTraceString(e));
            }
            try {
                mRecorder.start();
            } catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " + android.util.Log.getStackTraceString(e));
            }

            //mEMA = 0.0;
        }

    }

    public void stopRecorder() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 0) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),
                        "Application will not have audio on record", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void updateTv() {

        strNoise = Double.toString((getAmplitudeEMA())) + " dB";

        mStatusView.setText(strNoise);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "No GPS", Toast.LENGTH_SHORT).show();

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
        }
        generateNoteOnSD();


    }

    public double soundDb(double ampl) {
        return 20 * Math.log10(getAmplitudeEMA() / ampl);
    }

    public double getAmplitude() {
        if (mRecorder != null)
            return (mRecorder.getMaxAmplitude());
        else
            return 0;

    }

    public double getAmplitudeEMA() {
        double amp = getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }


    public void generateNoteOnSD() {


        try {

            rootFile = new File(Environment.getExternalStorageDirectory(), "Noise Detector");

            if (!rootFile.exists()) {
                rootFile.mkdirs();
                Utilities.putValueInEditor(this).putBoolean("title", true).commit();
            }
            CsvFile = new File(rootFile, "Noise Detector" + ".CSV");
            if (!CsvFile.exists()) {
                Utilities.putValueInEditor(this).putBoolean("title", true).commit();
            }
            writer = new FileWriter(CsvFile, true);
            aBoolean = Utilities.getSharedPreferences(this).getBoolean("title", false);
            if (aBoolean) {
                writer.append("Noise");
                writer.append(COMMA_DELIMITER);
                writer.append("Location");
                writer.append(COMMA_DELIMITER);
                writer.append(COMMA_DELIMITER);
                writer.append("Time Stamp");
                writer.append(NEW_LINE_SEPARATOR);
            }
            Date currentTime = Calendar.getInstance().getTime();
            strtime = String.valueOf(currentTime);

            strLatLon = String.valueOf(lattitude + "," + longitude);


            writer.append(strNoise);
            writer.append(COMMA_DELIMITER);
            writer.append(strLatLon);
            writer.append(COMMA_DELIMITER);
            writer.append(strtime);
            writer.append(NEW_LINE_SEPARATOR);


            writer.flush();
            writer.close();

            tvLocation.setText(strLatLon);
            tvTimeStamp.setText(strtime);

            // Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
            Utilities.putValueInEditor(this).putBoolean("title", false).commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();

                lattitude = latti;
                longitude = longi;


            } else if (location1 != null) {
                double latti = location1.getLatitude();
                double longi = location1.getLongitude();

                lattitude = latti;
                longitude = longi;


            } else if (location2 != null) {
                double latti = location2.getLatitude();
                double longi = location2.getLongitude();
                lattitude = latti;
                longitude = longi;


            } else {

                Toast.makeText(this, "Unable to Trace your location", Toast.LENGTH_SHORT).show();

            }
        }
    }


}

