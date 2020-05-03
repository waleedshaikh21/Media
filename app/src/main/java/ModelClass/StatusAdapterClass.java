package ModelClass;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.media.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class StatusAdapterClass extends FirestoreRecyclerAdapter<StatusModelClass, StatusAdapterClass.ViewHolder> {
    public StatusAdapterClass(@NonNull FirestoreRecyclerOptions<StatusModelClass> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull StatusModelClass statusModelClass) {
        viewHolder.Comments.setText(statusModelClass.getComments());
        Glide.with(viewHolder.URL.getContext())
                .load(statusModelClass.getURL())
                .into(viewHolder.URL);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View singlerow = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.single_row, parent, false
        );
        return new ViewHolder(singlerow);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView URL;
        TextView Comments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            URL = itemView.findViewById(R.id.IV);
            Comments = itemView.findViewById(R.id.TextHere);
        }

    }

}
