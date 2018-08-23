package uniteapp.uniteclient;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.util.Objects;

public class AddDialogFragment extends DialogFragment {

    public interface AddDialogListener {
        void onDialogPositiveClick(String toAdd);
        //void onDialogNegativeClick(DialogFragment dialog);
    }

    AddDialogListener mListener;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        try {
            mListener = (AddDialogListener) context;
        } catch (ClassCastException e)
        {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflator = Objects.requireNonNull(getActivity()).getLayoutInflater();

        @SuppressLint("InflateParams") final View view = inflator.inflate(R.layout.new_todo_dialog, null);
        builder.setView(view);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText textBox = view.findViewById(R.id.toDoAddText);
                mListener.onDialogPositiveClick(textBox.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AddDialogFragment.this.getDialog().cancel();
            }
        });
        return builder.create();
    }
}
