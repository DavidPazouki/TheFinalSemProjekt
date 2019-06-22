package at.fhooe.mc.android.Arrived;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

public class DeleteDialogFragment extends DialogFragment {
    public String deletePhoneNumber;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        deletePhoneNumber = getArguments().getString("phoneNumber");
        AlertDialog.Builder bob = new AlertDialog.Builder(getContext());
        bob.setMessage("You really wanna delete???");
        //if okay: make toast, item gets deleted in main activity
        bob.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(), "rip", Toast.LENGTH_SHORT).show();
               //problem
                ((InsideListView)getActivity()).itemGetsDeleted(deletePhoneNumber);
            }
        });
        //if cancel: nothing happens
        bob.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        Dialog d = bob.create();
        return d;
    }
}
