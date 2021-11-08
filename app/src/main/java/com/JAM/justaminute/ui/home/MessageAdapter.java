package com.JAM.justaminute.ui.home;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.JAM.justaminute.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    ArrayList<MessageModel> list;
    MediaPlayer mediaPlayer,mediaPlayer1;
    boolean click=true;
    int pos;
    MediaController mediaController,mediaController1;


    public MessageAdapter(Context context,ArrayList<MessageModel> list) {
        this.context = context;
        this.list=list;
    }

    private class TextTypeViewHolder extends RecyclerView.ViewHolder {
        TextView textView,text,timestamp,personname;

        public TextTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.textMessage);
            text=itemView.findViewById(R.id.textMessage1);
            timestamp = itemView.findViewById(R.id.timestamp);
            personname = itemView.findViewById(R.id.person_name_chat);
        }
    }

    private class AudioTypeViewHolder extends RecyclerView.ViewHolder {
        ImageButton imageButton; ImageButton button;
        SeekBar seekBar;SeekBar bar;
        TextView textView,text,timestamp,personname;
        public AudioTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            imageButton=itemView.findViewById(R.id.play);
            seekBar=itemView.findViewById(R.id.seekBar);
            textView=itemView.findViewById(R.id.textView);
            button=itemView.findViewById(R.id.pause);
            bar=itemView.findViewById(R.id.bar);
            text=itemView.findViewById(R.id.text);
            timestamp = itemView.findViewById(R.id.timestamp);
            personname = itemView.findViewById(R.id.person_name_chat);
        }
    }
    private class ImageTypeViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView,imageView1;
        TextView timestamp,personname;
        public ImageTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.background);
            imageView1=itemView.findViewById(R.id.background1);
            timestamp = itemView.findViewById(R.id.timestamp);
            personname = itemView.findViewById(R.id.person_name_chat);
            Display display = ((WindowManager)
                    context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            int height = display.getHeight();
            int width = display.getWidth();

            align(height,width);

        }
        void align(int height,int width){
            try{
                imageView.getLayoutParams().width = width*3/5;
                imageView.getLayoutParams().height = width*3/4;
            }
            catch (Exception e){

            }
            try {
                imageView1.getLayoutParams().width = width*3/5;
                imageView1.getLayoutParams().height = width*3/4;
            }
            catch (Exception e){

            }


        }
    }
    private class VideoTypeViewHolder extends RecyclerView.ViewHolder {
        ImageView videoImg,vidPlay,videoImg1;
        RelativeLayout select;
        TextView timestamp,personname;
        public VideoTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            videoImg=itemView.findViewById(R.id.videoImg);
            videoImg1=itemView.findViewById(R.id.videoImg1);
            vidPlay=itemView.findViewById(R.id.vidPlay);
            select=itemView.findViewById(R.id.select);
            timestamp = itemView.findViewById(R.id.timestamp);
            personname = itemView.findViewById(R.id.person_name_chat);
            Display display = ((WindowManager)
                    context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            int height = display.getHeight();
            int width = display.getWidth();

            align(height,width);
        }
        void align(int height,int width){
            try {
                videoImg.getLayoutParams().width = width*3/5;
                videoImg.getLayoutParams().height = width*3/4;
            }
            catch (Exception e ){
            }
            try{
                videoImg1.getLayoutParams().width = width*3/5;
                videoImg1.getLayoutParams().height = width*3/4;
            }
            catch (Exception e){}

        }
    }
    private class DocumentViewHolder extends RecyclerView.ViewHolder
    {
        ImageView docs,docs1;
        TextView docmsg,docmsg1,timestamp,personname;
        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            docs=itemView.findViewById(R.id.doc);
            docs1=itemView.findViewById(R.id.doc1);
            docmsg = itemView.findViewById(R.id.doc_msg);
            docmsg1 = itemView.findViewById(R.id.doc_msg1);
            timestamp = itemView.findViewById(R.id.timestamp);
            personname = itemView.findViewById(R.id.person_name_chat);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType)
        {
            case MessageModel.MESSAGE_TYPE_IN:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_msg_in, parent, false);
                return new TextTypeViewHolder(view);
            case MessageModel.MESSAGE_TYPE_OUT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_msg_out, parent, false);
                return new TextTypeViewHolder(view);
            case MessageModel.IMAGE_TYPE_IN:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_image_in, parent, false);
                return new ImageTypeViewHolder(view);
            case MessageModel.IMAGE_TYPE_OUT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_image_out, parent, false);
                return new ImageTypeViewHolder(view);
            case MessageModel.AUDIO_TYPE_IN:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_audio_in, parent, false);
                return new AudioTypeViewHolder(view);
            case MessageModel.AUDIO_TYPE_OUT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_audio_out, parent, false);
                return new AudioTypeViewHolder(view);
            case MessageModel.VIDEO_TYPE_IN:
                view=LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_video_in, parent, false);
                return new VideoTypeViewHolder(view);
            case MessageModel.VIDEO_TYPE_OUT:
                view=LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_video_out, parent, false);
                return new VideoTypeViewHolder(view);
            case MessageModel.DOCUMENT_TYPE_IN:
                view=LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_document_in, parent, false);
                return new DocumentViewHolder(view);
            case MessageModel.DOCUMENT_TYPE_OUT:
                view=LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_document_out, parent, false);
                return new DocumentViewHolder(view);
        }
        return null;

    }

    @Override
    public int getItemViewType(int position) {
        switch (list.get(position).type)
        {
            case 1:
                return MessageModel.MESSAGE_TYPE_IN;
            case 2:
                return MessageModel.MESSAGE_TYPE_OUT;
            case 3:
                return MessageModel.AUDIO_TYPE_IN;
            case 4:
                return MessageModel.AUDIO_TYPE_OUT;
            case 5:
                return MessageModel.IMAGE_TYPE_IN;
            case 6:
                return MessageModel.IMAGE_TYPE_OUT;
            case 7:
                return MessageModel.VIDEO_TYPE_IN;
            case 8:
                return MessageModel.VIDEO_TYPE_OUT;
            case 9:
                return MessageModel.DOCUMENT_TYPE_IN;
            case 10:
                return MessageModel.DOCUMENT_TYPE_OUT;
            default:
                return -1;

        }

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final MessageModel object=list.get(position);
        if(object!=null)
        {
            switch (object.type)
            {
                case MessageModel.MESSAGE_TYPE_IN:
                    ((TextTypeViewHolder) holder).textView.setText(object.getMessage());
                    ((TextTypeViewHolder) holder).timestamp.setText(getTimefromUTC(object.getTimestamp()));
                    break;
                case MessageModel.MESSAGE_TYPE_OUT:
                    ((TextTypeViewHolder) holder).text.setText(object.getMessage());
                    ((TextTypeViewHolder) holder).timestamp.setText(getTimefromUTC(object.getTimestamp()));
                    ((TextTypeViewHolder) holder).personname.setText(object.getName());
                    break;
                case MessageModel.AUDIO_TYPE_IN:

                        mediaPlayer=MediaPlayer.create(context, Uri.parse(object.getRes()));
                        ((AudioTypeViewHolder) holder).seekBar.setMax(mediaPlayer.getDuration());

                        ((AudioTypeViewHolder) holder).imageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(click) {
                                    click=false;
                                    ((AudioTypeViewHolder) holder).imageButton.setBackgroundResource(R.drawable.pause_white);
                                    mediaPlayer.start();
                                }
                                else
                                {
                                    mediaPlayer.pause();
                                    ((AudioTypeViewHolder) holder).imageButton.setBackgroundResource(R.drawable.play_white);
                                    click=true;
                                }

                            }
                        });
                        ((AudioTypeViewHolder) holder).seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                pos=progress;
                                int time=pos/1000;
                                if(time>60)
                                {
                                    int sec=time%60;
                                    if(sec<10) {
                                        ((AudioTypeViewHolder) holder).textView.setText("0"+time/60+ ":0"+sec);
                                    }
                                    else
                                    {
                                        ((AudioTypeViewHolder) holder).textView.setText("0" + time / 60+":"+sec);
                                    }
                                }
                                else {
                                    int sec=time%60;
                                    if(sec<10) {
                                        ((AudioTypeViewHolder) holder).textView.setText("00" + ":0" + time % 60);
                                    }
                                    else
                                    {
                                        ((AudioTypeViewHolder) holder).textView.setText("00"+":"+time % 60);
                                    }
                                }
                                if(pos==seekBar.getMax())
                                {
                                    mediaPlayer.seekTo(0);
                                    seekBar.setProgress(0);
                                    mediaPlayer.pause();
                                    ((AudioTypeViewHolder) holder).imageButton.setBackgroundResource(R.drawable.play_white);
                                    click=false;
                                }
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {
                                mediaPlayer.pause();
                                ((AudioTypeViewHolder) holder).imageButton.setBackgroundResource(R.drawable.play_white);

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                mediaPlayer.start();
                                mediaPlayer.seekTo(pos);
                                ((AudioTypeViewHolder) holder).imageButton.setBackgroundResource(R.drawable.pause_white);
                                if(seekBar.getProgress()==0)
                                {
                                    mediaPlayer.seekTo(0);
                                    seekBar.setProgress(0);
                                    mediaPlayer.pause();
                                    ((AudioTypeViewHolder) holder).imageButton.setBackgroundResource(R.drawable.play_white);
                                    click=true;
                                }
                            }
                        });
                        new Timer().scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                ((AudioTypeViewHolder) holder).seekBar.setProgress(mediaPlayer.getCurrentPosition());

                            }
                        },0,1000);
                    break;
                case MessageModel.AUDIO_TYPE_OUT:

                    mediaPlayer1=MediaPlayer.create(context,Uri.parse(object.getRes()));
                    ((AudioTypeViewHolder) holder).bar.setMax(mediaPlayer1.getDuration());
                    ((AudioTypeViewHolder) holder).button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(click) {
                                click=false;
                                ((AudioTypeViewHolder) holder).button.setBackgroundResource(R.drawable.pause_white);
                                mediaPlayer1.start();
                            }
                            else
                            {
                                mediaPlayer1.pause();
                                ((AudioTypeViewHolder) holder).button.setBackgroundResource(R.drawable.play_white);
                                click=true;
                            }

                        }
                    });
                    ((AudioTypeViewHolder) holder).bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            pos=progress;
                            int time=pos/1000;
                            if(time>60)
                            {
                                int sec=time%60;
                                if(sec<10) {
                                    ((AudioTypeViewHolder) holder).text.setText("0"+time/60+ ":0"+sec);
                                }
                                else
                                {
                                    ((AudioTypeViewHolder) holder).text.setText("0" + time / 60+":"+sec);
                                }
                            }
                            else {
                                int sec=time%60;
                                if(sec<10) {
                                    ((AudioTypeViewHolder) holder).text.setText("00" + ":0" + time % 60);
                                }
                                else
                                {
                                    ((AudioTypeViewHolder) holder).text.setText("00"+":"+time % 60);
                                }
                            }
                            if(pos==seekBar.getMax())
                            {
                                mediaPlayer1.seekTo(0);
                                seekBar.setProgress(0);
                                mediaPlayer1.pause();
                                ((AudioTypeViewHolder) holder).button.setBackgroundResource(R.drawable.play_white);
                                click=false;
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                            mediaPlayer1.pause();
                            ((AudioTypeViewHolder) holder).button.setBackgroundResource(R.drawable.play_white);

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            mediaPlayer1.start();
                            mediaPlayer1.seekTo(pos);
                            ((AudioTypeViewHolder) holder).button.setBackgroundResource(R.drawable.pause_white);
                            if(seekBar.getProgress()==0)
                            {
                                mediaPlayer1.seekTo(0);
                                seekBar.setProgress(0);
                                mediaPlayer1.pause();
                                ((AudioTypeViewHolder) holder).button.setBackgroundResource(R.drawable.play_white);
                                click=true;
                            }
                        }
                    });
                    new Timer().scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            ((AudioTypeViewHolder) holder).bar.setProgress(mediaPlayer.getCurrentPosition());

                        }
                    },0,1000);
                    break;
                case MessageModel.IMAGE_TYPE_IN:

                    try {
                        Picasso.get().load(object.getRes()).into(((ImageTypeViewHolder) holder).imageView);
                    }
                    catch (Exception e){

                    }

                    ((ImageTypeViewHolder) holder).imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent photoIntent=new Intent(context, PhotoViewActivity.class);
                            photoIntent.putExtra("photo",object.getRes());
                            context.startActivity(photoIntent);
                        }
                    });
                    ((ImageTypeViewHolder) holder).timestamp.setText(getTimefromUTC(object.getTimestamp()));
                break;
                case MessageModel.IMAGE_TYPE_OUT:
                    ((ImageTypeViewHolder) holder).timestamp.setText(getTimefromUTC(object.getTimestamp()));
                    ((ImageTypeViewHolder) holder).personname.setText(object.getName());
                        try {
                            Picasso.get().load(object.getRes()).into(((ImageTypeViewHolder) holder).imageView1);
                        } catch (Exception e) {
                            ((ImageTypeViewHolder) holder).imageView1.setImageURI(null);
                        }

                        ((ImageTypeViewHolder) holder).imageView1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent photoIntent = new Intent(context, PhotoViewActivity.class);
                                photoIntent.putExtra("photo", object.getRes());
                                context.startActivity(photoIntent);
                            }
                        });

                    break;
                case MessageModel.VIDEO_TYPE_IN:
                        ((VideoTypeViewHolder) holder).timestamp.setText(getTimefromUTC(object.getTimestamp()));
                        Glide.with(context).load(object.getRes()).into(((VideoTypeViewHolder) holder).videoImg);
                        ((VideoTypeViewHolder) holder).select.setAlpha(0);
                        ((VideoTypeViewHolder) holder).select.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, VideoViewActivity.class);
                                intent.putExtra("name", object.getRes());
                                context.startActivity(intent);
                            }
                        });
                break;
                case MessageModel.VIDEO_TYPE_OUT:
                    ((VideoTypeViewHolder) holder).timestamp.setText(getTimefromUTC(object.getTimestamp()));
                    ((VideoTypeViewHolder) holder).personname.setText(object.getName());
                        Glide.with(context).load(object.getRes()).into(((VideoTypeViewHolder) holder).videoImg1);
                        ((VideoTypeViewHolder) holder).select.setAlpha(0);
                        ((VideoTypeViewHolder) holder).select.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, VideoViewActivity.class);
                                intent.putExtra("name", object.getRes());
                                context.startActivity(intent);
                            }
                        });

                    break;
                case MessageModel.DOCUMENT_TYPE_IN:
                    ((DocumentViewHolder) holder).timestamp.setText(getTimefromUTC(object.getTimestamp()));
                    ((DocumentViewHolder) holder).docs.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent docIntent=new Intent(Intent.ACTION_VIEW,Uri.parse(object.getRes()));
                            context.startActivity(docIntent);
                        }
                    });
                    ((DocumentViewHolder) holder).docmsg.setText(object.message);
                    break;
                case MessageModel.DOCUMENT_TYPE_OUT:
                    ((DocumentViewHolder) holder).timestamp.setText(getTimefromUTC(object.getTimestamp()));
                    ((DocumentViewHolder) holder).personname.setText(object.getName());
                    ((DocumentViewHolder) holder).docs1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent docIntent=new Intent(Intent.ACTION_VIEW, Uri.parse(object.getRes()));
                            context.startActivity(docIntent);
                        }
                    });
                    ((DocumentViewHolder) holder).docmsg1.setText(object.message);
                    break;

            }
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    String getTimefromUTC(int time){
        String ctime="";
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time* 1000L);
        cal.setTimeZone(TimeZone.getDefault());
        if( cal.get(Calendar.HOUR) <10){

            if(cal.get(Calendar.HOUR) == 0){
                ctime+="12"+":";
            }
            else{
                ctime+="0"+cal.get(Calendar.HOUR)+":";
            }
        }
        else {
            ctime+=cal.get(Calendar.HOUR)+":";
        }
        if(cal.get(Calendar.MINUTE) <10){
            ctime+="0"+cal.get(Calendar.MINUTE);
        }
        else{
            ctime+=cal.get(Calendar.MINUTE);
        }
        if(cal.get(Calendar.AM_PM) == 0){
            ctime+=" am";
        }
        else{
            ctime+=" pm";
        }

        return ctime;
    }
}
