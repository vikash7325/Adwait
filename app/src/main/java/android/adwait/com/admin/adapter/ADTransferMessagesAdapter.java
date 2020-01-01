package android.adwait.com.admin.adapter;

import android.adwait.com.R;
import android.adwait.com.admin.model.ADAddChildModel;
import android.adwait.com.admin.model.ADTransferData;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ADTransferMessagesAdapter extends RecyclerView.Adapter<ADTransferMessagesAdapter.MyViewHolder> {

    private HashMap<String, ADAddChildModel> mChildListData;
    private HashMap<String, ADTransferData> messageData;
    private List<String> messageKey;

    public ADTransferMessagesAdapter(HashMap<String, ADAddChildModel> childModelHashMap, HashMap<String, ADTransferData> wishList) {
        this.messageData = wishList;
        this.mChildListData = childModelHashMap;
        messageKey = new ArrayList<>(messageData.keySet());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ADTransferData item = messageData.get(messageKey.get(position));
        final ADAddChildModel child = mChildListData.get(messageKey.get(position));
        holder.childName.setText(child.getChildName());
        holder.ngoName.setText(child.getNGOName());
        holder.message.setText(item.getMessage());
    }

    @Override
    public int getItemCount() {
        if (messageData != null) {
            return messageData.size();
        } else {
            return 0;
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout viewForeground;
        public TextView childName, ngoName, message;

        public MyViewHolder(View view) {
            super(view);
            childName = view.findViewById(R.id.child_name);
            ngoName = view.findViewById(R.id.ngo_name);
            message = view.findViewById(R.id.message);
            viewForeground = view.findViewById(R.id.view_foreground);
        }
    }
}