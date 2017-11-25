package net.slc.bantuin.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.slc.bantuin.Model.Category;
import net.slc.bantuin.R;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{
    private Context context;
    private ArrayList<Category> categories;
    public RecyclerAdapter(ArrayList<Category> categories, Context context){
        this.categories = categories;
        this.context = context;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView categoryName;
        private ImageView categoryPicture;

        public ViewHolder(View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
            categoryPicture = itemView.findViewById(R.id.categoryPicture);
            //set onclick
//            itemView.setOnClickListener(new View.OnClickListener() {
//            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.card_layout, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.categoryName.setText(categories.get(position).getName());
        Picasso.with(context)
                .load(categories.get(position).getIcon())
                .resize(100, 100)
                .into(holder.categoryPicture);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
