package com.example.childstuff.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.childstuff.ListActivity;
import com.example.childstuff.MainActivity;
import com.example.childstuff.R;
import com.example.childstuff.data.DatabaseHandler;
import com.example.childstuff.model.Item;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.text.MessageFormat;
import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<Item> itemList;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private LayoutInflater inflater;

    public RecyclerViewAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_row,viewGroup,false);
        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.itemName.setText(MessageFormat.format("Item Name: {0}", item.getItemName()));
        holder.itemColor.setText(MessageFormat.format("Item Color: {0}", item.getItemColor()));
        holder.quantity.setText(MessageFormat.format("Quantity: {0}", String.valueOf(item.getItemQuantity())));
        holder.size.setText(MessageFormat.format("Size: {0}", String.valueOf(item.getItemSize())));
        holder.dateAdded.setText(MessageFormat.format("Date: {0}", item.getDateItemAdded()));
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView itemName;
        public TextView itemColor;
        public TextView quantity;
        public TextView size;
        public TextView dateAdded;
        public Button editButton;
        public Button deleteButton;

        public int id;
        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            itemName = itemView.findViewById(R.id.item_name);
            itemColor = itemView.findViewById(R.id.item_color);
            quantity = itemView.findViewById(R.id.item_quantity);
            size = itemView.findViewById(R.id.item_size);
            dateAdded = itemView.findViewById(R.id.item_date);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);

            editButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Item item = itemList.get(position);
             switch (v.getId()) {
                 case R.id.editButton:
                     updateItem(item);
                     break;
                 case R.id.deleteButton:
                     deleteItem(item.getId());
                     break;
             }
        }

        private void deleteItem(final int id) {
            builder = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.confirmation_pop,null);

            Button noButton = view.findViewById(R.id.conf_no_button);
            Button yesButton = view.findViewById(R.id.conf_yes_button);

            builder.setView(view);
            dialog = builder.create();
            dialog.show();

            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseHandler db = new DatabaseHandler(context);
                    db.deleteItem(id);
                    itemList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    dialog.dismiss();
                }
            });
           noButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   dialog.dismiss();
               }
           });

        }


    private void updateItem(final Item item) {

        builder = new AlertDialog.Builder(context);
        inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.popup, null);
        final TextView babyItem;
        final TextView itemQuantity;
        final TextView itemColor;
        final TextView itemSize;
        Button saveButton;
        TextView title;

        babyItem = view.findViewById(R.id.babyItem);
        itemQuantity = view.findViewById(R.id.itemQuantity);
        itemColor = view.findViewById(R.id.itemColor);
        itemSize = view.findViewById(R.id.itemSize);
        saveButton = view.findViewById(R.id.saveButton);
        saveButton.setText(R.string.update);
        title = view.findViewById(R.id.title);

        title.setText(R.string.edit_item);
        babyItem.setText(item.getItemName());
        itemQuantity.setText(String.valueOf(item.getItemQuantity()));
        itemColor.setText(item.getItemColor());
        itemSize.setText(String.valueOf(item.getItemSize()));

        builder.setView(view);
        dialog = builder.create();
        dialog.show();


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHandler databaseHandler = new DatabaseHandler(context);
                item.setItemName(babyItem.getText().toString());
                item.setItemColor(itemColor.getText().toString());
                item.setItemQuantity(Integer.parseInt(itemQuantity.getText().toString()));
                item.setItemSize(Integer.parseInt(itemSize.getText().toString()));

                if (!babyItem.getText().toString().isEmpty()
                        && !itemColor.getText().toString().isEmpty()
                        && !itemQuantity.getText().toString().isEmpty()
                        && !itemSize.getText().toString().isEmpty()) {
                    databaseHandler.updateItem(item);
                    int position = getAdapterPosition();
                            notifyItemChanged(position, item);

                } else {
                    Snackbar.make(v, "field should not be empty", Snackbar.LENGTH_SHORT).show();
                }
                dialog.dismiss();;
            }


        });
    }

    }


}
