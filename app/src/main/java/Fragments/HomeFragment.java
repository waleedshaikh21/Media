package Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.media.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import ModelClass.StatusAdapterClass;
import ModelClass.StatusModelClass;

public class HomeFragment extends Fragment {
    private ImageView insta, comment;
    private GoogleSignInClient mGoogleSignInClient;

    private FirebaseAuth mAuth;
    private RecyclerView rcv;
    private FirebaseFirestore objectFirebaseAuth;

    private StatusAdapterClass objectStatusAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        rcv = view.findViewById(R.id.RCV);
        comment = view.findViewById(R.id.Comment);
        ConnectXML();
        return view;
    }

    private void ConnectXML() {
        try {
            objectFirebaseAuth = FirebaseFirestore.getInstance();
            addValuestoRV();
        } catch (Exception ex) {
            Toast.makeText(getActivity(), "onCreate: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void addValuestoRV() {
        try {
            Query objectQuery = objectFirebaseAuth.collection("Gallery");
            FirestoreRecyclerOptions<StatusModelClass> options =
                    new FirestoreRecyclerOptions.Builder<StatusModelClass>().setQuery(objectQuery, StatusModelClass.class).build();
            objectStatusAdapter = new StatusAdapterClass(options);

            rcv.setLayoutManager(new LinearLayoutManager(getActivity()));
            rcv.setAdapter(objectStatusAdapter);
        } catch (Exception ex) {
            Toast.makeText(getActivity(), "AddValuesToRV: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        objectStatusAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        objectStatusAdapter.stopListening();
    }

}
