package foodOreder.feedMeServer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import foodOreder.feedMeServer.Common.Common;
import foodOreder.feedMeServer.Model.DataMessage;
import foodOreder.feedMeServer.Model.MyResponse;
import foodOreder.feedMeServer.Model.Request;
import foodOreder.feedMeServer.Model.Shipper;
import foodOreder.feedMeServer.Model.Token;
import foodOreder.feedMeServer.Remote.APIService;
import foodOreder.feedMeServer.ViewHolder.OrderViewHolder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderStatus extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    FirebaseRecyclerOptions<Request> options;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    MaterialSpinner spinner, shipperSpinner;

    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        mService = Common.getFCMService();

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        recyclerView = (RecyclerView) findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        loadOrders();
        turnGPSOn();


    }
    private void turnGPSOn(){

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(!statusOfGPS) {
            Toast.makeText(getApplicationContext(),"Open Gps to get Your location",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

    private void loadOrders() {
        options = new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(requests, Request.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder, final int position, @NonNull final Request model) {

                holder.orderId.setText(adapter.getRef(position).getKey());
                holder.orderStatus.setText(convertCodeToStatus(model.getStatus()));
                holder.orderPhone.setText(model.getPhone());
                holder.orderDate.setText(Common.getDate(Long.parseLong(adapter.getRef(position).getKey())));


                //new event button
                holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showUpdateDialog(adapter.getRef(position).getKey(), adapter.getItem(position));

                    }
                });

                holder.btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteOrder(adapter.getRef(position).getKey());
                    }
                });

                holder.btnDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent OrderDetail = new Intent(OrderStatus.this, OrderDetail.class);
                        Common.currentRequest = model;
                        OrderDetail.putExtra("OrderId", adapter.getRef(position).getKey());
                        startActivity(OrderDetail);
                    }
                });

                holder.btnDirection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent trackingOrder = new Intent(OrderStatus.this, TrackingOrder.class);
                        Common.currentRequest = model;
                        startActivity(trackingOrder);
                    }
                });
            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_layout, viewGroup, false);
                return new OrderViewHolder(view);
            }

        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    private String convertCodeToStatus(String status) {
        if (status.equals("0"))
            return "Placed";
        else if (status.equals("1"))
            return "In my way";
        else
            return "Shipping";
    }

    private void deleteOrder(String key) {
        requests.child(key).removeValue();
        adapter.notifyDataSetChanged();
    }

    private void showUpdateDialog(String key, final Request item) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatus.this);
        alertDialog.setTitle("Update Order");
        alertDialog.setMessage("Please choose status");

        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.update_order_layout, null);

        spinner = (MaterialSpinner) view.findViewById(R.id.statusSpinner);
        List<String> items = new ArrayList<>();
        items.add("Placed");
        items.add("On My Way");
        items.add("Shipping");
        spinner.setItems(items);

        shipperSpinner = (MaterialSpinner) view.findViewById(R.id.shipperSpinner);


        //load all shippers phone to spinner
        final List<String> shipperList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference(Common.SHIPPER_TABLE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot shipperSnapShot : dataSnapshot.getChildren()) {
                            shipperList.add(shipperSnapShot.getValue(Shipper.class).getName());
                        }
                        shipperSpinner.setItems(shipperList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        alertDialog.setView(view);

        final String localKey = key;
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                item.setStatus(String.valueOf(spinner.getSelectedIndex()));

                if (item.getStatus().equals("2")) {

                    //copy item to table "OrderNeedShip"
                    FirebaseDatabase.getInstance().getReference(Common.ORDER_NEED_SHIP_TABLE)
                            .child(shipperSpinner.getItems().get(shipperSpinner.getSelectedIndex()).toString())
                            .child(localKey)
                            .setValue(item);

                    requests.child(localKey).setValue(item);
                    adapter.notifyDataSetChanged();

                    sendOrderStatusToUser(localKey, item);
                    sendOrderShipRequestToShipper(shipperSpinner.getItems().get(shipperSpinner.getSelectedIndex()).toString(), item);
                } else {
                    requests.child(localKey).setValue(item);
                    adapter.notifyDataSetChanged();
                    sendOrderStatusToUser(localKey, item);
                }


            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();

    }

    private void sendOrderShipRequestToShipper(String shipperPhone, Request item) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        tokens.child(shipperPhone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()) {
                            Token token = dataSnapshot.getValue(Token.class);

                            Map<String, String> dataSend = new HashMap<>();
                            dataSend.put("title", "Feed Me");
                            dataSend.put("message", "You have new order needs to be shipped");
                            DataMessage dataMessage = new DataMessage(token.getToken(), dataSend);

                            mService.sendNotification(dataMessage)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.body().success == 1) {
                                                Toast.makeText(getApplicationContext(), "Sent to shipper", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "failed to send notification !", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {
                                            Log.e("ERROR", t.getMessage());
                                        }
                                    });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void sendOrderStatusToUser(final String key, final Request item) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        tokens.child(item.getPhone())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Token token = dataSnapshot.getValue(Token.class);

                            Map<String, String> dataSend = new HashMap<>();
                            dataSend.put("title", "Feed Me");
                            dataSend.put("message", "Your order " + key + " was updated");
                            DataMessage dataMessage = new DataMessage(token.getToken(), dataSend);

                            mService.sendNotification(dataMessage)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.body().success == 1) {
                                                Toast.makeText(getApplicationContext(), "Order was Updated !", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Order was Updated but failed to send notification !", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {
                                            Log.e("ERROR", t.getMessage());
                                        }
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
