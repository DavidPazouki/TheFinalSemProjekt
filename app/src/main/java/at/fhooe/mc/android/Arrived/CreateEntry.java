package at.fhooe.mc.android.Arrived;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateEntry extends AppCompatActivity {

    EditText phoneNumber;
    EditText message;
    String place;
    float lon;
    float lat;
    private static final String TAG = "CreateEntry";


    //Test

    //widgets
    private AutoCompleteTextView mSearchText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_entry);
        phoneNumber = findViewById(R.id.nummer1);
        message = findViewById(R.id.nachricht1);
        mSearchText = (AutoCompleteTextView) findViewById(R.id.creatEntry_Search);
        init();
        Button create = findViewById(R.id.createbutton);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("phoneNumber", phoneNumber.getText().toString());
                i.putExtra("message", message.getText().toString());
                i.putExtra("address", place);
                i.putExtra("lon", lon);
                i.putExtra("lat", lat);
                setResult(RESULT_OK,i);
                finish();
            }
        });
    }



    private void init(){
        Log.d(TAG, "init: initializing");

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    //execute our method for searching
                    geoLocate();
                }

                return false;
            }
        });
    }

    private void geoLocate(){
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(CreateEntry.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
        }

        if(list.size() > 0){
            Address address = list.get(0);
            mSearchText.setText(address.getAddressLine(0));
            place = address.getAddressLine(0);
            lon = (float) address.getLongitude();
            lat = (float)address.getLatitude();
            Log.d(TAG, "geoLocate: found a location: " + address.toString());
        }
    }
}
