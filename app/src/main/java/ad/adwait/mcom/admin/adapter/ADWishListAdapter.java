package ad.adwait.mcom.admin.adapter;

import ad.adwait.mcom.R;
import ad.adwait.mcom.admin.view.ADAdminActivity;
import ad.adwait.mcom.wish_corner.model.ADWishModel;
import ad.adwait.mcom.wish_corner.view.ADAddWishesActivity;
import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import ad.adwait.mcom.wish_corner.model.ADWishModel;
import ad.adwait.mcom.wish_corner.view.ADAddWishesActivity;

public class ADWishListAdapter extends RecyclerView.Adapter<ADWishListAdapter.MyViewHolder> {
    private Context context;
    private List<ADWishModel> wishListData;

    public ADWishListAdapter(Context context, List<ADWishModel> wishList) {
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
        final ADWishModel item = wishListData.get(position);
        holder.childName.setText("Name : " + item.getName());
        holder.wishItem.setText("Wish/Item : " + item.getWishItem());
        holder.itemPrice.setText("Price(₹) : " + item.getPrice());

        holder.viewForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent wishes =  new Intent(context, ADAddWishesActivity.class);
                wishes.putExtra("wishData",item);
                ((ADAdminActivity)context).startActivityForResult(wishes,1235);

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

    public void restoreItem(ADWishModel item, int position) {
        wishListData.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }
}