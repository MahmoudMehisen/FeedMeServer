package foodOreder.feedMeServer.ViewHolder;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import foodOreder.feedMeServer.Common.Common;
import foodOreder.feedMeServer.Interface.ItemClickListener;
import foodOreder.feedMeServer.R;
import info.hoang8f.widget.FButton;

public class OrderViewHolder extends RecyclerView.ViewHolder{

    public TextView orderId,orderStatus,orderPhone,orderAddress;
    public FButton btnEdit, btnRemove, btnDetails, btnDirection;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        orderId = (TextView) itemView.findViewById(R.id.orderId);

        orderPhone = (TextView) itemView.findViewById(R.id.orderPhone);
        orderStatus = (TextView) itemView.findViewById(R.id.orderStatus);


        btnEdit = (FButton)itemView.findViewById(R.id.btnEdit);
        btnRemove = (FButton)itemView.findViewById(R.id.btnRemove);
        btnDetails = (FButton) itemView.findViewById(R.id.btnDetails);
        btnDirection = (FButton)itemView.findViewById(R.id.btnDirection);
    }
}
