package com.example.crimereport01;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddVideoActivity extends AppCompatActivity {

    public VideoView videoView;
    public Button uploadBtn;
    public FloatingActionButton floatingActionButton;

    private static final int VIDEO_PICK_GALLERY_CODE = 100;
    private static final int VIDEO_PICK_CAMERA_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;

    private String[] cameraPermission;
    private Uri videoUri = null;
    private ProgressDialog progressDialog;
    public String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video);

        Intent intent1 = getIntent();
        UID = intent1.getSerializableExtra("uid").toString();
        Log.d("TAG", "||||||||||||||||------------->>>>>>>>>>>>>>>>>>>>>>" + UID);

        videoView = findViewById(R.id.videoView);
        uploadBtn = findViewById(R.id.uploadBtn);
        floatingActionButton = findViewById(R.id.pick_video);

        cameraPermission = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //Start progress Dialog Operations
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait...");
        progressDialog.setMessage("Uploading Video");
        progressDialog.setCanceledOnTouchOutside(false);
        //End progress Dialog Operations

        //Start Upload Button Functions
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(videoUri == null){
                    Toast.makeText(AddVideoActivity.this, "Pick a video....", Toast.LENGTH_SHORT).show();
                }else{
                    uploadVideoFirebase();
                }


            }
        });
        //End Upload Button Functions


        //Start Floating Button Action Functions
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoPickDiolog();
            }
        });
        //End Floating Button Action Functions
    }

    private void uploadVideoFirebase() {
        progressDialog.show();

        String timestamp = ""+ System.currentTimeMillis();
        String filePathAndName = "Videos/" + "video_" + timestamp;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(videoUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        Uri downloadUri = uriTask.getResult();

                        if(uriTask.isSuccessful()){

                            HashMap<String, Object> hashMap = new HashMap<>();
                            Log.d("AddVideoActivity.this", "||||||||||------->>>> " + downloadUri.toString());

                            progressDialog.dismiss();

                            Intent intent = new Intent(AddVideoActivity.this, MakeEnqueryActivity.class);
                            intent.putExtra("hashMapv", downloadUri.toString());
                            intent.putExtra("uid",UID);
                            startActivity(intent);

                        }
                    }
                });

    }

    private void videoPickDiolog() {
        String[] options = {"Camera" , "Galerry"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("pick video from")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(i == 0){
                            //Camera clicked
                            if(!checkCameraPermission()){

                                requestCameraPermission();
                            }
                            else
                            {
                                videoPickCamera();
                            }
                        }
                        else if (i == 1){
                            //Gallery clicked
                            videoPickGallery();
                        }
                    }
                }).show();




    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result1 = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean result2 = ContextCompat.checkSelfPermission(this,Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED;
        return  result1 && result2;
    }

    private void videoPickGallery(){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Videos"), VIDEO_PICK_GALLERY_CODE );
    }
    private void videoPickCamera(){
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent,VIDEO_PICK_CAMERA_CODE);
    }

    private void setVideoToVideoView(){
        MediaController mediaController =  new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        videoView.setVideoURI(videoUri);
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                videoView.pause();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if(cameraAccepted && storageAccepted ){

                        videoPickCamera();
                    }else {

                        Toast.makeText(this, "Camera & Storage Permission are required", Toast.LENGTH_SHORT).show();
                    }
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode == RESULT_OK){
            if(requestCode == VIDEO_PICK_GALLERY_CODE){
                videoUri = data.getData();

                setVideoToVideoView();
            }
            else if(requestCode == VIDEO_PICK_CAMERA_CODE){
                videoUri = data.getData();

                setVideoToVideoView();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
