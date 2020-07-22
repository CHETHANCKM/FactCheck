package adapters;

import android.content.Context;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.betalabs.factcheck.R;
import com.betalabs.factcheck.new_notification;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class adapter_noti extends RecyclerView.Adapter<adapter_noti.Myholder> {


    Context context;
    List<new_notification> new_notificationList;
    String uid;

    public adapter_noti(Context context, List<new_notification> new_notification) {
        this.context = context;
        this.new_notificationList = new_notification;
        this.uid = uid;
    }

    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.notification_design, parent, false);

        return new Myholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Myholder holder, int i) {

        String noti_hisdp = new_notificationList.get(i).getProfile_Image();
        String noti_hisname = new_notificationList.get(i).getName();
        String noti_hisimage = new_notificationList.get(i).getImage();
        String noti_histitle = new_notificationList.get(i).getTitle();
        String noti_time = new_notificationList.get(i).getTime_Stamp();


        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(noti_time));
        String ptime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        holder.noti_time.setText(ptime);

        String title = "<b>" + noti_hisname + "</b>" +" has added a new post: "+noti_histitle;
        holder.title.setText(Html.fromHtml(title));



        Picasso.get().load(noti_hisdp).into(holder.hisimage);


        if (noti_hisimage.equals("No Image"))
        {
            holder.postimage.setVisibility(View.GONE);
        }
        else
        {
            Picasso.get().load(noti_hisimage).into(holder.postimage);
        }

    }

    @Override
    public int getItemCount() {
        return new_notificationList.size();
    }


    class Myholder extends RecyclerView.ViewHolder{


        CircleImageView hisimage;
        ImageView postimage;
        TextView title,noti_time;



        public Myholder(@NonNull View itemView)
        {
            super(itemView);
            noti_time = itemView.findViewById(R.id.noti_time);
            hisimage = itemView.findViewById(R.id.noti_pp);
            title = itemView.findViewById(R.id.noti_text);
            postimage = itemView.findViewById(R.id.noti_pic);

        }
    }
}
