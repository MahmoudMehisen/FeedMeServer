package foodOreder.feedMeServer;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.rengwuxian.materialedittext.MaterialEditText;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import foodOreder.feedMeServer.Common.Common;
import foodOreder.feedMeServer.Interface.ItemClickListener;
import foodOreder.feedMeServer.Model.Banner;
import foodOreder.feedMeServer.Model.Food;
import foodOreder.feedMeServer.ViewHolder.FoodViewHolder;
import info.hoang8f.widget.FButton;


public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseDatabase database;
    DatabaseReference foodList;
    DatabaseReference banner;
    String categoryId;
    FirebaseRecyclerOptions<Food> options;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    FirebaseRecyclerOptions<Food> searchOptions;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;
    FloatingActionButton fab;
    boolean searched=false;


    MaterialEditText edtName, edtDescription, edtPrice, edtDiscount;
    FButton btnUpload, btnSelect;
    private final int PICK_IMAGE_REQUEST = 71;

    Food newFood;
    Uri saveUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        fab = (FloatingActionButton) findViewById(R.id.fabFood);

        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Foods");
        banner = database.getReference("Banner");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerFood);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("images/");


        if (getIntent() != null) {
            categoryId = getIntent().getStringExtra("CategoryId");
        }
        if (!categoryId.isEmpty()) {
            loadListFood(categoryId);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddFoodDialog();
            }
        });

        materialSearchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter your food");

        loadSuggest();
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                List<String> suggest = new ArrayList<>();
                for (String search : suggestList) {
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase())) {
                        suggest.add(search);
                    }
                }
                materialSearchBar.setLastSuggestions(suggest);


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled) {
                    recyclerView.setAdapter(adapter);
                    adapter.startListening();
                    searched = false;
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text);
                searched = true;

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

    }

    private void uploadImage() {
        if (saveUri != null) {
            final ProgressDialog mDilaog = new ProgressDialog(this);
            mDilaog.setMessage("Uploading...");
            mDilaog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDilaog.dismiss();
                    Toast.makeText(FoodList.this, "Uploaded !!!", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            newFood = new Food(edtName.getText().toString(), uri.toString(), edtDescription.getText().toString(), edtPrice.getText().toString(), edtDiscount.getText().toString(), categoryId);

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDilaog.dismiss();
                    Toast.makeText(FoodList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mDilaog.setMessage("Uploaded " + progress + "%");
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            saveUri = data.getData();
            btnSelect.setText("Image Selected");

        }
    }

    private void chooseImage() {


        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);


    }

    private void showAddFoodDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodList.this);
        alertDialog.setTitle("Add new Food");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View addMenuLayout = inflater.inflate(R.layout.add_new_food_layout, null);

        edtName = addMenuLayout.findViewById(R.id.editName);
        btnSelect = addMenuLayout.findViewById(R.id.btnSelect);
        btnUpload = addMenuLayout.findViewById(R.id.btnUpload);
        edtDescription = addMenuLayout.findViewById(R.id.editDescription);
        edtDiscount = addMenuLayout.findViewById(R.id.editDiscount);
        edtPrice = addMenuLayout.findViewById(R.id.editPrice);


        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });


        alertDialog.setView(addMenuLayout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if (newFood != null) {
                    foodList.push().setValue(newFood);
                    Toast.makeText(getApplicationContext(), "New Food Added", Toast.LENGTH_SHORT).show();
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

    private void startSearch(CharSequence text) {
        searchOptions = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(foodList.orderByChild("name").equalTo(text.toString()), Food.class)
                .build();
        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(searchOptions) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {

                holder.foodName.setText(model.getName());
                Glide.with(getApplicationContext()).load(model.getImage()).centerCrop().placeholder(R.drawable.placeholder).into(holder.foodImage);

                final Food food = model;
                //System.out.println(food.getName());
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //

                    }
                });
            }


            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.food_item, viewGroup, false);

                return new FoodViewHolder(view);
            }
        };
        recyclerView.setAdapter(searchAdapter);
        searchAdapter.startListening();

    }

    private void loadSuggest() {
        foodList.orderByChild("menuId").equalTo(categoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Food item = postSnapshot.getValue(Food.class);
                    suggestList.add(item.getName());
                    System.out.println(item.getName());

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadListFood(String categoryId) {
        options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(foodList.orderByChild("menuId").equalTo(categoryId), Food.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {

                holder.foodName.setText(model.getName());
                Glide.with(getApplicationContext()).load(model.getImage()).centerCrop().placeholder(R.drawable.placeholder).into(holder.foodImage);

                final Food food = model;
                //System.out.println(food.getName());
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {


                    }
                });

            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.food_item, viewGroup, false);

                return new FoodViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals(Common.UPDATE)) {
            if (!searched)
                showUpdateFoodDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
            else
                showUpdateFoodDialog(searchAdapter.getRef(item.getOrder()).getKey(), searchAdapter.getItem(item.getOrder()));
        } else if (item.getTitle().equals(Common.DELETE)) {
            if (!searched)
                deleteFood(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
            else
                deleteFood(searchAdapter.getRef(item.getOrder()).getKey(), searchAdapter.getItem(item.getOrder()));

        }
        else if(item.getTitle().equals(Common.ADD_TO_BANNER))
        {
            if (!searched)
                addToBanner( adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
            else
                addToBanner(searchAdapter.getRef(item.getOrder()).getKey(), searchAdapter.getItem(item.getOrder()));
        }


        return super.onContextItemSelected(item);
    }

    private void addToBanner(String key, Food item) {
        Banner ban = new Banner(key,item.getName(),item.getImage());
        banner.push().setValue(ban);
        Toast.makeText(this,"Added to Banner",Toast.LENGTH_SHORT).show();
    }


    private void deleteFood(String key, Food item) {
        foodList.child(key).removeValue();
        Toast.makeText(getApplicationContext(), "Deleted Food", Toast.LENGTH_SHORT).show();
    }

    private void showUpdateFoodDialog(final String key, final Food item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodList.this);
        alertDialog.setTitle("Edit Food");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View addMenuLayout = inflater.inflate(R.layout.add_new_food_layout, null);

        edtName = addMenuLayout.findViewById(R.id.editName);
        btnSelect = addMenuLayout.findViewById(R.id.btnSelect);
        btnUpload = addMenuLayout.findViewById(R.id.btnUpload);
        edtDescription = addMenuLayout.findViewById(R.id.editDescription);
        edtDiscount = addMenuLayout.findViewById(R.id.editDiscount);
        edtPrice = addMenuLayout.findViewById(R.id.editPrice);


        edtName.setText(item.getName());
        edtDescription.setText(item.getDescription());
        edtDiscount.setText(item.getDiscount());
        edtPrice.setText(item.getPrice());


        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImage(item);
            }
        });


        alertDialog.setView(addMenuLayout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                item.setName(edtName.getText().toString());
                item.setDescription(edtDescription.getText().toString());
                item.setDiscount(edtDiscount.getText().toString());
                item.setPrice(edtPrice.getText().toString());

                foodList.child(key).setValue(item);


                Toast.makeText(getApplicationContext(), "Updated Food", Toast.LENGTH_SHORT).show();


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

    private void changeImage(final Food item) {
        if (saveUri != null) {
            final ProgressDialog mDilaog = new ProgressDialog(this);
            mDilaog.setMessage("Uploading...");
            mDilaog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDilaog.dismiss();
                    Toast.makeText(FoodList.this, "Uploaded !!!", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            item.setImage(uri.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDilaog.dismiss();
                    Toast.makeText(FoodList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mDilaog.setMessage("Uploaded " + progress + "%");
                }
            });
        }
    }

}
