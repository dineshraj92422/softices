package login.softices.com.splash.activities;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import login.softices.com.splash.R;
import login.softices.com.splash.adapter.UsersRecyclerAdapter;
import login.softices.com.splash.database.DatabaseHelper;

public class UserListActivity extends AppCompatActivity {

    private AppCompatActivity activity = UserListActivity.this;
    private TextView textviewname;
    private RecyclerView recyclerViewUsers;
    private List<User> listUsers;
    private UsersRecyclerAdapter usersRecyclerAdapter;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_user_list );
        initViews();
        initObjects();
    }

    private void initViews() {
        textviewname = (TextView) findViewById( R.id.textViewName );
        recyclerViewUsers = (RecyclerView) findViewById( R.id.recyclerViewUsers );
    }

    private void initObjects() {
        listUsers=new ArrayList<>(  );
        databaseHelper = new DatabaseHelper(activity);
        usersRecyclerAdapter=new UsersRecyclerAdapter(listUsers,databaseHelper);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewUsers.setLayoutManager(mLayoutManager);
        recyclerViewUsers.setItemAnimator(new DefaultItemAnimator());
        recyclerViewUsers.setHasFixedSize(true);
        recyclerViewUsers.setAdapter(usersRecyclerAdapter);
        listUsers.addAll(databaseHelper.getAllUser());
        usersRecyclerAdapter.notifyDataSetChanged();



    }


}