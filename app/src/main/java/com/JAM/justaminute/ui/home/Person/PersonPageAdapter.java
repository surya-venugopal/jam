package com.JAM.justaminute.ui.home.Person;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.JAM.justaminute.R;
import com.JAM.justaminute.ui.home.HomePage.List_Users;
import com.JAM.justaminute.ui.home.HomePage.RecyclerViewClickInterface;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonPageAdapter extends RecyclerView.Adapter<PersonPageAdapter.personViewHolder>  {

    public static RecyclerViewClickInterface recyclerViewClickInterface;

    List_Users person;
    private List<List_Users> personList;
    Dialog myDialog;
    Context context;
    TextView userName;
    TextView phone;
    Button button1,button2,button3;
    ImageButton button4;
    CircleImageView image;

    public PersonPageAdapter(Context context, List<List_Users> personList, RecyclerViewClickInterface recyclerViewClickInterface) {
        this.personList = personList;
        this.recyclerViewClickInterface=recyclerViewClickInterface;
        this.context=context;

    }
    @NonNull
    @Override
    public personViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.display_listview,parent,false);
        final personViewHolder holder=new personViewHolder(view);
        myDialog=new Dialog(context);
        myDialog.setContentView(R.layout.dialog_contact);
        holder.personImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(personList.get(holder.getAdapterPosition()).getType() ==0) {
                    userName = myDialog.findViewById(R.id.userName);

                    phone = myDialog.findViewById(R.id.number);
                    image = myDialog.findViewById(R.id.userPic);
                    button1 = myDialog.findViewById(R.id.callbutton);
                    button2 = myDialog.findViewById(R.id.chatButton);
                    button3 = myDialog.findViewById(R.id.videoButton);
                    button4 = myDialog.findViewById(R.id.infoButton);

                    userName.setText(personList.get(holder.getAdapterPosition()).getPersonName());


                    try {
                        Picasso.get().load(personList.get(holder.getAdapterPosition()).getPersonImage()).into(image);
                    } catch (Exception e) {
                        image.setImageResource(R.drawable.person_white);
                    }


                       myDialog.show();

                    button2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, Chat.class);
                            intent.putExtra("person", personList.get(holder.getAdapterPosition()).getPerson_id());
                            intent.putExtra("person_dp", personList.get(holder.getAdapterPosition()).getPersonImage());
                            intent.putExtra("person_name", personList.get(holder.getAdapterPosition()).getPersonName());
                            context.startActivity(intent);
                        }
                    });

                    button4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, FriendProfile.class);
                            intent.putExtra("person", personList.get(holder.getAdapterPosition()).getPerson_id());
                            context.startActivity(intent);
                        }
                    });
                }

            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull personViewHolder holder, int position) {
        person=personList.get(position);
        holder.personName.setText(person.getPersonName());
        holder.lastText.setText(person.getLast_text());
        holder.lastTime.setText(person.getLast_time());
        try{
            holder.personImage.setImageResource(R.drawable.person);

            if(!person.getPersonImage().isEmpty())
            Picasso.get().load(person.getPersonImage()).into(holder.personImage);
        }
        catch (Exception e){
            holder.personImage.setImageResource(R.drawable.person);

        }


    }

    @Override
    public int getItemCount() {
        return personList.size();
    }


    class personViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener
    {
        public TextView personName,lastText,lastTime,space;
        public CircleImageView personImage;
        public ConstraintLayout layout;
        public RecyclerView recyclerView;

        public personViewHolder(@NonNull final View itemView) {
            super(itemView);
            personName=itemView.findViewById(R.id.profileName);
            lastText = itemView.findViewById(R.id.last_text);
            lastTime = itemView.findViewById(R.id.last_time);
            personImage=itemView.findViewById(R.id.profileImage);
            space=itemView.findViewById(R.id.space1);
            layout = itemView.findViewById(R.id.constraintLayout_listview);
            recyclerView=itemView.findViewById(R.id.homeRecycler);

            Display display = ((WindowManager)
                    context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            int height = display.getHeight();
            int width = display.getWidth();

            align(height,width);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewClickInterface.onItemClick(getAdapterPosition());
                }
            });

        }

        private void align(int height,int width){
            space.setWidth(width*3/4);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select an option");
            menu.add(this.getAdapterPosition(),121,0,"Delete this item");
            menu.add(this.getAdapterPosition(),122,1,"Archive");
        }
    }
}
