package android.adwait.com.admin.adapter;

import android.adwait.com.R;
import android.adwait.com.admin.model.ADAddChildModel;
import android.adwait.com.admin.view.ADAddChildActivity;
import android.adwait.com.admin.view.ADAdminActivity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

public class ADChildListAdapter extends RecyclerView.Adapter<ADChildListAdapter.MyViewHolder> {
    private Context context;
    private List<ADAddChildModel> cartListData;


    public ADChildListAdapter(Context context, List<ADAddChildModel> cartList) {
        this.context = context;
        this.cartListData = cartList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.child_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ADAddChildModel item = cartListData.get(position);
        holder.childName.setText("Name : " + item.getChildName());
        holder.ngoName.setText("NGO : " + item.getNGOName());
        holder.monthlyNeed.setText("Monthly(₹) : " + item.getAmountNeeded());
        if (item.getChildImage()!=null && !item.getChildImage().equals("")){
            Glide.with(context).load(item.getChildImage()).error(R.drawable.image_not_found).into(holder.childImage);
        }

        holder.viewForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent addChild = new Intent(context, ADAddChildActivity.class);
                addChild.putExtra("childData",item);
                ((ADAdminActivity)context).startActivityForResult(addChild,1235);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (cartListData != null) {
            return cartListData.size();
        } else {
            return 0;
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout viewBackground, viewForeground;
        public TextView childName, ngoName, monthlyNeed;
        private ImageView childImage;

        public MyViewHolder(View view) {
            super(view);
            childName = view.findViewById(R.id.child_name);
            childImage = view.findViewById(R.id.thumbnail);
            ngoName = view.findViewById(R.id.ngo_name);
            monthlyNeed = view.findViewById(R.id.monthly_need);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
        }
    }

    public void removeItem(int position) {
        cartListData.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(ADAddChildModel item, int position) {
        cartListData.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }
}