package android.adwait.com.wish_corner.adapter;

import android.adwait.com.R;
import android.adwait.com.wish_corner.model.ADEventsModel;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import and.com.polam.utils.ADBaseActivity;

public class ADEventsAdapter extends RecyclerView.Adapter {
    private final int mScreenWidth;
    private List<ADEventsModel> mEventsData;

    public void setmEventsData(List<ADEventsModel> eventsData) {
        mEventsData = new ArrayList<ADEventsModel>();
        this.mEventsData.addAll(eventsData);
        notifyDataSetChanged();
    }

    public ADEventsAdapter(Context context, List<ADEventsModel> messageList) {
        mEventsData = messageList;
        mScreenWidth = (int) (((ADBaseActivity) context).getScreenDetails(false) * 0.5);
    }

    @Override
    public int getItemCount() {
        return mEventsData.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        return position;

    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item_layout, parent, false);

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = (int) (mScreenWidth);
        view.setLayoutParams(layoutParams);
        return new EventHolder(view);

    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ADEventsModel dataItem = (ADEventsModel) mEventsData.get(position);


        ((EventHolder) holder).bind(dataItem);

    }

    private class EventHolder extends RecyclerView.ViewHolder {
        TextView date, name, description;

        EventHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date);
            name = (TextView) itemView.findViewById(R.id.event_name);
            description = (TextView) itemView.findViewById(R.id.event_desc);
        }

        void bind(ADEventsModel data) {
            date.setText(data.getDate());
            name.setText(data.getEventName());
            description.setText(data.getDescription());
        }
    }
}
