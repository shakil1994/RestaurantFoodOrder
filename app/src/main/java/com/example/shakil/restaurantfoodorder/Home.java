package com.example.shakil.restaurantfoodorder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.example.shakil.restaurantfoodorder.Common.Common;
import com.example.shakil.restaurantfoodorder.Database.Database;
import com.example.shakil.restaurantfoodorder.Interface.ItemClickListener;
import com.example.shakil.restaurantfoodorder.Model.Category;
import com.example.shakil.restaurantfoodorder.Model.Token;
import com.example.shakil.restaurantfoodorder.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database;
    DatabaseReference category;

    TextView txtFullName;

    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    SwipeRefreshLayout swipeRefreshLayout;
    CounterFab fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        //view
        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark, android.R.color.holo_blue_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Common.isConnectedToInternet(getBaseContext())){
                    loadMenu();
                }
                else {
                    Toast.makeText(getBaseContext(), "Please check your internet connection !!!!!", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });

        //Default, load for first time
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (Common.isConnectedToInternet(getBaseContext())){
                    loadMenu();
                }
                else {
                    Toast.makeText(getBaseContext(), "Please check your internet connection !!!!!", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });




        //Init Firebase
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");

        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class, R.layout.menu_item, MenuViewHolder.class, category) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Category model, int position) {

                viewHolder.txtMenuName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageView);

                final Category clickItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        //Get CategoryId and send to new Activity
                        Intent foodList = new Intent(Home.this, FoodList.class);

                        //Because CategoryId is Key , so we just get key of this item
                        foodList.putExtra("CategoryId", adapter.getRef(position).getKey());
                        startActivity(foodList);


                    }
                });
            }
        };


        Paper.init(this);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(Home.this, Cart.class);
                startActivity(cartIntent);
            }
        });
        fab.setCount(new Database(this).getCountCart());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Set Name for user
        View headerView = navigationView.getHeaderView(0);
        txtFullName = (TextView)headerView.findViewById(R.id.txtFullName);
        txtFullName.setText(Common.currentUser.getName());

        //load menu
        recycler_menu = (RecyclerView) findViewById(R.id.recycler_menu);
        /*recycler_menu.setHasFixedSize(true);*/
        layoutManager = new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);

        //for animation
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recycler_menu.getContext(),R.anim.layout_fall_down);
        recycler_menu.setLayoutAnimation(controller);

        updateToken(FirebaseInstanceId.getInstance().getToken());

    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setCount(new Database(this).getCountCart());
        /*if (adapter != null){
            adapter.startListening();
        }*/
    }

    private void updateToken(String token) {
        FirebaseDatabase db =FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token data = new Token(token, false); //false because this token send from client app
        tokens.child(Common.currentUser.getPhone()).setValue(data);
    }

    private void loadMenu() {

        /*FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(category, Category.class).build();

        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder viewHolder, int position, @NonNull Category model) {
                viewHolder.txtMenuName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageView);

                final Category clickItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        //Get CategoryId and send to new Activity
                        Intent foodList = new Intent(Home.this, FoodList.class);

                        //Because CategoryId is Key , so we just get key of this item
                        foodList.putExtra("CategoryId", adapter.getRef(position).getKey());
                        startActivity(foodList);


                    }
                });
            }

            @Override
            public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);
                return new MenuViewHolder(itemView);
            }
        };

        adapter.startListening();*/



        recycler_menu.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);

        //animation
        recycler_menu.getAdapter().notifyDataSetChanged();
        recycler_menu.scheduleLayoutAnimation();
    }

    /*@Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }*/

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh){
            loadMenu();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            // Handle the camera action
        }
        else if (id == R.id.nav_cart) {
            Intent cartIntent = new Intent(Home.this, Cart.class);
            startActivity(cartIntent);
        }
        else if (id == R.id.nav_orders) {
            Intent orderIntent = new Intent(Home.this, OrderStatus.class);
            startActivity(orderIntent);
        }
        else if (id == R.id.nav_log_out) {

            //delete remember user * password
            Paper.book().destroy();

            //Logout
            Intent signIn = new Intent(Home.this, SignIn.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signIn);
        }
        else if (id == R.id.nav_change_pwd){
            showChangePasswordDialog();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("CHANGE PASSWORD");
        alertDialog.setMessage("Please fill all information");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_pwd = inflater.inflate(R.layout.change_password_layout, null);

        /*final MaterialEditText edtName = layout_pwd.findViewById(R.id.edtName);*/
        final MaterialEditText edtPassword = layout_pwd.findViewById(R.id.edtPassword);
        final MaterialEditText edtNewPassword = layout_pwd.findViewById(R.id.edtNewPassword);
        final MaterialEditText edtRepeatPassword = layout_pwd.findViewById(R.id.edtRepeatPassword);

        alertDialog.setView(layout_pwd);

        //Button
        alertDialog.setPositiveButton("CHANGE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //change password here

                //for use SpotsDialog, please use AlertDialog from android.app, not from v7 like above AlertDialog
                final AlertDialog waitingDialog = new SpotsDialog(Home.this);
                waitingDialog.show();

                /*//Update Name
                Map<String, Object> update_name = new HashMap<>();
                update_name.put("name", edtName.getText().toString());*/

                /*FirebaseDatabase.getInstance().getReference("User").child(Common.currentUser.getPhone())
                        .updateChildren(update_name).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //Dismiss dialog
                                waitingDialog.dismiss();
                                if (task.isSuccessful()){
                                    Toast.makeText(Home.this, "Name was Updated", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });*/


                //check old password
                if (edtPassword.getText().toString().equals(Common.currentUser.getPassword())){
                    //check new password and repeat password
                    if (edtNewPassword.getText().toString().equals(edtRepeatPassword.getText().toString())){
                        Map<String, Object> passwordUpdate = new HashMap<>();
                        passwordUpdate.put("Password", edtNewPassword.getText().toString());

                        //Make update
                        DatabaseReference user = FirebaseDatabase.getInstance().getReference("User");
                        user.child(Common.currentUser.getPhone()).updateChildren(passwordUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        waitingDialog.dismiss();
                                        Toast.makeText(Home.this, "Password was update", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Home.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else {
                        waitingDialog.dismiss();
                        Toast.makeText(Home.this, "New password doesn't match", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    waitingDialog.dismiss();
                    Toast.makeText(Home.this, "Wrong old password", Toast.LENGTH_SHORT).show();
                }


            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }
}
