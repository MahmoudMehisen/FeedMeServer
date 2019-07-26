package foodOreder.feedMeServer;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import foodOreder.feedMeServer.Common.Common;
import foodOreder.feedMeServer.Interface.ItemClickListener;
import foodOreder.feedMeServer.Model.Banner;
import foodOreder.feedMeServer.Model.Food;
import foodOreder.feedMeServer.ViewHolder.BannerViewHolder;
import foodOreder.feedMeServer.ViewHolder.FoodViewHolder;
import info.hoang8f.widget.FButton;

public class BannerActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;


    FirebaseDatabase database;
    DatabaseReference banner;
    String categoryId;
    FirebaseRecyclerOptions<Banner> options;
    FirebaseRecyclerAdapter<Banner, BannerViewHolder> adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);

        database = FirebaseDatabase.getInstance();
        banner = database.getReference("Banner");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerBanner);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadListBanner();


    }

    private void loadListBanner() {
        options = new FirebaseRecyclerOptions.Builder<Banner>()
                .setQuery(banner, Banner.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Banner, BannerViewHolder >(options) {

            @NonNull
            @Override
            public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.banner_layout, viewGroup, false);
                return new BannerViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull BannerViewHolder holder, int position, @NonNull Banner model) {
                holder.bannerName.setText(model.getName());
                Picasso.with(getApplicationContext())
                        .load(model.getImage())
                        .into(holder.bannerImage);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item.getTitle()==Common.DELETE)
        {
            deleteBanner(adapter.getRef(item.getOrder()).getKey());
        }


        return super.onContextItemSelected(item);
    }

    private void deleteBanner(String key) {
        banner.child(key).removeValue();
    }
}
