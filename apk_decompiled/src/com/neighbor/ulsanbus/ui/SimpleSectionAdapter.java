package com.neighbor.ulsanbus.ui;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class SimpleSectionAdapter<T> extends BaseAdapter {
    static final boolean DEBUG = false;
    static final String TAG = SimpleSectionAdapter.class.getSimpleName();
    private static final int VIEW_TYPE_SECTION_HEADER = 0;
    private final DataSetObserver dataSetObserver = new C02681();
    private Context mContext;
    private CursorAdapter mListAdapter;
    private int mSectionHeaderLayoutId;
    private int mSectionTitleTextViewId;
    private Sectionizer<T> mSectionizer;
    private LinkedHashMap<String, Integer> mSections;

    /* renamed from: com.neighbor.ulsanbus.ui.SimpleSectionAdapter$1 */
    class C02681 extends DataSetObserver {
        C02681() {
        }

        public void onChanged() {
            super.onChanged();
            SimpleSectionAdapter.this.findSections();
        }
    }

    static class SectionHolder {
        public TextView titleTextView;

        SectionHolder() {
        }
    }

    public SimpleSectionAdapter(Context context, CursorAdapter listAdapter, int sectionHeaderLayoutId, int sectionTitleTextViewId, Sectionizer<T> sectionizer) {
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null.");
        } else if (listAdapter == null) {
            throw new IllegalArgumentException("listAdapter cannot be null.");
        } else if (sectionizer == null) {
            throw new IllegalArgumentException("sectionizer cannot be null.");
        } else if (isTextView(context, sectionHeaderLayoutId, sectionTitleTextViewId)) {
            this.mContext = context;
            this.mListAdapter = listAdapter;
            this.mSectionHeaderLayoutId = sectionHeaderLayoutId;
            this.mSectionTitleTextViewId = sectionTitleTextViewId;
            this.mSectionizer = sectionizer;
            this.mSections = new LinkedHashMap();
            registerDataSetObserver(this.dataSetObserver);
            findSections();
        } else {
            throw new IllegalArgumentException("sectionTitleTextViewId should be a TextView.");
        }
    }

    private boolean isTextView(Context context, int layoutId, int textViewId) {
        return View.inflate(context, layoutId, null).findViewById(textViewId) instanceof TextView;
    }

    public int getCount() {
        return this.mListAdapter.getCount() + getSectionCount();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        SectionHolder sectionHolder = null;
        switch (getItemViewType(position)) {
            case 0:
                if (view != null) {
                    sectionHolder = (SectionHolder) view.getTag();
                    break;
                }
                view = View.inflate(this.mContext, this.mSectionHeaderLayoutId, null);
                sectionHolder = new SectionHolder();
                sectionHolder.titleTextView = (TextView) view.findViewById(this.mSectionTitleTextViewId);
                view.setTag(sectionHolder);
                break;
            default:
                view = this.mListAdapter.getView(getIndexForPosition(position), convertView, parent);
                break;
        }
        if (sectionHolder != null) {
            sectionHolder.titleTextView.setText(sectionTitleForPosition(position));
        }
        return view;
    }

    public boolean areAllItemsEnabled() {
        if (this.mListAdapter.areAllItemsEnabled() && this.mSections.size() == 0) {
            return true;
        }
        return false;
    }

    public int getItemViewType(int position) {
        int positionInCustomAdapter = getIndexForPosition(position);
        if (this.mSections.values().contains(Integer.valueOf(position))) {
            return 0;
        }
        return this.mListAdapter.getItemViewType(positionInCustomAdapter) + 1;
    }

    public int getViewTypeCount() {
        return this.mListAdapter.getViewTypeCount() + 1;
    }

    public boolean isEnabled(int position) {
        if (this.mSections.values().contains(Integer.valueOf(position))) {
            return false;
        }
        return this.mListAdapter.isEnabled(getIndexForPosition(position));
    }

    public Object getItem(int position) {
        return this.mListAdapter.getItem(getIndexForPosition(position));
    }

    public long getItemId(int position) {
        return this.mListAdapter.getItemId(getIndexForPosition(position));
    }

    public void notifyDataSetChanged() {
        this.mListAdapter.notifyDataSetChanged();
        findSections();
        super.notifyDataSetChanged();
    }

    public int getIndexForPosition(int position) {
        int nSections = 0;
        for (Entry<String, Integer> entry : this.mSections.entrySet()) {
            if (((Integer) entry.getValue()).intValue() < position) {
                nSections++;
            }
        }
        return position - nSections;
    }

    private void findSections() {
        int n = this.mListAdapter.getCount();
        int nSections = 0;
        this.mSections.clear();
        for (int i = 0; i < n; i++) {
            String sectionName = this.mSectionizer.getSectionTitleForItem(this.mListAdapter.getItem(i));
            if (!this.mSections.containsKey(sectionName)) {
                this.mSections.put(sectionName, Integer.valueOf(i + nSections));
                nSections++;
            }
        }
    }

    private int getSectionCount() {
        return this.mSections.size();
    }

    private String sectionTitleForPosition(int position) {
        for (Entry<String, Integer> entry : this.mSections.entrySet()) {
            if (((Integer) entry.getValue()).intValue() == position) {
                return (String) entry.getKey();
            }
        }
        return null;
    }

    public void registerDataSetObserver(DataSetObserver observer) {
        this.mListAdapter.registerDataSetObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        this.mListAdapter.unregisterDataSetObserver(observer);
    }
}
