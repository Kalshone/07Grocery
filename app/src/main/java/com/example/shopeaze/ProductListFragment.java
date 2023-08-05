package com.example.shopeaze;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ProductListFragment extends Fragment {
    private RecyclerView recyclerView;
    private ArrayList<Product> products;
    private ProductAdapter productAdapter;
    private DatabaseReference productsRef;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Products...");
        progressDialog.show();

        recyclerView = view.findViewById(R.id.recyclerViewProducts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        products = new ArrayList<>();
        productAdapter = new ProductAdapter(getActivity(), products);

        recyclerView.setAdapter(productAdapter);
        // Get the reference to your Realtime Database node, in this case, "Products"
        productsRef = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child("StoreOwner")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Products");

        // Set up a ChildEventListener to listen for changes in the data
        productsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                Product product = dataSnapshot.getValue(Product.class);
                products.add(product);
                productAdapter.notifyDataSetChanged();
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // Handle changes to existing children if needed
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // Handle removed children if needed
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // Handle moved children if needed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                Log.w("TAG", "Listen failed.", databaseError.toException());
            }
        });

        return view;
    }
}
