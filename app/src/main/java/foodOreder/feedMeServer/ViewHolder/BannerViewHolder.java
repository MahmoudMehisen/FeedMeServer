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

public class BannerViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {


    public TextView bannerName;
    public ImageView bannerImage;


    private ItemClickListener itemClickListener;


    public BannerViewHolder(@NonNull View itemView) {
        super(itemView);
        bannerName = (TextView) itemView.findViewById(R.id.bannerName);
        bannerImage = (ImageView) itemView.findViewById(R.id.bannerImage);

        itemView.setOnCreateContextMenuListener(this);

    }


    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.add(0, 0, getAdapterPosition(), Common.DELETE);

    }
}