package jp.relo.cluboff.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;


import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import jp.relo.cluboff.adapter.MaterialSpinnerAdapter;
import jp.relo.cluboff.adapter.MaterialSpinnerBaseAdapter;
import jp.relo.cluboff.util.Utils;

/**
 * Created by tonkhanh on 7/11/17.
 */

public class MyMaterialSpinner extends android.support.v7.widget.AppCompatTextView{
    private MyMaterialSpinner.OnNothingSelectedListener onNothingSelectedListener;
    private MyMaterialSpinner.OnItemSelectedListener onItemSelectedListener;
    private MaterialSpinnerBaseAdapter adapter;
    private PopupWindow popupWindow;
    private ListView listView;
    private Drawable arrowDrawable;
    private boolean hideArrow;
    private boolean nothingSelected;
    private int popupWindowMaxHeight;
    private int popupWindowHeight;
    private int selectedIndex;
    private int backgroundColor;
    private int arrowColor;
    private int arrowColorDisabled;
    private int textColor;
    private int numberOfItems;

    public MyMaterialSpinner(Context context) {
        super(context);
        init(context, null);
    }

    public MyMaterialSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MyMaterialSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(final Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, com.jaredrummler.materialspinner.R.styleable.MaterialSpinner);
        int defaultColor = getTextColors().getDefaultColor();
        boolean rtl = false;

        try {
            backgroundColor = ta.getColor(com.jaredrummler.materialspinner.R.styleable.MaterialSpinner_ms_background_color, Color.WHITE);
            textColor = ta.getColor(com.jaredrummler.materialspinner.R.styleable.MaterialSpinner_ms_text_color, defaultColor);
            arrowColor = ta.getColor(com.jaredrummler.materialspinner.R.styleable.MaterialSpinner_ms_arrow_tint, textColor);
            hideArrow = ta.getBoolean(com.jaredrummler.materialspinner.R.styleable.MaterialSpinner_ms_hide_arrow, false);
            popupWindowMaxHeight = ta.getDimensionPixelSize(com.jaredrummler.materialspinner.R.styleable.MaterialSpinner_ms_dropdown_max_height, 0);
            popupWindowHeight = ta.getLayoutDimension(com.jaredrummler.materialspinner.R.styleable.MaterialSpinner_ms_dropdown_height,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            arrowColorDisabled = Utils.lighter(arrowColor, 0.8f);
        } finally {
            ta.recycle();
        }

        Resources resources = getResources();
        int left, right, bottom, top;
        left = right = bottom = top = resources.getDimensionPixelSize(com.jaredrummler.materialspinner.R.dimen.ms__padding_top);
        if (rtl) {
            right = resources.getDimensionPixelSize(com.jaredrummler.materialspinner.R.dimen.ms__padding_left);
        } else {
            left = resources.getDimensionPixelSize(com.jaredrummler.materialspinner.R.dimen.ms__padding_left);
        }

        setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        setClickable(true);
        setPadding(left, top, right, bottom);
        setBackgroundResource(com.jaredrummler.materialspinner.R.drawable.ms__selector);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && rtl) {
            setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            setTextDirection(View.TEXT_DIRECTION_RTL);
        }

        if (!hideArrow) {
            arrowDrawable = Utils.getDrawable(context, com.jaredrummler.materialspinner.R.drawable.ms__arrow).mutate();
            arrowDrawable.setColorFilter(arrowColor, PorterDuff.Mode.SRC_IN);
            if (rtl) {
                setCompoundDrawablesWithIntrinsicBounds(arrowDrawable, null, null, null);
            } else {
                setCompoundDrawablesWithIntrinsicBounds(null, null, arrowDrawable, null);
            }
        }

        listView = new ListView(context);
        listView.setId(getId());
        listView.setDivider(null);
        listView.setItemsCanFocus(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*if (position >= selectedIndex && position < adapter.getCount()) {
                    position++;
                }*/
                selectedIndex = position;
                nothingSelected = false;
                Object item = adapter.get(position);
                adapter.notifyItemSelected(position);
                //setText(item.toString());
                collapse();
                if (onItemSelectedListener != null) {
                    //noinspection unchecked
                    onItemSelectedListener.onItemSelected(MyMaterialSpinner.this, position, id, item);
                }
            }
        });

        popupWindow = new PopupWindow(context);
        popupWindow.setContentView(listView);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setElevation(16);
            popupWindow.setBackgroundDrawable(Utils.getDrawable(context, com.jaredrummler.materialspinner.R.drawable.ms__drawable));
        } else {
            popupWindow.setBackgroundDrawable(Utils.getDrawable(context, com.jaredrummler.materialspinner.R.drawable.ms__drop_down_shadow));
        }

        if (backgroundColor != Color.WHITE) { // default color is white
            setBackgroundColor(backgroundColor);
        }
        if (textColor != defaultColor) {
            setTextColor(textColor);
        }

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override public void onDismiss() {
                if (nothingSelected && onNothingSelectedListener != null) {
                    onNothingSelectedListener.onNothingSelected(MyMaterialSpinner.this);
                }
                if (!hideArrow) {
                    animateArrow(false);
                }
            }
        });
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        popupWindow.setWidth(MeasureSpec.getSize(widthMeasureSpec));
        popupWindow.setHeight(calculatePopupWindowHeight());
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (isEnabled() && isClickable()) {
                if (!popupWindow.isShowing()) {
                    expand();
                } else {
                    collapse();
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @Override public void setBackgroundColor(int color) {
        backgroundColor = color;
        Drawable background = getBackground();
        if (background instanceof StateListDrawable) { // pre-L
            try {
                Method getStateDrawable = StateListDrawable.class.getDeclaredMethod("getStateDrawable", int.class);
                if (!getStateDrawable.isAccessible()) getStateDrawable.setAccessible(true);
                int[] colors = {Utils.darker(color, 0.85f), color};
                for (int i = 0; i < colors.length; i++) {
                    ColorDrawable drawable = (ColorDrawable) getStateDrawable.invoke(background, i);
                    drawable.setColor(colors[i]);
                }
            } catch (Exception e) {
                Log.e("MaterialSpinner", "Error setting background color", e);
            }
        } else if (background != null) { // 21+ (RippleDrawable)
            background.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
        popupWindow.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    @Override public void setTextColor(int color) {
        textColor = color;
        super.setTextColor(color);
    }

    @Override public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("state", super.onSaveInstanceState());
        bundle.putInt("selected_index", selectedIndex);
        if (popupWindow != null) {
            bundle.putBoolean("is_popup_showing", popupWindow.isShowing());
            collapse();
        } else {
            bundle.putBoolean("is_popup_showing", false);
        }
        return bundle;
    }

    @Override public void onRestoreInstanceState(Parcelable savedState) {
        if (savedState instanceof Bundle) {
            Bundle bundle = (Bundle) savedState;
            selectedIndex = bundle.getInt("selected_index");
            if (adapter != null) {
                setText(adapter.get(selectedIndex).toString());
                adapter.notifyItemSelected(selectedIndex);
            }
            if (bundle.getBoolean("is_popup_showing")) {
                if (popupWindow != null) {
                    // Post the show request into the looper to avoid bad token exception
                    post(new Runnable() {

                        @Override public void run() {
                            expand();
                        }
                    });
                }
            }
            savedState = bundle.getParcelable("state");
        }
        super.onRestoreInstanceState(savedState);
    }

    @Override public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (arrowDrawable != null) {
            arrowDrawable.setColorFilter(enabled ? arrowColor : arrowColorDisabled, PorterDuff.Mode.SRC_IN);
        }
    }

    /**
     * @return the selected item position
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }

    /**
     * Set the default spinner item using its index
     *
     * @param position
     *     the item's position
     */
    public void setSelectedIndex(int position) {
        if (adapter != null) {
            if (position >= 0 && position <= adapter.getCount()) {
                adapter.notifyItemSelected(position);
                selectedIndex = position;
                //setText(adapter.get(position).toString());
            } else {
                throw new IllegalArgumentException("Position must be lower than adapter count!");
            }
        }
    }

    /**
     * Register a callback to be invoked when an item in the dropdown is selected.
     *
     * @param onItemSelectedListener
     *     The callback that will run
     */
    public void setOnItemSelectedListener(@Nullable MyMaterialSpinner.OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    /**
     * Register a callback to be invoked when the {@link PopupWindow} is shown but the user didn't select an item.
     *
     * @param onNothingSelectedListener
     *     the callback that will run
     */
    public void setOnNothingSelectedListener(@Nullable MyMaterialSpinner.OnNothingSelectedListener onNothingSelectedListener) {
        this.onNothingSelectedListener = onNothingSelectedListener;
    }

    /**
     * Set the dropdown items
     *
     * @param items
     *     A list of items
     * @param <T>
     *     The item type
     */
    public <T> void setItems(@NonNull List<T> items) {
        numberOfItems = items.size();
        adapter = new MaterialSpinnerAdapter<>(getContext(), items).setTextColor(textColor);
        setAdapterInternal(adapter);
    }

    /**
     * Set the dropdown items
     *
     * @param items
     *     A list of items
     * @param <T>
     *     The item type
     */
    public <T> void setItems(@NonNull T... items) {
        setItems(Arrays.asList(items));
    }

    /**
     * Get the list of items in the adapter
     *
     * @param <T>
     *     The item type
     * @return A list of items or {@code null} if no items are set.
     */
    public <T> List<T> getItems() {
        if (adapter == null) {
            return null;
        }
        //noinspection unchecked
        return adapter.getItems();
    }

    /**
     * Set a custom adapter for the dropdown items
     *
     * @param adapter
     *     The list adapter
     */
    public void setAdapter(@NonNull ListAdapter adapter) {
        //this.adapter = new MaterialSpinnerAdapterWrapper(getContext(), adapter);
        setAdapterInternal(this.adapter);
    }

    public <T> void setAdapter(MaterialSpinnerAdapter<T> adapter) {
        this.adapter = adapter;
        setAdapterInternal(adapter);
    }

    private void setAdapterInternal(@NonNull MaterialSpinnerBaseAdapter adapter) {
        listView.setAdapter(adapter);
        if (selectedIndex >= numberOfItems) {
            selectedIndex = 0;
        }
        //setText(adapter.get(selectedIndex).toString());
    }

    /**
     * Show the dropdown menu
     */
    public void expand() {
        if (!hideArrow) {
            animateArrow(true);
        }
        nothingSelected = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popupWindow.setOverlapAnchor(false);
            popupWindow.showAsDropDown(this);
        } else {
            int[] location = new int[2];
            getLocationOnScreen(location);
            int x = location[0];
            int y = getHeight() + location[1];
            popupWindow.showAtLocation(this, Gravity.TOP | Gravity.START, x, y);
        }
    }

    /**
     * Closes the dropdown menu
     */
    public void collapse() {
        if (!hideArrow) {
            animateArrow(false);
        }
        popupWindow.dismiss();
    }

    /**
     * Set the tint color for the dropdown arrow
     *
     * @param color
     *     the color value
     */
    public void setArrowColor(@ColorInt int color) {
        arrowColor = color;
        arrowColorDisabled = Utils.lighter(arrowColor, 0.8f);
        if (arrowDrawable != null) {
            arrowDrawable.setColorFilter(arrowColor, PorterDuff.Mode.SRC_IN);
        }
    }

    private void animateArrow(boolean shouldRotateUp) {
        int start = shouldRotateUp ? 0 : 10000;
        int end = shouldRotateUp ? 10000 : 0;
        ObjectAnimator animator = ObjectAnimator.ofInt(arrowDrawable, "level", start, end);
        animator.start();
    }

    /**
     * Set the maximum height of the dropdown menu.
     *
     * @param height
     *     the height in pixels
     */
    public void setDropdownMaxHeight(int height) {
        popupWindowMaxHeight = height;
        popupWindow.setHeight(calculatePopupWindowHeight());
    }

    /**
     * Set the height of the dropdown menu
     *
     * @param height
     *     the height in pixels
     */
    public void setDropdownHeight(int height) {
        popupWindowHeight = height;
        popupWindow.setHeight(calculatePopupWindowHeight());
    }

    private int calculatePopupWindowHeight() {
        if (adapter == null) {
            return WindowManager.LayoutParams.WRAP_CONTENT;
        }
        float listViewHeight = adapter.getCount() * getResources().getDimension(com.jaredrummler.materialspinner.R.dimen.ms__item_height);
        if (popupWindowMaxHeight > 0 && listViewHeight > popupWindowMaxHeight) {
            return popupWindowMaxHeight;
        } else if (popupWindowHeight != WindowManager.LayoutParams.MATCH_PARENT
                && popupWindowHeight != WindowManager.LayoutParams.WRAP_CONTENT
                && popupWindowHeight <= listViewHeight) {
            return popupWindowHeight;
        }
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    /**
     * Get the {@link PopupWindow}.
     *
     * @return The {@link PopupWindow} that is displayed when the view has been clicked.
     */
    public PopupWindow getPopupWindow() {
        return popupWindow;
    }

    /**
     * Interface definition for a callback to be invoked when an item in this view has been selected.
     *
     * @param <T>
     *     Adapter item type
     */
    public interface OnItemSelectedListener<T> {
        void onItemSelected(MyMaterialSpinner view, int position, long id, T item);

    }

    /**
     * Interface definition for a callback to be invoked when the dropdown is dismissed and no item was selected.
     */
    public interface OnNothingSelectedListener {

        /**
         * Callback method to be invoked when the {@link PopupWindow} is dismissed and no item was selected.
         *
         * @param spinner
         *     the {@link MyMaterialSpinner}
         */
        void onNothingSelected(MyMaterialSpinner spinner);
    }

}