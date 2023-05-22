package com.example.superchat;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.superchat.Adapters.ChatAdapter;
import com.example.superchat.Models.MessageModel;
import com.example.superchat.Models.Users;
import com.example.superchat.databinding.ActivityGroupChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;

public class HousingActivity extends AppCompatActivity {

    ActivityGroupChatBinding binding;
    final String[] uName = new String[1];

    private static final int PICK_IMAGE_REQUEST = 1;
    FirebaseAuth auth;
    private Uri imageUri;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();
        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HousingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        final ArrayList<MessageModel> messageModels = new ArrayList<>();

        final String senderId = FirebaseAuth.getInstance().getUid();
        binding.userName.setText("Housing Group");

        final ChatAdapter adapter = new ChatAdapter(messageModels, this);
        binding.chatRecyclearView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatRecyclearView.setLayoutManager(layoutManager);

        FirebaseDatabase.getInstance().getReference("Users").child(senderId).addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                uName[0] = user.getUserName();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });


        database.getReference().child("Housing")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageModels.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            MessageModel model = dataSnapshot.getValue(MessageModel.class);
                            messageModels.add(model);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.sendAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.etMessage.getText().toString().isEmpty()){
                    binding.etMessage.setError("Enter some message");
                    return;
                }

                FirebaseAuth oauth = FirebaseAuth.getInstance();
                FirebaseDatabase.getInstance().getReference().child("Users");
                final String uuId = FirebaseAuth.getInstance().getUid();

                final String message = binding.etMessage.getText().toString();
                final MessageModel model = new MessageModel(senderId , message);
                model.setTimestamp(new Date().getTime());
                model.setUserName(uName[0]);
                binding.etMessage.setText("");

                database.getReference().child("Housing")
                        .push()
                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final String senderId = auth.getUid();
        String receiveId = getIntent().getStringExtra("userId");
        final  String senderRoom = senderId + receiveId;
        final  String receiverRoom = receiveId + senderId;
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            imageUri = data.getData();
            // Use the imageUri to send the attachment
            // For example, you can upload the image to Firebase Storage and then send a message with the download URL
            // You can also display the image in the chat by loading the image from the URI using a library like Picasso
            ContentResolver contentResolver = getContentResolver();
            String fileName = null;
            Cursor cursor = contentResolver.query(imageUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                cursor.close();
            }

            FirebaseStorage storage= FirebaseStorage.getInstance();
            String uniqueFilename = new Date().getTime() + "-" + fileName;
            final StorageReference reference = storage.getReference().child("attachments")
                    .child(FirebaseAuth.getInstance().getUid())
                    .child(uniqueFilename);

            reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            MessageModel messageModel = new MessageModel(senderId, "");
                            messageModel.setTimestamp(new Date().getTime());
                            messageModel.setImageUrl(uri.toString());
                            messageModel.setUserName(uName[0]);

                            database.getReference().child("Housing")
                                    .push()
                                    .setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    });

                        }
                    });

                }
            });


        }
    }
}