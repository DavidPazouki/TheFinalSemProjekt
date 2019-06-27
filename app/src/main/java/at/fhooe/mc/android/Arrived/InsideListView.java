package at.fhooe.mc.android.Arrived;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class InsideListView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inside_listview);

        // display the name
        String savedExtra = getIntent().getStringExtra("name");
        TextView myText = findViewById(R.id.inside_listview_name);
        myText.setText(savedExtra);

        //display the date
        savedExtra = getIntent().getStringExtra("message");
        myText = findViewById(R.id.inside_listview_date);
        myText.setText(savedExtra);


        //display the date
        savedExtra = getIntent().getStringExtra("place");
        myText = findViewById(R.id.inside_listview_diff);
        myText.setText(savedExtra);

        //the delete button
        Button button = findViewById(R.id.delete);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteDialogFragment deleteDialogFragment = newInstance(getIntent().getStringExtra("name"));
                deleteDialogFragment.show(getSupportFragmentManager(), "delete");
            }
        });

        //the return button
        button = findViewById(R.id.returning);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnWithoutDelete();
            }
        });
    }

    public static DeleteDialogFragment newInstance(String name) {
        DeleteDialogFragment f = new DeleteDialogFragment();
        // Supply name input as an argument.
        Bundle args = new Bundle();
        args.putString("name", name);
        f.setArguments(args);
        return f;
    }

    public void itemGetsDeleted(String name) {
        Intent i = new Intent();
        i.putExtra("name", name);
        setResult(RESULT_OK, i);
        finish();
    }

    public void returnWithoutDelete() {
        Intent i = new Intent();
        setResult(RESULT_CANCELED, i);
        finish();
    }
}
