package com.JAM.justaminute.ui.home.Group;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.JAM.justaminute.R;
import com.JAM.justaminute.ui.home.Person.Chat;
import com.JAM.justaminute.ui.home.Person.FriendProfile;
import com.JAM.justaminute.ui.home.HomePage.RecyclerViewClickInterfaceDis;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<DisplayMembersModel> names;
    RecyclerViewClickInterfaceDis clickInterface1;
    Context context;
    Button button1,button2,button3;
    TextView phone,userName;
    CircleImageView image;
    ImageButton button4;
    Dialog myDialog;
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    String my_id;

    public GroupAdapter(ArrayList<DisplayMembersModel> names, RecyclerViewClickInterfaceDis recyclerViewClickInterface, Context context) {
        this.names = names;
        clickInterface1 = recyclerViewClickInterface;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_group_persons_addview, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        my_id = mFirebaseUser.getEmail().substring(0, mFirebaseUser.getEmail().indexOf('@'));
        my_id = validate_mailid(my_id);
        ((ViewHolder) holder).textViewName.setText(names.get(position).getPersonName());
        try{
            Picasso.get().load(names.get(position).getPersonImage()).into(((ViewHolder) holder).imageView);
        }
        catch (Exception e){
            ((ViewHolder) holder).imageView.setImageResource(R.drawable.person_white);
        }
        if (names.get(position).getPerson_id().compareTo(my_id) == 0) {
            ((ViewHolder) holder).textViewName.setText("Me");
        }
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        CircleImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            textViewName = (TextView) itemView.findViewById(R.id.person_name_add_dis);

            imageView = (CircleImageView) itemView.findViewById(R.id.dis_person_img);
            Display display = ((WindowManager)
                    context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            int height = display.getHeight();
            int width = display.getWidth();

            align(height,width);

            myDialog=new Dialog(context);
            myDialog.setContentView(R.layout.dialog_contact);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickInterface1.onItemClick1(getAdapterPosition());
                    mFirebaseAuth = FirebaseAuth.getInstance();
                    mFirebaseUser = mFirebaseAuth.getCurrentUser();
                    my_id = mFirebaseUser.getEmail().substring(0, mFirebaseUser.getEmail().indexOf('@'));
                    my_id = validate_mailid(my_id);
                    if (names.get(getAdapterPosition()).getPerson_id().compareTo(my_id) != 0) {
                        userName = myDialog.findViewById(R.id.userName);
                        phone = myDialog.findViewById(R.id.number);
                        image = myDialog.findViewById(R.id.userPic);
                        button1 = myDialog.findViewById(R.id.callbutton);
                        button2 = myDialog.findViewById(R.id.chatButton);
                        button3 = myDialog.findViewById(R.id.videoButton);
                        button4 = myDialog.findViewById(R.id.infoButton);

                        userName.setText(names.get(getAdapterPosition()).getPersonName());


                        try {
                            Picasso.get().load(names.get(getAdapterPosition()).getPersonImage()).into(image);
                        } catch (Exception e) {
                            image.setImageResource(R.drawable.person_white);
                        }


                        myDialog.show();

                        button2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, Chat.class);
                                intent.putExtra("person", names.get(getAdapterPosition()).getPerson_id());
                                intent.putExtra("person_dp", names.get(getAdapterPosition()).getPersonImage());
                                intent.putExtra("person_name", names.get(getAdapterPosition()).getPersonName());
                                context.startActivity(intent);
                            }
                        });

                        button4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, FriendProfile.class);
                                intent.putExtra("person", names.get(getAdapterPosition()).getPerson_id());
                                context.startActivity(intent);
                            }
                        });
                    }
                }
            });

        }
        private void align(int height,int width){
            int img_width_pad = width/60;
            imageView.getLayoutParams().height = width/5;
            imageView.getLayoutParams().width = width/5;
            imageView.setPadding(img_width_pad,img_width_pad,img_width_pad,img_width_pad);
            textViewName.setMaxWidth(width/5);
        }
    }
    String validate_mailid(String mail_id){

        char mail[] = mail_id.toCharArray();
        for (int i=0;i<mail.length;i++){
            if(mail[i] == '.'){
                mail[i] = ',';
            }
        }
        mail_id = String.valueOf(mail);
        return mail_id;
    }
}