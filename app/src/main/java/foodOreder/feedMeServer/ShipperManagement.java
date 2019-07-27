package foodOreder.feedMeServer;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.ObjectStreamException;
import java.util.HashMap;
import java.util.Map;

import foodOreder.feedMeServer.Common.Common;
import foodOreder.feedMeServer.Model.Shipper;
import foodOreder.feedMeServer.ViewHolder.ShipperViewHolder;
import info.hoang8f.widget.FButton;

public class ShipperManagement extends AppCompatActivity {


    FloatingActionButton fabAdd;

    FirebaseDatabase database;
    DatabaseReference shippers;

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;


    FirebaseRecyclerAdapter<Shipper, ShipperViewHolder> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_management);

        fabAdd = (FloatingActionButton)findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateShipperLayout();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_shipper);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        //Firebase
        database = FirebaseDatabase.getInstance();
        shippers = database.getReference(Common.SHIPPER_TABLE);


        //load all shippers
        loadAllShippers();



    }

    private void loadAllShippers() {
        FirebaseRecyclerOptions<Shipper> allShippers = new FirebaseRecyclerOptions.Builder<Shipper>()
                .setQuery(shippers, Shipper.class)
                .build();



        adapter = new FirebaseRecyclerAdapter<Shipper, ShipperViewHolder>(allShippers) {
            @Override
            protected void onBindViewHolder(@NonNull ShipperViewHolder holder, final int position, @NonNull final Shipper model) {
                holder.shipper_phone.setText(model.getPhone());
                holder.shipper_name.setText(model.getName());

                holder.btn_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showEditDialog(adapter.getRef(position).getKey(), model);
                    }
                });


                holder.btn_remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeShipper(adapter.getRef(position).getKey());
                    }
                });


            }

            @NonNull
            @Override
            public ShipperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.shipper_layout, parent, false);
                return new ShipperViewHolder(itemView);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private void removeShipper(String key) {
        shippers.child(key)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ShipperManagement.this, "Removed Successfully !!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ShipperManagement.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        adapter.notifyDataSetChanged();
    }

    private void showEditDialog(String key, Shipper model) {
        AlertDialog.Builder create_shipper_dialog = new AlertDialog.Builder(ShipperManagement.this);
        create_shipper_dialog.setTitle("Update Shipper");


        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.create_shipper_layout, null);

        final MaterialEditText edtName = (MaterialEditText)view.findViewById(R.id.edtName);
        final MaterialEditText edtPhone = (MaterialEditText)view.findViewById(R.id.edtPhone);
        final MaterialEditText edtPassword = (MaterialEditText)view.findViewById(R.id.edtPassword);


        //set data
        edtName.setText(model.getName());
        edtPhone.setText(model.getPhone());
        edtPassword.setText(model.getPassword());



        create_shipper_dialog.setView(view);
        create_shipper_dialog.setIcon(R.drawable.ic_local_shipping_black_24dp);

        create_shipper_dialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                Map<String, Object> update = new HashMap<>();
                update.put("name", edtName.getText().toString());
                update.put("phone", edtPhone.getText().toString());
                update.put("password", edtPassword.getText().toString());



                shippers.child(edtPhone.getText().toString())
                        .setValue(update)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ShipperManagement.this, "Shipper has been updated !", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ShipperManagement.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

        create_shipper_dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        create_shipper_dialog.show();

    }

    private void showCreateShipperLayout() {
        AlertDialog.Builder create_shipper_dialog = new AlertDialog.Builder(ShipperManagement.this);
        create_shipper_dialog.setTitle("Create Shipper");


        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.create_shipper_layout, null);

        final MaterialEditText edtName = (MaterialEditText)view.findViewById(R.id.edtName);
        final MaterialEditText edtPhone = (MaterialEditText)view.findViewById(R.id.edtPhone);
        final MaterialEditText edtPassword = (MaterialEditText)view.findViewById(R.id.edtPassword);


        create_shipper_dialog.setView(view);
        create_shipper_dialog.setIcon(R.drawable.ic_local_shipping_black_24dp);

        create_shipper_dialog.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Shipper shipper = new Shipper();
                shipper.setName(edtName.getText().toString());
                shipper.setPhone(edtPhone.getText().toString());
                shipper.setPassword(edtPassword.getText().toString());

                shippers.child(edtPhone.getText().toString())
                        .setValue(shipper)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ShipperManagement.this, "A new shipper has been added !", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ShipperManagement.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

        create_shipper_dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        create_shipper_dialog.show();


    }
}
