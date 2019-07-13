package login.softices.com.splash.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import login.softices.com.splash.R;
import login.softices.com.splash.activities.User;
import login.softices.com.splash.database.DatabaseHelper;

public class UsersRecyclerAdapter extends RecyclerView.Adapter<UsersRecyclerAdapter.UserViewHolder> {

    private List<User> listUsers;
    private DatabaseHelper databaseHelper;

    public UsersRecyclerAdapter(List<User> listUsers, DatabaseHelper databaseHelper) {
        this.listUsers = listUsers;
        this.databaseHelper = databaseHelper;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflating recycler item view
        View itemView = LayoutInflater.from( parent.getContext() )
                .inflate( R.layout.item_user_recycler, parent, false );

        return new UserViewHolder( itemView );
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, final int position) {
        holder.textViewName.setText( listUsers.get( position ).getFirst_name() );
        holder.textViewEmail.setText( listUsers.get( position ).getEmail() );
        holder.Delete.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(  "onClick: ",""  );
                databaseHelper.deleteUser( listUsers.get( position ).getEmail() );
                notifyDataSetChanged();
            }
        } );
    }

    @Override
    public int getItemCount() {

        return listUsers.size();
    }


    /**
     * ViewHolder class
     */
    public class UserViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewName;
        public TextView textViewEmail;
        public Button Delete;


        public UserViewHolder(View view) {
            super( view );
            textViewName = view.findViewById( R.id.textViewName );
            textViewEmail = view.findViewById( R.id.textViewEmail );
            Delete = view.findViewById( R.id.btn_delet );
        }
    }


}
