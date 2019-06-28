package at.fhooe.mc.android.Arrived;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    //storing important information
    private static final String TAG = "xdd";
    public static final int CREATE_ENTRY = 0;
    public static final int INSIDE_LIST_VIEW = 99;
    public ArrayList<String> names = new ArrayList<>();
    public ArrayList<String> messages = new ArrayList<>();
    public ArrayList<String> places = new ArrayList<>();
    public ArrayList<String> phoneNumbers = new ArrayList<>();
    public ArrayList<String> radius = new ArrayList<>();
    public int entries;
    public String newMessage;
    public String newPhoneNumber;
    public String newPlace;
    public String newName;
    public float newLon;
    public float newLat;
    public int newRadius;
    CustomListAdapter customListAdapter;

    @Override //this happens when you start the app
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "MainActivity::onCreate(): app started");
        //checking permissions
        if (checkSelfPermission(android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED || checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED || checkSelfPermission(android.Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED)
            requestPermissions(new String[]{Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}, 0);

        //reset service
        stopService(new Intent(this, GPSService.class));
        startService(new Intent(this, GPSService.class));

        //get entries out of shared preferences
        entries = getSharedPreferences("entries", MODE_PRIVATE).getInt("entries", 0);
        loadData();

        //create toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //create custom listview --> new class
        customListAdapter = new CustomListAdapter(this, names, messages, places);

        //create list view
        ListView listView = findViewById(R.id.listview);
        listView.setAdapter(customListAdapter);

        //if you click on an item in the list --> new activity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Log.i(TAG, "MainActivity::onCreate(): switching to InsideListView.class");
                Intent intent = new Intent(MainActivity.this, InsideListView.class);
                intent.putExtra("name", names.get(position));
                intent.putExtra("message", messages.get(position));
                intent.putExtra("place", places.get(position));
                intent.putExtra("phoneNumber", phoneNumbers.get(position));
                intent.putExtra("radius", radius.get(position));
                startActivityForResult(intent, INSIDE_LIST_VIEW);
            }
        });

        //button for adding birthdays
        FloatingActionButton fab = findViewById(R.id.fab);
        //opens createentry class
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "MainActivity::onCreate():clicked on fab");
                Intent intent = new Intent(MainActivity.this, CreateEntry.class);
                startActivityForResult(intent, CREATE_ENTRY);
            }
        });
    }

    @Override //whenever you get an result from an activity
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case CREATE_ENTRY: {
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        newName = data.getStringExtra("name");
                        newPhoneNumber = data.getStringExtra("phoneNumber");
                        newMessage = data.getStringExtra("message");
                        newPlace = data.getStringExtra("address");
                        newLon = data.getFloatExtra("lon", 0);
                        newLat = data.getFloatExtra("lat", 0);
                        newRadius = data.getIntExtra("radius", 0);
                    }
                    add();
                }
            }
            break;
            case INSIDE_LIST_VIEW: {
                if (resultCode == RESULT_OK)
                    if (data != null) {
                        delete(data.getStringExtra("name"));
                    }
            }
            break;
            default:
                Log.e("tag", "unexpected requestcode");
        }
    }

    //loading data
    private void loadData() {
        names = loadArrayString("name");
        messages = loadArrayString("message");
        places = loadArrayString("place");
        radius = loadArrayInt("radius");
        phoneNumbers = loadArrayString("phoneNumber");
        if (getSharedPreferences("entries", MODE_PRIVATE).getBoolean("switch", false)) {
            Collections.reverse(names);
            Collections.reverse(messages);
            Collections.reverse(places);
            Collections.reverse(radius);
            Collections.reverse(phoneNumbers);
        }
    }

    //method to get stringarrays out of shared preferences
    public ArrayList<String> loadArrayString(String arrayName) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("entries", MODE_PRIVATE);
        ArrayList<String> list = new ArrayList<>();
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

    //method to get intarrays out of shared preferences and save them in string arrays
    public ArrayList<String> loadArrayInt(String arrayName) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("entries", MODE_PRIVATE);
        ArrayList<String> list = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < entries; i++) {
            String newString = String.valueOf(prefs.getInt(arrayName + "_" + i, 2000));
            list.add(count, newString);
            count++;
        }
        return list;
    }

    //if name and date are set:
    public void add() {
        places.add(newPlace);
        names.add(newName);
        messages.add(newMessage);
        phoneNumbers.add(newPhoneNumber);
        radius.add(String.valueOf(newRadius));
        addToSharedPreferences();
        customListAdapter.notifyDataSetChanged();
        Log.i(TAG, "MainActivity::add(): entry added");
        stopService(new Intent(this, GPSService.class));
        startService(new Intent(this, GPSService.class));
    }

    //the process of adding new stuff to the shared preferences
    private void addToSharedPreferences() {
        entries++;
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("entries", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name_" + (entries - 1), newName);
        editor.putString("message_" + (entries - 1), newMessage);
        editor.putString("phoneNumber_" + (entries - 1), newPhoneNumber);
        editor.putString("place_" + (entries - 1), newPlace);
        editor.putFloat("lon_" + (entries - 1), newLon);
        editor.putFloat("lat_" + (entries - 1), newLat);
        editor.putInt("radius_" + (entries - 1), newRadius);
        editor.putInt("entries", entries);
        editor.apply();
    }

    //deleting the entry in the arraylists
    private void delete(String deleteName) {
        int x = names.indexOf(deleteName);
        deleteInSharedPreferences(deleteName);
        names.remove(x);
        messages.remove(x);
        places.remove(x);
        customListAdapter.notifyDataSetChanged();
        Log.i(TAG, "MainActivity::delete(): entry deleted");
        stopService(new Intent(this, GPSService.class));
        startService(new Intent(this, GPSService.class));
    }

    //deleting the entry in the shared preferences
    public void deleteInSharedPreferences(String deleteName) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("entries", MODE_PRIVATE);
        for (int i = 0; i < entries; i++) {
            String newString = prefs.getString("name_" + (i), null);
            if (newString != null) {
                if (newString.equals(deleteName)) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.remove("name_" + (i));
                    editor.remove("message_" + (i));
                    editor.remove("place_" + (i));
                    editor.remove("phoneNumber_" + (i));
                    editor.remove("lon_" + (i));
                    editor.remove("lat_" + (i));
                    editor.remove("radius_" + (i));
                    editor.apply();
                    Log.i(TAG, "entry deleted");
                    return;
                }
            }
        }
    }

    @Override //inflates the options menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override //if an option in the options menu is selected
    public boolean onOptionsItemSelected(MenuItem item) {
        //starting options menu
        Intent intent = new Intent(MainActivity.this, AppSettings.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }
}