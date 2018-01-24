package net.fukuri.memberapp.adapter;

import android.content.Context;

import java.util.List;

/**
 * Created by tonkhanh on 7/12/17.
 */

public class MaterialSpinnerAdapter <T> extends MaterialSpinnerBaseAdapter {

    private final List<T> items;

    public MaterialSpinnerAdapter(Context context, List<T> items) {
        super(context);
        this.items = items;
    }

    @Override public int getCount() {
        return items.size();
    }

    @Override public T getItem(int position) {
        return items.get(position);
    }

    @Override public T get(int position) {
        return items.get(position);
    }

    @Override public List<T> getItems() {
        return items;
    }

}