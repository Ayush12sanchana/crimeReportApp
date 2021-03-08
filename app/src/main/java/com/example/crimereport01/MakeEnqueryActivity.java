package com.example.crimereport01;

import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MakeEnqueryActivity extends AppCompatActivity {

    public String UID;
    public ImageView homeIcon;
    public Button sendBtn;
    public LinearLayout addImage, addVideo, addLocation;
    public EditText uText;
    public Spinner crimeType;
    public FirebaseFirestore mfirebaseFirestore;
    public CollectionReference reference,reference2;
   // public ProgressDialog progressDialog;

    HashMap<String, String> hashMapv;
    HashMap<String, String> hashMap;
    String dateStr = "04/05/2010";
    String newDateStr;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_enquery);

        addImage = findViewById(R.id.image_attachment);
        addVideo = findViewById(R.id.video_attachment);
        addLocation = findViewById(R.id.set_location_btn);
        sendBtn = findViewById(R.id.send_button);
        uText = findViewById(R.id.set_text);
        crimeType = findViewById(R.id.spinner);
        homeIcon = findViewById(R.id.home_icon);
        final ProgressDialog progressDialog = new ProgressDialog(this);

        SimpleDateFormat curFormater = new SimpleDateFormat("dd/MM/yyyy");
        Date dateObj = null;
        try {
            dateObj = curFormater.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat postFormater = new SimpleDateFormat("MMMM dd, yyyy");

        newDateStr = postFormater.format(dateObj);



        mfirebaseFirestore = FirebaseFirestore.getInstance();


        Intent intent1 = getIntent();
        UID = intent1.getSerializableExtra("uid").toString();
        Log.d("TAG", "||||||||||||||||------------->>>>>>>>>>>>>>>>>>>>>>" + UID);

        hashMapv= new HashMap<>();
        hashMap= new HashMap<>();

        //Get data from ImageActivity
        Intent intent = getIntent();
        hashMap = (HashMap<String, String>) intent.getSerializableExtra("hashMap");
        UID = intent.getSerializableExtra("uid").toString();
        Log.d("TAG", "||||||||||||||||------------->>>>>>>>>>>>>>>>>>>>>>" + hashMap);
        Log.d("TAG", "||||||||||||||||------------->>>>>>>>>>>>>>>>>>>>>>" + UID);
        //End function

        //get data from videoActivity
        Intent intentv = getIntent();
        hashMapv = (HashMap<String, String>) intentv.getSerializableExtra("hashMapv");
        UID = intentv.getSerializableExtra("uid").toString();
        Log.d("TAG", "||||||||||||||||------------->>>>>>>>>>>>>>>>>>>>>>" + hashMapv);
        Log.d("TAG", "||||||||||||||||------------->>>>>>>>>>>>>>>>>>>>>>" + UID);
        //End function

        //Start AddVideo Button Functions
        addVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("uid",UID);


                Intent intent =  new Intent(MakeEnqueryActivity.this, AddVideoActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //End AddVideo Button Functions

        //Start AddImage Button Function
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("uid",UID);

                Intent intent =  new Intent(MakeEnqueryActivity.this, SelectImageActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        //End AddImage Button Function

        //Start Send Button Function
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference documentReference = mfirebaseFirestore.collection("Crime").document(UID).collection(crimeType.getSelectedItem().toString()).document(newDateStr.toString());
                Map<String, Object> user = new HashMap<>();
                user.put("Uid", UID);
                user.put("CrimeType", crimeType.getSelectedItem().toString());
                user.put("UserText", uText.toString());
                user.put("Date", newDateStr.toString());
                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(MakeEnqueryActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();
                        progressDialog.setMessage("Message Sent");
                        Bundle bundle = new Bundle();
                        bundle.putString("uid",UID);
                        Intent intent3= new Intent(MakeEnqueryActivity.this, MainMenuActivity.class);
                        intent3.putExtras(bundle);
                        startActivity(intent3);

                        /*reference = mfirebaseFirestore.collection("Evidence").document(newDateStr.toString()).collection(UID);
                        reference2 = mfirebaseFirestore.collection("VideoEvidence").document(newDateStr.toString()).collection(UID);

                        if(hashMap.size()<1){

                            progressDialog.setMessage("Message Sent");
                        }else {

                            //Start Set Images
                            reference.add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    //progressDialog.dismiss();
                                    //coreHelper.createAlert("Success", "Images uploaded and saved successfully!", "OK", "", null, null, null);


                                    progressDialog.setMessage("Message Sent");

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // progressDialog.dismiss();
                                    //coreHelper.createAlert("Error", "Images uploaded but we couldn't save them to database.", "OK", "", null, null, null);
                                    // Log.e("MainActivity:SaveData", e.getMessage());
                                }
                            });
                        }

                        if(hashMapv.size()<1){

                            progressDialog.setMessage("Message Sent");
                        }else{
                            //Start Set Images
                            reference2.add(hashMapv).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    //progressDialog.dismiss();
                                    //coreHelper.createAlert("Success", "Images uploaded and saved successfully!", "OK", "", null, null, null);

                                    progressDialog.setMessage("Message Sent");

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // progressDialog.dismiss();
                                    //coreHelper.createAlert("Error", "Images uploaded but we couldn't save them to database.", "OK", "", null, null, null);
                                    // Log.e("MainActivity:SaveData", e.getMessage());
                                }
                            });
                        }*/

                    }
                });

            }
        });
        //End Send Button Function

        homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("uid",UID);
                Intent intent2= new Intent(MakeEnqueryActivity.this, MainMenuActivity.class);
                intent2.putExtras(bundle);
                startActivity(intent2);
            }
        });
    }


}
