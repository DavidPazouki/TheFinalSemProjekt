package at.fhooe.mc.android.Arrived;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    //storing important information
    public ArrayList<String> phoneNumbers = new ArrayList<String>();
    public ArrayList<String> messages = new ArrayList<String>();
    public ArrayList<String> places = new ArrayList<String>();
    public String newMessage;
    public String newPhoneNumber;
    public String newPlace;
    public int entries;
    public float newLon;
    public float newLat;
    CustomListAdapter customListAdapter;


    @Override //this happens when you start the app
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get entries out of shared preferences
        entries = getSharedPreferences("entries", 0).getInt("entries", 0);
        loadData();
        //create toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //create custom listview --> new class
        customListAdapter = new CustomListAdapter(this, phoneNumbers, places);

        //create list view
        ListView listView = findViewById(R.id.listview);
        listView.setAdapter(customListAdapter);

        //if you click on an item in the list --> new activity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(MainActivity.this, InsideListView.class);
                intent.putExtra("phoneNumber", phoneNumbers.get(position));
                intent.putExtra("message", messages.get(position));
                intent.putExtra("place", places.get(position));
                startActivityForResult(intent, 99);
            }
        });

        //button for adding birthdays
        FloatingActionButton fab = findViewById(R.id.fab);
        //opens first datepickerdialog
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateEntry.class);
                startActivityForResult(intent, 0);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 0: {
                if (resultCode == RESULT_OK) {
                    newPhoneNumber = data.getStringExtra("phoneNumber");
                    newMessage = data.getStringExtra("message");
                    newPlace = data.getStringExtra("address");
                    newLon = data.getFloatExtra("lon", 0);
                    newLat = data.getFloatExtra("lat", 0);
                    add();
                }
            }
            break;
            case 99: {
                if (resultCode == RESULT_OK)
                    delete(data.getStringExtra("deletePhoneNumber"));
            }
            break;
            default:
                Log.e("tag", "unexpected requestcode");
        }
    }

    //loading data
    private void loadData() {
        phoneNumbers = loadArray("phoneNumber");
        messages = loadArray("message");
        places = loadArray("place");
    }

    //method to get arrays out of shared preferences
    public ArrayList<String> loadArray(String arrayName) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("entries", MODE_PRIVATE);
        ArrayList<String> list = new ArrayList();
        int count = 0;
        for (int i = 0; i < entries; i++) {
            String newString = prefs.getString(arrayName + "_" + i, null);
            if (newString != null) {
                list.add(count, newString);
                count++;
            }
        }
        return list;
    }

    //if name and date are set:
    public void add() {
        addService();
        places.add(newPlace);
        phoneNumbers.add(newPhoneNumber);
        messages.add(newMessage);
        addToSharedPreferences();
        customListAdapter.notifyDataSetChanged();
        startService(new Intent(this,GPSService.class));
        Log.i("xdd", "entry added");
    }

    private void addService() {
        Intent i = new Intent(this, GPSService.class);
        i.putExtra("phoneNumber", newPhoneNumber);
        i.putExtra("place", newPlace);
        i.putExtra("message", newMessage);
        startService(i);
    }

    //the process of adding new stuff to the shared preferences
    private void addToSharedPreferences() {
        entries++;
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("entries", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("message_" + (entries - 1), newMessage);
        editor.putString("phoneNumber_" + (entries - 1), newPhoneNumber);
        editor.putString("place_" + (entries - 1), newPlace);
        editor.putFloat("lon_" + (entries - 1), newLon);
        editor.putFloat("lat_" + (entries - 1), newLat);
        editor.putInt("entries", entries);
        editor.commit();
    }

    //deleting the name
    private void delete(String deletePhoneNumber) {
        int x = phoneNumbers.indexOf(deletePhoneNumber);
        deleteInSharedPreferences(deletePhoneNumber);
        phoneNumbers.remove(x);
        messages.remove(x);
        places.remove(x);
        customListAdapter.notifyDataSetChanged();
    }

    public void deleteInSharedPreferences(String deleteName) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("entries", 0);
        for (int i = 0; i < entries; i++) {
            String newString = prefs.getString("names_" + i, null);
            if (newString != null) {
                if (newString.equals(deleteName)) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.remove("message_" + (i-1));
                    editor.remove("place_" + (i-1));
                    editor.remove("phoneNumber_" + (i-1));
                    editor.remove("lon_" + (i-1));
                    editor.remove("lat_" + (i-1));
                    editor.commit();
                    Log.i("xdd", "entry deleted");
                    return;
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}