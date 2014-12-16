package davidgasquez.windrose;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends Activity implements SensorEventListener {

    TextView northDirection;
    TextView latitudeText;
    TextView longitudeText;
    TextView distanceText;

    double latitude;
    double longitude;

    double distance;

    private ImageView image;

    private float currentDegree = 0.0f;
    private float lastDegree = 0.0f;

    private SensorManager mSensorManager;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = (ImageView) findViewById(R.id.imageViewCompass);

        distance = 0;

        northDirection = (TextView) findViewById(R.id.northDirection);
        latitudeText = (TextView) findViewById(R.id.latitudeText);
        longitudeText = (TextView) findViewById(R.id.longitudeText);
        distanceText = (TextView) findViewById(R.id.distanceText);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        lastDegree = Math.round(event.values[0]);

        if (lastDegree > 0 && lastDegree < 180) {
            northDirection.setText("Turn left");
        } else {
            northDirection.setText("Turn right");
        }

        RotateAnimation rotation;
        rotation = new RotateAnimation(
                currentDegree,
                -lastDegree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        rotation.setDuration(300);
        rotation.setFillAfter(true);

        image.startAnimation(rotation);
        currentDegree = -lastDegree;

        updateCoordinates();

    }

    private void updateCoordinates() {
        locationListener = new LocationListener() {

            int i = 0;

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            @Override
            public void onProviderEnabled(String provider) {
                lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            @Override
            public void onProviderDisabled(String provider) {
            }

            @Override
            public void onLocationChanged(Location location) {

                if (i == 0) {
                    lastLocation = location;
                    i++;
                }

                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                distance += location.distanceTo(lastLocation);
                System.out.println(distance);

                latitude = location.getLatitude();
                longitude = location.getLongitude();

                if (latitude > 0) {
                    latitudeText.setText(String.valueOf(latitude) + " N");
                } else {
                    latitudeText.setText(String.valueOf(-latitude) + " S");
                }

                if (longitude > 0) {
                    longitudeText.setText(String.valueOf(longitude) + " E");
                } else {
                    longitudeText.setText(String.valueOf(-longitude) + " W");
                }


                lastLocation = location;

                distanceText.setText(String.valueOf(distance + "meters"));

            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, locationListener);
    }

    /**
     * Called when the accuracy of the registered sensor has changed.
     * <p/>
     * <p>See the SENSOR_STATUS_* constants in
     * {@link android.hardware.SensorManager SensorManager} for details.
     *
     * @param sensor   Sensor type
     * @param accuracy The new accuracy of this sensor, one of
     *                 {@code SensorManager.SENSOR_STATUS_*}
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
