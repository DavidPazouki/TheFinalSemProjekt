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

    //to store the list of phoneNumbers
    private final ArrayList<String> phoneNumber;

    //to store the list of messages
    private final ArrayList<String> place;

    public CustomListAdapter(Activity context, ArrayList<String> nameArrayParam, ArrayList<String> infoArrayParam) {
        super(context, R.layout.listview_row, nameArrayParam);
        this.context = context;
        this.phoneNumber = nameArrayParam;
        this.place = infoArrayParam;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.listview_row, null, true);

        //this code gets references to objects in the listview_row.xml file
        TextView nameTextField = rowView.findViewById(R.id.nameTextViewID);
        TextView infoTextField = rowView.findViewById(R.id.infoTextViewID);
        //ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView1ID);

        //this code sets the values of the objects to values from the arrays
        nameTextField.setText(phoneNumber.get(position));
        infoTextField.setText(place.get(position));
        //imageView.setImageResource(imageIDarray[position]);

        return rowView;

    }

}
