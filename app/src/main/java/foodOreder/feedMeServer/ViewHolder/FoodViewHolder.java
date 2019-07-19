package foodOreder.feedMeServer.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import foodOreder.feedMeServer.Common.Common;
import foodOreder.feedMeServer.Interface.ItemClickListener;
import foodOreder.feedMeServer.R;


    public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener ,View.OnCreateContextMenuListener{



    public TextView foodName;
    public ImageView foodImage;


    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);
        foodName = (TextView)itemView.findViewById(R.id.food_name);
        foodImage =(ImageView)itemView.findViewById(R.id.food_image);

        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select the action");
        contextMenu.add(0,0,getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0,0,getAdapterPosition(), Common.DELETE);
    }
}
