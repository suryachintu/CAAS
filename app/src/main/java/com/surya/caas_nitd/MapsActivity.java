package com.surya.caas_nitd;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private BottomSheetBehavior bottomSheetBehavior;
    private FirebaseAuth mAuth;
    @BindView(R.id.room_linearlayout)
    LinearLayout linearLayout;
    @BindView(R.id.available_textview)
    TextView logout;
    @BindView(R.id.bottomsheet)
    FrameLayout frameLayout;
    @BindColor(R.color.red_color)
    int red;
    @BindColor(R.color.white_color)
    int white;
    @BindView(R.id.room_no)
    TextView room_number;
    @BindView(R.id.department_name)
    TextView department_name;
    @BindColor(R.color.color_black)
    int black;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.menu_btn)
    ImageView menu_btn;
    @BindView(R.id.card_view)
    CardView mCardView;
    @BindView(R.id.devices_recyclerview)
    RecyclerView deviceRecycler;

    private DatabaseReference mDatabaseReference;
    private HashMap<String,String> mRoomDetailMap;
    private DeviceAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/ShareTech.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            Intent intent = new Intent(MapsActivity.this,Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(MapsActivity.this,Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });


        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomsheet));

        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        //animation
        final Animation fadeOut = AnimationUtils.loadAnimation(this,R.anim.fade_out);
        final Animation fadeIn = AnimationUtils.loadAnimation(this,R.anim.fade_in);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                switch (newState){

                    case BottomSheetBehavior.STATE_COLLAPSED:
                            mCardView.setVisibility(View.VISIBLE);
                            mCardView.startAnimation(fadeIn);

                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                            mCardView.startAnimation(fadeOut);
                            mCardView.setVisibility(View.GONE);
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:

                        mCardView.setVisibility(View.VISIBLE);
                        mCardView.startAnimation(fadeIn);
                        break;

                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                    default:

                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        deviceRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRoomDetailMap = new HashMap<>();
        mAdapter = new DeviceAdapter(this,mRoomDetailMap);
        deviceRecycler.setAdapter(mAdapter);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        // Add a marker in Sydney and move the camera
        Log.e(TAG,"On map ready");

        LatLng sydney = new LatLng(28.8428, 77.1050);
        mMap.addMarker(new MarkerOptions().position(sydney).title("NIT Delhi"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        getRoomData();

        //set the click listener to marker
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //open the bottom sheets
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                room_number.setText(marker.getTitle());
                getDevicesData(marker.getTitle());
                Log.e(TAG,marker.toString() + "**"+ marker.getTitle());
                return false;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            }
        });
    }

    private void getDevicesData(final String title) {

        mRoomDetailMap.clear();
        mAdapter.notifyDataSetChanged();
        mDatabaseReference.child("Rooms").child(title).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot :
                        dataSnapshot.getChildren()) {
                    mRoomDetailMap.put(snapshot.getKey() + "," + title, snapshot.getValue().toString());
                    Log.e(TAG, dataSnapshot.toString());
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @OnClick(R.id.menu_btn)
    public void openDrawer(){
        drawer.openDrawer(GravityCompat.START);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
        else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // Handle the camera action
        } else if (id == R.id.nav_attendance) {

        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_share) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getRoomData() {

        mDatabaseReference.child("Rooms").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot :
                        dataSnapshot.getChildren()) {
                    for (DataSnapshot snap :
                            snapshot.getChildren()) {
                        if (snap.getKey().equals("Location")){
                            String p = snap.getValue().toString();
                            addMarkers(snapshot.getKey(),p.substring(1,p.length()-2));
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void addMarkers(String key, String location) {

        if (mMap == null)
            return;
        String[] x = location.split(",");
        double lat = Double.parseDouble(x[0].split("=")[1]);
        double lng = Double.parseDouble(x[1].split("=")[1]);
        LatLng latLng = new LatLng(lat,lng);
        mMap.addMarker(new MarkerOptions().position(latLng).title(key));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

    }

}
