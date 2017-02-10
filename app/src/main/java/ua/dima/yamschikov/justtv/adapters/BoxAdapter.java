package ua.dima.yamschikov.justtv.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ua.dima.yamschikov.justtv.R;
import ua.dima.yamschikov.justtv.constructors.Chanel;

/**
 * Created by user on 08.02.2017.
 */

public class BoxAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Chanel> objects;

    public BoxAdapter(Context context, ArrayList<Chanel> products) {
        ctx = context;
        objects = products;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }
    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.chanel_list_row, parent, false);
        }

        Chanel p = getProduct(position);

        // заполняем View в пункте списка данными из товаров: наименование, цена
        // и картинка
        ((TextView) view.findViewById(R.id.title)).setText(p.getTitle());
        ((ImageView) view.findViewById(R.id.pic)).setImageResource(p.getPic());

        return view;
    }

    // товар по позиции
    Chanel getProduct(int position) {
        return ((Chanel) getItem(position));
    }
}
