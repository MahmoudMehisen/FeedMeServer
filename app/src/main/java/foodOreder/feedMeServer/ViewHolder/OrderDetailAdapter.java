package foodOreder.feedMeServer.ViewHolder;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import foodOreder.feedMeServer.Model.Order;
import foodOreder.feedMeServer.R;

class MyViewHolder extends RecyclerView.ViewHolder{

    public TextView name, quantity, price, discount;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        name = (TextView)itemView.findViewById(R.id.product_name);
        quantity = (TextView)itemView.findViewById(R.id.product_quantity);
        price = (TextView)itemView.findViewById(R.id.product_price);
        discount = (TextView)itemView.findViewById(R.id.product_discount);
    }
}


public class OrderDetailAdapter extends RecyclerView.Adapter<MyViewHolder>{

    List<Order> MyOrders;

    public OrderDetailAdapter(List<Order> myOrders) {
        MyOrders = myOrders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_detail_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Order order = MyOrders.get(position);
        holder.name.setText(String.format("Name : %s", order.getProductName()));
        holder.quantity.setText(String.format("Quantity : %s", order.getQuantity()));
        holder.price.setText(String.format("Price : %s", order.getPrice()));
        holder.discount.setText(String.format("Discount : %s", order.getDiscount()));
    }

    @Override
    public int getItemCount() {
        return MyOrders.size();
    }
}
