package adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.betalabs.factcheck.R;
import com.betalabs.factcheck.comment;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class Adapter_comment extends RecyclerView.Adapter<Adapter_comment.Myholder> {



Context context;
List<comment> commentList;




    public Adapter_comment(Context context, List<comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }



    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.new_comment, parent, false);

        return new Myholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Myholder holder, int i) {

        String dbname = commentList.get(i).getName();
        String dbcomment = commentList.get(i).getComment();
        String dbcomment_id = commentList.get(i).getComment_id();
        final String db_profile = commentList.get(i).getProfile_image();


        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(dbcomment_id));
        String ptime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        holder.histime.setText(ptime);
        holder.hisname.setText(dbname);
        holder.hiscomment.setText(dbcomment);



        try
        {

            Picasso
                    .get()
                    .load(db_profile)
                    .fetch(new Callback() {
                        @Override
                        public void onSuccess() {
                            Picasso
                                    .get()
                                    .load(db_profile)
                                    .networkPolicy(NetworkPolicy.OFFLINE)
                                    .into(holder.hisdp);

                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });

        }
        catch (Exception e)
        {

            Uri uri = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
            holder.hisdp.setImageURI(uri);
            Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
        }





    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }


    class Myholder extends RecyclerView.ViewHolder{
        CircleImageView hisdp;
        TextView hisname, hiscomment, histime;




        public Myholder(@NonNull View itemView)
        {
            super(itemView);
            hisdp = itemView.findViewById(R.id.hisdp);
            hiscomment = itemView.findViewById(R.id.hiscomment);
            hisname = itemView.findViewById(R.id.hisname);
            histime = itemView.findViewById(R.id.histime);



        }
    }

}
