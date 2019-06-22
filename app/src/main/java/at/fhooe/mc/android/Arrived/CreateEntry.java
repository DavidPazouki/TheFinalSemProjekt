package at.fhooe.mc.android.Arrived;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class CreateEntry extends AppCompatActivity {

    EditText phoneNumber;
    EditText message;
    EditText place;

    private static final String TAG = "CreateEntry";
    private static final int ERROR_DIALOG_REQUEST = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_entry);
        phoneNumber = findViewById(R.id.nummer1);
        message = findViewById(R.id.nachricht1);

        //#############################################################################################//

        if(isServicesOK()){
            init();

        }

        //############################################################################################//


        Button create = findViewById(R.id.createbutton);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("phoneNumber", phoneNumber.getText().toString());
                i.putExtra("message", message.getText().toString());
                i.putExtra("place", place.getText().toString());
                setResult(RESULT_OK,i);
                finish();
            }
        });
    }

    private void init(){
        Button btnMap = (Button) findViewById(R.id.createEntry_MapButton);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateEntry.this, MapActivity.class);
                startActivity(intent);
            }
        });
    }

    //###########################################################################################################//





    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int available = googleApiAvailability.isGooglePlayServicesAvailable(CreateEntry.this);

        if(available == ConnectionResult.SUCCESS){
            //alles ist lit
            Log.d(TAG, "is ServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //error but we can fix it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(CreateEntry.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "We cannot make mak request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    //###########################################################################################################//
}
