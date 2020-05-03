package Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.media.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SearchFragment extends Fragment {
    private Button back, imageDownloadBtn;
    private EditText imagenameET;

    private ImageView imageToDownloadIV;
    private TextView gallery;

    private ProgressBar bar;
    private FirebaseFirestore objectFirebaseFirestore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        imageToDownloadIV = view.findViewById(R.id.imageToDownloadIV);

        imageDownloadBtn = view.findViewById(R.id.Download);
        imagenameET = view.findViewById(R.id.Caption);

        gallery = view.findViewById(R.id.textgallery);
        objectFirebaseFirestore = FirebaseFirestore.getInstance();

        bar = view.findViewById(R.id.ProgressBar);
        imageDownloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setVisibility(View.VISIBLE);
                gallery.setVisibility(View.INVISIBLE);
                Download();
            }
        });

        return view;
    }

    private void Download() {
        try {
            if (!imagenameET.getText().toString().isEmpty()) {
                bar.setVisibility(View.VISIBLE);
                imageToDownloadIV.setImageResource(R.drawable.loading);
                objectFirebaseFirestore.collection("Gallery")
                        .document(imagenameET.getText().toString())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    bar.setVisibility(View.INVISIBLE);
                                    String url = documentSnapshot.getString("URL");
                                    Glide.with(getActivity())
                                            .load(url)
                                            .into(imageToDownloadIV);
                                    imagenameET.setText("");
                                    Toast.makeText(getActivity(), "Image Downloaded", Toast.LENGTH_SHORT).show();

                                } else {
                                    bar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getActivity(), "No Such File Exists", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        bar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getActivity(), "Failed To Retrieve Image" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                bar.setVisibility(View.INVISIBLE);
                Toast.makeText(getActivity(), "Please Enter Name of The Image To Download", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            bar.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(), "DownloadError: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
