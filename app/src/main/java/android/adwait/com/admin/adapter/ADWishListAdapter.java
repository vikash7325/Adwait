package android.adwait.com.admin.adapter;

import android.adwait.com.R;
import android.adwait.com.admin.model.ADAddChildModel;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class ADWishListAdapter extends RecyclerView.Adapter<ADWishListAdapter.MyViewHolder> {
    private Context context;
    private List<ADAddChildModel> wishListData;

    public ADWishListAdapter(Context context, List<ADAddChildModel> wishList) {
        this.context = context;
        this.wishListData = wishList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wish_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ADAddChildModel item = wishListData.get(position);
        holder.childName.setText("Name : " + (position + 1));
        holder.wishItem.setText("Wish/Item : ");
        holder.itemPrice.setText("Price(₹) : ");

        holder.viewForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        if (wishListData != null) {
            return wishListData.size();
        } else {
            return 0;
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout viewBackground, viewForeground;
        public TextView childName, wishItem, itemPrice;

        public MyViewHolder(View view) {
            super(view);
            childName = view.findViewById(R.id.child_name);
            wishItem = view.findViewById(R.id.wish_item);
            itemPrice = view.findViewById(R.id.price);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
        }
    }

    public void removeItem(int position) {
        wishListData.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(ADAddChildModel item, int position) {
        wishListData.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }
}