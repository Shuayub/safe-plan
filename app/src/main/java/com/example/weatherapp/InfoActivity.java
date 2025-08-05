package com.example.weatherapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.android.material.button.MaterialButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;
import java.util.Map;

public class InfoActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigation;
    private Button addContactBtn, addDocumentBtn, addMedicationBtn, addLocationBtn;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        NavigationButton.setupNavigation(this, bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_info);

        addContactBtn = findViewById(R.id.add_contact_button);
        addDocumentBtn = findViewById(R.id.add_document_button);
        addMedicationBtn = findViewById(R.id.add_medication_button);
        addLocationBtn = findViewById(R.id.add_location_button);
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        uid = prefs.getString("userUID", "xw5kWxTsebgdWJHHVOKfBIbxAwR2");

        String testUid = "xw5kWxTsebgdWJHHVOKfBIbxAwR2";
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInAnonymously();



        loadDocuments();
        loadContacts();
        loadLocations();
        loadMedications();
        getSupportFragmentManager().setFragmentResultListener(
                "item_updated", // <-- NEW universal key for both add and edit
                this,
                (requestKey, bundle) -> {
                    String type = bundle.getString("item_type");
                    if ("Document".equals(type)) {
                        loadDocuments();
                    } else if ("Contact".equals(type)) {
                        loadContacts();
                    } else if ("Medication".equals(type)) {
                        loadMedications();
                    } else if ("Location".equals(type)) {
                        loadLocations();
                    }
                }
        );

        addContactBtn.setOnClickListener(v -> {
            AddItemDialogFragment dialog = new AddItemDialogFragment("Contact");
            dialog.show(getSupportFragmentManager(), "AddItemDialog");
        });

        addDocumentBtn.setOnClickListener(v -> {
            AddItemDialogFragment dialog = new AddItemDialogFragment("Document");

            dialog.show(getSupportFragmentManager(), "AddItemDialog");
        });

        addMedicationBtn.setOnClickListener(v -> {
            AddItemDialogFragment dialog = new AddItemDialogFragment("Medication");
            dialog.show(getSupportFragmentManager(), "AddItemDialog");
        });

        addLocationBtn.setOnClickListener(v -> {
            AddItemDialogFragment dialog = new AddItemDialogFragment("Location");
            dialog.show(getSupportFragmentManager(), "AddItemDialog");
        });


    }

    private void loadDocuments() {
        LinearLayout documentListLayout = findViewById(R.id.document_list_layout);
        documentListLayout.removeAllViews();

        DatabaseReference docRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(uid).child("documents");

        docRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    TextView noDocs = new TextView(InfoActivity.this);
                    noDocs.setText("No documents uploaded.");
                    noDocs.setTextColor(ContextCompat.getColor(InfoActivity.this, R.color.gray));
                    documentListLayout.addView(noDocs);
                    return;
                }

                for (DataSnapshot docSnapshot : snapshot.getChildren()) {
                    String docId = docSnapshot.getKey();
                    String docName = docSnapshot.child("name").getValue(String.class);
                    String fileUrl = docSnapshot.child("url").getValue(String.class);

                    if (docName == null || fileUrl == null) continue;

                    // Horizontal layout for each document
                    LinearLayout itemLayout = new LinearLayout(InfoActivity.this);
                    itemLayout.setOrientation(LinearLayout.HORIZONTAL);
                    itemLayout.setGravity(Gravity.CENTER_VERTICAL);
                    itemLayout.setPadding(0, 16, 0, 16);

// File Name TextView
                    TextView fileNameView = new TextView(InfoActivity.this);
                    fileNameView.setText(docName);
                    fileNameView.setTextSize(16);
                    fileNameView.setTextColor(ContextCompat.getColor(InfoActivity.this, R.color.black));                    fileNameView.setLayoutParams(new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)); // Weight 1 to take up remaining space

                    itemLayout.addView(fileNameView);

// View Button
                    Drawable viewIcon = getResizedIcon(R.drawable.ic_view, 20);    // 20dp
                    Drawable deleteIcon = getResizedIcon(R.drawable.ic_delete, 20);
                    MaterialButton viewButton = new MaterialButton(InfoActivity.this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
                    viewButton.setText("View");
                    viewButton.setIcon(viewIcon);
                    viewButton.setIconPadding(8);
                    viewButton.setTextColor(ContextCompat.getColor(InfoActivity.this, R.color.primary));
                    viewButton.setStrokeColorResource(R.color.primary);
                    viewButton.setIconTintResource(R.color.primary);
                    viewButton.setStrokeWidth(2);
                    viewButton.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    viewButton.setOnClickListener(v -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(fileUrl), "*/*");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    });
                    itemLayout.addView(viewButton);


                    MaterialButton deleteButton = new MaterialButton(InfoActivity.this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
                    deleteButton.setText("Delete");
                    deleteButton.setIcon(deleteIcon);
                    deleteButton.setIconPadding(8);
                    deleteButton.setTextColor(ContextCompat.getColor(InfoActivity.this, R.color.bright_red));
                    deleteButton.setStrokeColorResource(R.color.bright_red);
                    deleteButton.setIconTintResource(R.color.bright_red);
                    deleteButton.setStrokeWidth(2);
                    deleteButton.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    deleteButton.setOnClickListener(v -> {
                        // 1. Delete from Firebase Storage
                        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(fileUrl);
                        storageRef.delete()
                                .addOnSuccessListener(aVoid -> {
                                    // 2. Delete from Realtime Database
                                    docRef.child(docId).removeValue()
                                            .addOnSuccessListener(aVoid1 -> {
                                                Toast.makeText(InfoActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                                loadDocuments(); // refresh the list
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(InfoActivity.this, "Failed to delete DB entry", Toast.LENGTH_SHORT).show();
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(InfoActivity.this, "Failed to delete file", Toast.LENGTH_SHORT).show();
                                });
                    });

                    itemLayout.addView(deleteButton);
                    LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

                    LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    deleteParams.setMarginStart(50);

                    viewButton.setLayoutParams(viewParams);
                    deleteButton.setLayoutParams(deleteParams);
// Add the whole item layout to the parent container
                    documentListLayout.addView(itemLayout);

// Add `buttonRow` to your document layout
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "loadDocuments failed", error.toException());
            }
        });
    }

    private void loadContacts() {
        LinearLayout contactListLayout = findViewById(R.id.contact_list_layout);
        contactListLayout.removeAllViews();

        DatabaseReference contactRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(uid).child("contacts");

        // Header Row
        LinearLayout headerRow = new LinearLayout(this);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setPadding(0, 16, 0, 8);

        String[] headers = {"Name", "Phone", "Relation", "", ""};
        int[] weights = {3, 3, 3, 1, 1}; // More space for text columns

        for (int i = 0; i < headers.length; i++) {
            TextView header = new TextView(this);
            header.setText(headers[i]);
            header.setTextColor(ContextCompat.getColor(this, R.color.black));
            header.setTextSize(15);
            header.setTypeface(null, Typeface.BOLD);
            header.setMaxLines(1);
            header.setEllipsize(TextUtils.TruncateAt.END);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, weights[i]);
            params.setMarginEnd(8);
            header.setLayoutParams(params);
            header.setGravity(Gravity.CENTER);
            headerRow.addView(header);
        }

        contactListLayout.addView(headerRow);
        addDivider(contactListLayout);

        contactRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    TextView noContacts = new TextView(InfoActivity.this);
                    noContacts.setText("No contacts added.");
                    noContacts.setTextColor(ContextCompat.getColor(InfoActivity.this, R.color.gray));
                    contactListLayout.addView(noContacts);
                    return;
                }

                for (DataSnapshot contactSnapshot : snapshot.getChildren()) {
                    String contactId = contactSnapshot.getKey();
                    String name = contactSnapshot.child("name").getValue(String.class);
                    String phone = contactSnapshot.child("phone").getValue(String.class);
                    String relationship = contactSnapshot.child("relationship").getValue(String.class);

                    if (name == null || phone == null) continue;
                    if (relationship == null) relationship = "—";

                    LinearLayout rowLayout = new LinearLayout(InfoActivity.this);
                    rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                    rowLayout.setPadding(0, 8, 0, 8);
                    rowLayout.setGravity(Gravity.CENTER_VERTICAL);

                    // Data columns
                    String[] values = {name, phone, relationship};
                    for (int i = 0; i < values.length; i++) {
                        TextView col = new TextView(InfoActivity.this);
                        col.setText(values[i]);
                        col.setTextSize(14);
                        col.setTextColor(ContextCompat.getColor(InfoActivity.this, R.color.black));
                        col.setMaxLines(1);
                        col.setEllipsize(TextUtils.TruncateAt.END);

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                0, LinearLayout.LayoutParams.WRAP_CONTENT, weights[i]);
                        params.setMarginEnd(8);
                        col.setLayoutParams(params);
                        rowLayout.addView(col);
                    }

                    MaterialButton editButton = new MaterialButton(InfoActivity.this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
                    editButton.setIcon(getResizedIcon(R.drawable.ic_edit, 20));
                    editButton.setIconTintResource(R.color.primary); // or bright_red for delete
                    editButton.setStrokeColorResource(R.color.primary);
                    editButton.setStrokeWidth(2);

                    editButton.setText("");
                    editButton.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_START); // Use this available gravity
                    editButton.setIconPadding(0); // Remove padding around icon if needed                    editButton.setBackgroundColor(Color.TRANSPARENT);

                    editButton.setPadding(0, 0, 0, 0);
                    editButton.setInsetTop(0);
                    editButton.setInsetBottom(0);
                    editButton.setMinimumWidth(0);
                    editButton.setMinimumHeight(0);

// Layout params
                    LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(
                            dpToPx(36),
                            dpToPx(36)
                    );
                    editParams.setMargins(dpToPx(4), 0, dpToPx(4), 0);
                    editButton.setLayoutParams(editParams);
                    String finalRelationship = relationship;
                    editButton.setOnClickListener(v -> {
                        Map<String, String> data = new HashMap<>();
                        data.put("name", name);
                        data.put("phone", phone);
                        data.put("relationship", finalRelationship);

                        EditItemDialogFragment dialog = new EditItemDialogFragment("Contact", contactId, data);
                        dialog.show(getSupportFragmentManager(), "EditItemDialog");
                    });


                    // Delete Button
                    MaterialButton deleteButton = new MaterialButton(InfoActivity.this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
                    deleteButton.setIcon(getResizedIcon(R.drawable.ic_delete, 20));
                    deleteButton.setIconTintResource(R.color.bright_red); // or bright_red for delete
                    deleteButton.setStrokeColorResource(R.color.bright_red);
                    deleteButton.setStrokeWidth(2);

                    deleteButton.setText("");
                    deleteButton.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_START); // Use this available gravity
                    deleteButton.setIconPadding(0); // Remove padding around icon if needed                    editButton.setBackgroundColor(Color.TRANSPARENT);

                    deleteButton.setPadding(0, 0, 0, 0);
                    deleteButton.setInsetTop(0);
                    deleteButton.setInsetBottom(0);
                    deleteButton.setMinimumWidth(0);
                    deleteButton.setMinimumHeight(0);

// Layout params
                    LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(
                            dpToPx(36),
                            dpToPx(36)
                    );
                    deleteParams.setMargins(dpToPx(4), 0, dpToPx(4), 0);
                    deleteButton.setLayoutParams(editParams);
                    deleteButton.setOnClickListener(v -> {
                        contactRef.child(contactId).removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(InfoActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                    loadContacts();
                                });
                    });


                    rowLayout.addView(editButton);
                    rowLayout.addView(deleteButton);

                    contactListLayout.addView(rowLayout);
                    addDivider(contactListLayout);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "loadContacts failed", error.toException());
            }
        });
    }

    public void loadMedications() {
        LinearLayout medicationListLayout = findViewById(R.id.medication_list_layout);
        medicationListLayout.removeAllViews();

        DatabaseReference medicationRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(uid).child("medications");

        // Header Row
        LinearLayout headerRow = new LinearLayout(this);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setPadding(0, 16, 0, 8);

        String[] headers = {"Medication", "Dosage", "", ""};
        int[] weights = {4, 4, 1, 1};

        for (int i = 0; i < headers.length; i++) {
            TextView header = new TextView(this);
            header.setText(headers[i]);
            header.setTextColor(ContextCompat.getColor(this, R.color.black));
            header.setTextSize(15);
            header.setTypeface(null, Typeface.BOLD);
            header.setMaxLines(1);
            header.setEllipsize(TextUtils.TruncateAt.END);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, weights[i]);
            params.setMarginEnd(8);
            header.setLayoutParams(params);
            header.setGravity(Gravity.CENTER);
            headerRow.addView(header);
        }

        medicationListLayout.addView(headerRow);
        addDivider(medicationListLayout);

        medicationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    TextView noMeds = new TextView(InfoActivity.this);
                    noMeds.setText("No medications added.");
                    noMeds.setTextColor(ContextCompat.getColor(InfoActivity.this, R.color.gray));
                    medicationListLayout.addView(noMeds);
                    return;
                }

                for (DataSnapshot medSnapshot : snapshot.getChildren()) {
                    String medId = medSnapshot.getKey();
                    String medName = medSnapshot.child("name").getValue(String.class);
                    String dosage = medSnapshot.child("dosage").getValue(String.class);

                    if (medName == null || dosage == null) continue;

                    LinearLayout rowLayout = new LinearLayout(InfoActivity.this);
                    rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                    rowLayout.setPadding(0, 8, 0, 8);
                    rowLayout.setGravity(Gravity.CENTER_VERTICAL);

                    String[] values = {medName, dosage};
                    for (int i = 0; i < values.length; i++) {
                        TextView col = new TextView(InfoActivity.this);
                        col.setText(values[i]);
                        col.setTextSize(14);
                        col.setTextColor(ContextCompat.getColor(InfoActivity.this, R.color.black));
                        col.setMaxLines(1);
                        col.setEllipsize(TextUtils.TruncateAt.END);

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                0, LinearLayout.LayoutParams.WRAP_CONTENT, weights[i]);
                        params.setMarginEnd(8);
                        col.setLayoutParams(params);
                        rowLayout.addView(col);
                    }

                    // Edit Button
                    MaterialButton editButton = createIconButton(R.drawable.ic_edit, R.color.primary);
                    editButton.setOnClickListener(v -> {
                        Map<String, String> data = new HashMap<>();
                        data.put("name", medName);
                        data.put("dosage", dosage);

                        EditItemDialogFragment dialog = new EditItemDialogFragment("Medication", medId, data);
                        dialog.show(getSupportFragmentManager(), "EditItemDialog");
                    });

                    // Delete Button
                    MaterialButton deleteButton = createIconButton(R.drawable.ic_delete, R.color.bright_red);
                    deleteButton.setOnClickListener(v -> {
                        medicationRef.child(medId).removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(InfoActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                    loadMedications();
                                });
                    });

                    rowLayout.addView(editButton);
                    rowLayout.addView(deleteButton);

                    medicationListLayout.addView(rowLayout);
                    addDivider(medicationListLayout);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "loadMedications failed", error.toException());
            }
        });
    }
    public void loadLocations() {
        LinearLayout locationListLayout = findViewById(R.id.location_list_layout);
        locationListLayout.removeAllViews();

        DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(uid).child("locations");

        // Header Row
        LinearLayout headerRow = new LinearLayout(this);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setPadding(0, 16, 0, 8);

        String[] headers = {"Address", "Notes", "", ""};
        int[] weights = {4, 4, 1, 1};

        for (int i = 0; i < headers.length; i++) {
            TextView header = new TextView(this);
            header.setText(headers[i]);
            header.setTextColor(ContextCompat.getColor(this, R.color.black));
            header.setTextSize(15);
            header.setTypeface(null, Typeface.BOLD);
            header.setMaxLines(1);
            header.setEllipsize(TextUtils.TruncateAt.END);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, weights[i]);
            params.setMarginEnd(8);
            header.setLayoutParams(params);
            header.setGravity(Gravity.CENTER);
            headerRow.addView(header);
        }

        locationListLayout.addView(headerRow);
        addDivider(locationListLayout);

        locationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    TextView noLocations = new TextView(InfoActivity.this);
                    noLocations.setText("No locations added.");
                    noLocations.setTextColor(ContextCompat.getColor(InfoActivity.this, R.color.gray));
                    locationListLayout.addView(noLocations);
                    return;
                }

                for (DataSnapshot locSnapshot : snapshot.getChildren()) {
                    String locId = locSnapshot.getKey();
                    String address = locSnapshot.child("address").getValue(String.class);
                    String notes = locSnapshot.child("notes").getValue(String.class);

                    if (address == null) address = "—";
                    if (notes == null) notes = "—";

                    LinearLayout rowLayout = new LinearLayout(InfoActivity.this);
                    rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                    rowLayout.setPadding(0, 8, 0, 8);
                    rowLayout.setGravity(Gravity.CENTER_VERTICAL);

                    String[] values = {address, notes};
                    for (int i = 0; i < values.length; i++) {
                        TextView col = new TextView(InfoActivity.this);
                        col.setText(values[i]);
                        col.setTextSize(14);
                        col.setTextColor(ContextCompat.getColor(InfoActivity.this, R.color.black));
                        col.setMaxLines(1);
                        col.setEllipsize(TextUtils.TruncateAt.END);

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                0, LinearLayout.LayoutParams.WRAP_CONTENT, weights[i]);
                        params.setMarginEnd(8);
                        col.setLayoutParams(params);
                        rowLayout.addView(col);
                    }

                    // Edit Button
                    MaterialButton editButton = createIconButton(R.drawable.ic_edit, R.color.primary);
                    String finalAddress = address;
                    String finalNotes = notes;
                    String finalAddress1 = address;
                    String finalNotes1 = notes;
                    editButton.setOnClickListener(v -> {
                        Map<String, String> data = new HashMap<>();
                        data.put("address", finalAddress1);
                        data.put("notes", finalNotes1);

                        EditItemDialogFragment dialog = new EditItemDialogFragment("Location", locId, data);
                        dialog.show(getSupportFragmentManager(), "EditItemDialog");
                    });

                    // Delete Button
                    MaterialButton deleteButton = createIconButton(R.drawable.ic_delete, R.color.bright_red);
                    deleteButton.setOnClickListener(v -> {
                        locationRef.child(locId).removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(InfoActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                    loadLocations();
                                });
                    });

                    rowLayout.addView(editButton);
                    rowLayout.addView(deleteButton);

                    locationListLayout.addView(rowLayout);
                    addDivider(locationListLayout);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "loadLocations failed", error.toException());
            }
        });
    }

    private Drawable getResizedIcon(int resId, int sizeDp) {
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), resId);

        int sizePx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                sizeDp,
                getResources().getDisplayMetrics()
        );

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, sizePx, sizePx, true);
        return new BitmapDrawable(getResources(), scaledBitmap);
    }
    private void addDivider(LinearLayout parent) {
        View divider = new View(this);
        divider.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                2 // height
        ));
        divider.setBackgroundColor(ContextCompat.getColor(this, R.color.gray)); // Add `light_gray` to your colors.xml
        parent.addView(divider);
    }
    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }
    private MaterialButton createIconButton(int iconRes, int tintColorRes) {
        MaterialButton button = new MaterialButton(this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        button.setIcon(getResizedIcon(iconRes, 20));
        button.setIconTintResource(tintColorRes);
        button.setStrokeColorResource(tintColorRes);
        button.setStrokeWidth(2);
        button.setText("");
        button.setIconPadding(0);
        button.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_START);
        button.setPadding(0, 0, 0, 0);
        button.setInsetTop(0);
        button.setInsetBottom(0);
        button.setMinimumWidth(0);
        button.setMinimumHeight(0);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(36), dpToPx(36));
        params.setMargins(dpToPx(4), 0, dpToPx(4), 0);
        button.setLayoutParams(params);
        return button;
    }
}