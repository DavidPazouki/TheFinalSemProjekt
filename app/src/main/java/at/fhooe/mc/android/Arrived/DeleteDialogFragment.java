package at.fhooe.mc.android.Arrived;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.Toast;

public class DeleteDialogFragment extends DialogFragment {

    private static final String TAG = "xdd";
    public String deleteName;

    @NonNull
    @Override //when dialog gets created
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.i(TAG, "DeleteDialogFragment::onCreateDialog(): dialog created");
        deleteName = getArguments().getString("name");
        AlertDialog.Builder bob = new AlertDialog.Builder(getContext());
        bob.setMessage("Do you really want to delete the entry?");
        //if okay: make toast, item gets deleted in main activity
        bob.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "DeleteDialogFragment::onCreateDialog(): item gets deleted");
                Toast.makeText(getContext(), deleteName + " deleted", Toast.LENGTH_SHORT).show();
                ((InsideListView)getActivity()).itemGetsDeleted(deleteName);
            }
        });
        //if cancel: nothing happens
        bob.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "DeleteDialogFragment::onCreateDialog(): return to InsideListView");
            }
        });
        return bob.create();
    }
}
