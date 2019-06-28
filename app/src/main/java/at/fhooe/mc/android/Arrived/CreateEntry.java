package at.fhooe.mc.android.Arrived;

import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateEntry extends AppCompatActivity {

    private static final String TAG = "CreateEntry";
    EditText name;
    EditText phoneNumber;
    EditText message;
    String place;
    float lon;
    float lat;
    int radius = 2000;
    TextView radiusDisplay;
    SeekBar radiusChanger;
    boolean foundLocation = false;
    private AutoCompleteTextView mSearchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_entry);
        name = findViewById(R.id.name);
        phoneNumber = findViewById(R.id.nummer);
        message = findViewById(R.id.nachricht);
        mSearchText = findViewById(R.id.creatEntry_Search);
        init();
        radiusDisplay = findViewById(R.id.textView1);
        radiusChanger = findViewById(R.id.seekBar);
        radiusChanger.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= 8) {
                    progress = progress * 100 + 100;
                    radiusDisplay.setText("Radius: " + progress + " m");
                    radius = progress;
                } else if (progress == 19) {
                    radiusDisplay.setText("Radius: 20 km");
                    radius = 20000;
                } else if (progress == 20) {
                    radiusDisplay.setText("Radius: 50 km");
                    radius = 50000;
                } else {
                    progress = progress - 8;
                    radiusDisplay.setText("Radius: " + progress + " km");
                    radius = progress * 1000;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        Button create = findViewById(R.id.createbutton);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (everythingFilledOut()) {
                    Intent i = new Intent();
                    i.putExtra("name", name.getText().toString());
                    i.putExtra("phoneNumber", phoneNumber.getText().toString());
                    i.putExtra("message", message.getText().toString());
                    i.putExtra("address", place);
                    i.putExtra("lon", lon);
                    i.putExtra("lat", lat);
                    i.putExtra("radius", radius);
                    setResult(RESULT_OK, i);
                    finish();
                } else
                    Toast.makeText(getApplicationContext(), "Please fill out everything!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean everythingFilledOut() {
        return (!name.getText().toString().equals("") && !phoneNumber.getText().toString().equals("") && !message.getText().toString().equals("") && foundLocation);
    }


    private void init() {
        Log.d(TAG, "init: initializing");

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                    //execute our method for searching
                    geoLocate();
                }

                return false;
            }
        });
    }

    private void geoLocate() {
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(CreateEntry.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if (list.size() > 0) {
            foundLocation = true;
            Address address = list.get(0);
            mSearchText.setText(address.getAddressLine(0));
            place = address.getAddressLine(0);
            lon = (float) address.getLongitude();
            lat = (float) address.getLatitude();
            Log.d(TAG, "geoLocate: found a location: " + address.toString());
        } else {
            foundLocation = false;
        }
    }
}
