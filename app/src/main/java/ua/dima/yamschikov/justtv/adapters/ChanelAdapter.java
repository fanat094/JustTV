package ua.dima.yamschikov.justtv.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ua.dima.yamschikov.justtv.R;
import ua.dima.yamschikov.justtv.constructors.Chanel;

/**
 * Created by user on 05.02.2017.
 */

public class ChanelAdapter extends RecyclerView.Adapter<ChanelAdapter.MyViewHolder> {

    private List<Chanel> chanelsList;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView title;
        public ImageView pic;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            pic = (ImageView) view.findViewById(R.id.pic);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d("CLICK!","CLICK");
            Toast.makeText(view.getContext(), "Clicked Country Position = " + getPosition(), Toast.LENGTH_SHORT).show();
        }
    }


    public ChanelAdapter(List<Chanel> chanelsList) {
        this.chanelsList = chanelsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chanel_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Chanel chanel = chanelsList.get(position);
        holder.title.setText(chanel.getTitle());
        holder.pic.setImageResource(chanel.getPic());
    }

    @Override
    public int getItemCount() {
        return chanelsList.size();
    }
}