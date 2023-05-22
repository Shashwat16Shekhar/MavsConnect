package com.example.superchat.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.superchat.Models.MessageModel;
import com.example.superchat.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.w3c.dom.Text;

import java.sql.Time;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatAdapter extends RecyclerView.Adapter {

    ArrayList<MessageModel> messageModels;
    Context context;
    String recId;

    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;

    boolean GROUP_CHAT_VIEW = false;

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context) {
        this.messageModels = messageModels;
        this.context = context;
    }

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context, String recId) {
        this.messageModels = messageModels;
        this.context = context;
        this.recId = recId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender , parent , false);
            return new SenderViewHolder(view);
        }
        else
        {
            View view;
            if(this.messageModels.get(0).getUserName()==null){
                GROUP_CHAT_VIEW = false;
                view = LayoutInflater.from(context).inflate(R.layout.sample_receiver, parent, false);
            }
            else{
                GROUP_CHAT_VIEW = true;
                view = LayoutInflater.from(context).inflate(R.layout.sample_grp_receiver, parent, false);
            }
            return new ReceiverViewHolder(view);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (messageModels.get(position).getuId().equals(FirebaseAuth.getInstance().getUid()))
        {
            return SENDER_VIEW_TYPE;
        }
        else {
            return RECEIVER_VIEW_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel = messageModels.get(position);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View  v) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to delete this message")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                String senderRoom = FirebaseAuth.getInstance().getUid() + recId;
                                database.getReference().child("chats").child(senderRoom)
                                        .child(messageModel.getMessageId())
                                        .setValue(null);
                            }
                        }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();


                return false;
            }
        });

        if (holder.getClass() == SenderViewHolder.class){
            if(messageModel.getImageUrl()==null || messageModel.getImageUrl().equals("")){
                ((SenderViewHolder)holder).senderMsg.setText(messageModel.getMessage());
            }else{
                ((SenderViewHolder)holder).senderMsg.setVisibility(View.GONE);
                ((SenderViewHolder)holder).senderImage.setVisibility(View.VISIBLE);
                // Load the image from Firebase Storage and display it in the ImageView
                Picasso.get().load(messageModel.getImageUrl())
                        .into(((SenderViewHolder)holder).senderImage);
            }

            Date d = new Date();
            d.setTime(messageModel.getTimestamp());
            String time = String.valueOf(d.getHours())+":"+String.valueOf(d.getMinutes());
            ((SenderViewHolder)holder).senderTime.setText(time);
        }
        else {

            if(messageModel.getImageUrl()==null || messageModel.getImageUrl().equals("")){
                ((ReceiverViewHolder)holder).receiverMsg.setText(messageModel.getMessage());
            }else{
                ((ReceiverViewHolder)holder).receiverMsg.setVisibility(View.GONE);
                ((ReceiverViewHolder)holder).receiveImage.setVisibility(View.VISIBLE);
                // Load the image from Firebase Storage and display it in the ImageView
                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(String.valueOf(messageModel.getImageUrl()));
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri)
                                .into(((ReceiverViewHolder)holder).receiveImage);
                    }
                });
            }


            if(GROUP_CHAT_VIEW){
                ((ReceiverViewHolder)holder).userName.setText(messageModel.getUserName());
            }
            Date d = new Date();
            d.setTime(messageModel.getTimestamp());
            String time = String.valueOf(d.getHours())+":"+String.valueOf(d.getMinutes());
            ((ReceiverViewHolder)holder).receiverTime.setText(time);
        }

    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {

        TextView receiverMsg, userName, receiverTime;
        ImageView receiveImage;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMsg = itemView.findViewById(R.id.receiverText);
            userName = itemView.findViewById(R.id.userName);
            receiverTime = itemView.findViewById(R.id.receiverTime);
            receiveImage = itemView.findViewById(R.id.receiveImage);
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {

        TextView senderMsg , senderTime;
        ImageView senderImage;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMsg = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.senderTime);
            senderImage = itemView.findViewById(R.id.senderImage);
        }
    }


}
