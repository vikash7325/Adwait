package android.adwait.com.subscription.adapter;

import android.adwait.com.R;
import android.adwait.com.subscription.model.ADStoredSubData;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ADCompletedSubAdapter extends RecyclerView.Adapter<ADCompletedSubAdapter.SubscriptionHolder> {
    private List<ADStoredSubData> mCompletedSubData;
    private Context mContext;

    public ADCompletedSubAdapter(Context context, List<ADStoredSubData> messageList) {
        mCompletedSubData = messageList;
        mContext = context;
    }

    public void setCompletedSubData(List<ADStoredSubData> eventsData) {
        mCompletedSubData = new ArrayList<ADStoredSubData>();
        this.mCompletedSubData.addAll(eventsData);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mCompletedSubData == null) {
            return 10;
        }
        return mCompletedSubData.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        return position;

    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public SubscriptionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.completed_sub_items, parent, false);
        SubscriptionHolder holder = new SubscriptionHolder(view);
        return holder;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(SubscriptionHolder holder, int position) {
//        ADStoredSubData dataItem = (ADStoredSubData) mCompletedSubData.get(position);
//        ((SubscriptionHolder) holder).bind(dataItem);
    }

    public class SubscriptionHolder extends RecyclerView.ViewHolder {
        TextView amount, lastContribution, cancelSub;

        SubscriptionHolder(View itemView) {
            super(itemView);
            amount = (TextView) itemView.findViewById(R.id.amount);
            lastContribution = (TextView) itemView.findViewById(R.id.last_contribution);
            cancelSub = (TextView) itemView.findViewById(R.id.cancel);
        }

        void bind(ADStoredSubData data) {
            String text = String.format(mContext.getString(R.string.last_contribution), "");
            lastContribution.setText(text);
            text = mContext.getString(R.string.rupees) + data.getAmount();
            amount.setText(text);

            cancelSub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
