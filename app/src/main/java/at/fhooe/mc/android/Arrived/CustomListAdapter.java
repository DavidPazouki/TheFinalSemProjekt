package at.fhooe.mc.android.Arrived;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListAdapter extends ArrayAdapter {

    //to reference the Activity
    private final Activity context;
    private final ArrayList<String> names;
    private final ArrayList<String> messages;
    private final ArrayList<String> places;

    public CustomListAdapter(Activity context, ArrayList<String> names, ArrayList<String> messages,ArrayList<String>places) {
        super(context, R.layout.listview_row, names);
        this.context = context;
        this.names = names;
        this.messages = messages;
        this.places = places;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.listview_row, null, true);

        //this code gets references to objects in the listview_row.xml file
        TextView nameTextField = rowView.findViewById(R.id.nameTextView);
        TextView messageTextField = rowView.findViewById(R.id.messageTextView);
        TextView placeTextField = rowView.findViewById(R.id.placeTextView);

        String message = messages.get(position);
        if(message.length()>35)
            message= message.substring(0,35)+"...";

        //this code sets the values of the objects to values from the arrays
        nameTextField.setText(names.get(position));
        messageTextField.setText(message);
        placeTextField.setText(places.get(position));
        return rowView;
    }
}
