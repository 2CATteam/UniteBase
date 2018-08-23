package uniteapp.uniteclient;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class ButtonAdapter extends RecyclerView.Adapter<ButtonAdapter.ViewHolder> {
    private ArrayList<Button> toDoList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout layout;

        ViewHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.LinearLayout);
        }
    }


    ButtonAdapter(ArrayList<Button> toShow) {
        toDoList = toShow;
    }

    @NonNull
    @Override
    public ButtonAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_button_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Log.d("Replacing", toDoList.get(i).getText().toString());
        viewHolder.layout.removeAllViews();
        try {
            viewHolder.layout.addView(toDoList.get(i));
        } catch (IllegalStateException e) {
            ViewGroup toRemove = (ViewGroup) toDoList.get(i).getParent();
            toRemove.removeView(toDoList.get(i));
            viewHolder.layout.addView(toDoList.get(i));
        }
    }

    @Override
    public int getItemCount() {
        return toDoList.size();
    }

    public void removeItem(int position)
    {
        toDoList.remove(position);
        this.notifyItemRemoved(position);
    }

    public void addItem(Button toAdd)
    {
        for (Button toCompare: toDoList)
        {
            if (toCompare.getText().equals(toAdd.getText()))
            {
                if (!toCompare.equals(toAdd)) {
                    Log.d("Found match", toAdd.getText().toString());
                    toDoList.set(toDoList.indexOf(toCompare), toAdd);
                    this.notifyItemChanged(toDoList.indexOf(toAdd));
                }
                return;
            }
        }
        Log.d("Found no match", toAdd.getText().toString() + toDoList.toString());
        toDoList.add(toAdd);
        this.notifyItemInserted(toDoList.indexOf(toAdd));
    }

}
