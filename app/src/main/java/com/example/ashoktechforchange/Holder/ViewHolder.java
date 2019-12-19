package com.example.ashoktechforchange.Holder;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ashoktechforchange.DescriptionActivity;
import com.example.ashoktechforchange.R;

public class ViewHolder  extends RecyclerView.ViewHolder {
    public  LinearLayout readMore;
    TextView name;
    TextView status;
    TextView department;
    TextView date;
    TextView address;
    TextView like;
    TextView duration;
    public ImageView likeButton;
//    public VideoView videoView;
    ImageView imageView;

    public ViewHolder(View itemView) {
        super(itemView);
        readMore = itemView.findViewById(R.id.card_ll_read_more);
        name = itemView.findViewById(R.id.card_name);
        status = itemView.findViewById(R.id.card_status);
        department = itemView.findViewById(R.id.card_dept);
        date = itemView.findViewById(R.id.card_date);
        address = itemView.findViewById(R.id.card_addr);
        like = itemView.findViewById(R.id.card_like);
        likeButton = itemView.findViewById(R.id.card_iv_like);
        duration = itemView.findViewById(R.id.card_duration);

//        videoView = itemView.findViewById(R.id.imageComplaintVideo);
    }

    public void setName(String name) {
        this.name.setText(name);
    }

    public void setStatus(int status, Context context) {
        String taskStatus = "NA";
        int background = R.drawable.background_status_1 ;
        int color = Color.parseColor("#E60000");
        switch (status){
            case 1 : taskStatus = "PENDING";
                    background = R.drawable.background_status_1;
                    color = Color.parseColor("#E60000");
                    break;
            case 2 : taskStatus = "IN PROCESS";
                background = R.drawable.background_status_2;
                color = Color.parseColor("#FF7800");
                break;
            case 3 : taskStatus = "RESOLVED";
                background = R.drawable.background_status_3;
                color = Color.parseColor("#4CBB17");
                break;

            default : taskStatus = "NA"; break;
        }
        this.status.setText(taskStatus);
        this.status.setBackground(ContextCompat.getDrawable(context, background));
        this.status.setTextColor(color);
    }

    public void setDuration(String duration){
        this.duration.setText(duration);
    }

    public void setDepartment(String  department) {
        this.department.setText(department);
    }

    public void setDate(String date) {
        this.date.setText(date);
    }

    public void setAddress(String  address) {
        this.address.setText(address);
    }

    public void setLike(int like) {
        this.like.setText(String.valueOf(like));
    }

    public void setLikeButton(String isLiked, Context context) {
        if (isLiked.equals("yes")){
            this.likeButton.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_thumb_up_green_24dp));
        }
        else {
            this.likeButton.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_thumb_up_grey_24dp));
        }
    }


    public void setVideoView(String videoUrl){
        Uri uri = Uri.parse(videoUrl);
//        this.videoView.setVideoURI(uri);

    }
}