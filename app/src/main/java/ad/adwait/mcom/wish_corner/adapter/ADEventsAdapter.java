package ad.adwait.mcom.wish_corner.adapter;

import ad.adwait.mcom.R;
import ad.adwait.mcom.wish_corner.model.ADEventsModel;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import ad.adwait.mcom.wish_corner.model.ADEventsModel;
import and.com.polam.utils.ADBaseActivity;

public class ADEventsAdapter extends RecyclerView.Adapter<ADEventsAdapter.EventHolder> {
    private final int mScreenWidth;
    private final int mScreenHeight;
    private List<ADEventsModel> mEventsData;
    private Context mContext;

    public void setmEventsData(List<ADEventsModel> eventsData) {
        mEventsData = new ArrayList<ADEventsModel>();
        this.mEventsData.addAll(eventsData);
        notifyDataSetChanged();
    }

    public ADEventsAdapter(Context context, List<ADEventsModel> messageList) {
        mEventsData = messageList;
        mContext = context;
        mScreenWidth = (int) (((ADBaseActivity) context).getScreenDetails(false) * 0.45);
        mScreenHeight = (int) (((ADBaseActivity) context).getScreenDetails(false) * 0.23);
        Log.i("Test", "mScreenHeight -> " + mScreenHeight + " mScreenWidth -> " + mScreenWidth);
    }

    @Override
    public int getItemCount() {
        if (mEventsData == null) {
            return 0;
        }
        return mEventsData.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        return position;

    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item_layout, parent, false);
        EventHolder holder = new EventHolder(view);
        holder.eventImage.getLayoutParams().width = mScreenWidth;
        holder.eventImage.getLayoutParams().height = mScreenHeight;
        return holder;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(EventHolder holder, int position) {
        ADEventsModel dataItem = (ADEventsModel) mEventsData.get(position);
        ((EventHolder) holder).bind(dataItem);
    }

    public class EventHolder extends RecyclerView.ViewHolder {
        TextView date, name, description;
        ImageView eventImage;

        EventHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date);
            name = (TextView) itemView.findViewById(R.id.event_name);
            description = (TextView) itemView.findViewById(R.id.event_desc);
            eventImage = (ImageView) itemView.findViewById(R.id.event_image);
        }

        void bind(ADEventsModel data) {
            date.setText(data.getDate());
            name.setText(data.getEventName());
            description.setText(data.getDescription());

            if (!data.getImageUrl().isEmpty()) {
                Glide.with(mContext).load(data.getImageUrl()).diskCacheStrategy(
                        DiskCacheStrategy.SOURCE
                ).error(R.drawable.image_not_found).into(eventImage);
            }
        }
    }
}
