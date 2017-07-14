package uk.co.walesbirds.birdwatching;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by NylePudding on 17-Jun-17.
 */

class CustomAdapter extends BaseAdapter implements Filterable {

    private static final int TYPE_GENERAL = 0;
    private static final int TYPE_SEPARATOR = 1;
    private static final int TYPE_RARE = 2;
    private static final int TYPE_WINTER = 3;
    private static final int TYPE_SUMMER = 4;

    private ArrayList<String> mData = new ArrayList<String>();
    private ArrayList<String> mKey = new ArrayList<>();
    private List<String>filteredData = new ArrayList<String>();
    private TreeSet<Integer> sectionHeader = new TreeSet<>();
    private TreeSet<Integer> generalItem = new TreeSet<>();

    private LayoutInflater mInflater;
    private ItemFilter mFilter = new ItemFilter();

    public CustomAdapter(Context context) {
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addGeneral(final BirdEntry bEn) {

        if (Globals.english) {
            mData.add(bEn.getEnglish() + "\n" + bEn.getWelsh());
        } else {
            mData.add(bEn.getWelsh() + "\n" + bEn.getEnglish());
        }

        String key = getKey(bEn);
        mKey.add(key);

        //filteredData.add(item);
        generalItem.add(mData.size() - 1);
        notifyDataSetChanged();
    }

    public String getKey(BirdEntry bEn) {
        String key = "GENERAL";
        if (bEn.getRare().equals("R")){
            key = "RARE";
        }
        if (bEn.getVisitor().equals("WINTER")){
            key = "WINTER";
        }
        if (bEn.getVisitor().equals("SUMMER")){
            key = "SUMMER";
        }

        return key;
    }

    public void addSectionHeaderItem(final String item) {
        mData.add(item);
        //filteredData.add(item);
        sectionHeader.add(mData.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {

        if (sectionHeader.contains(position)){
            return TYPE_SEPARATOR;
        }
        else {
            return TYPE_GENERAL;
        }

    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public int getPosition(int position) {
        //return position - sectionHeader.headSet(position).size();
        return position;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position);

    }



    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int rowType = getItemViewType(position);

        if (convertView == null) {
            holder = new ViewHolder();
            switch (rowType) {
                case TYPE_GENERAL:
                    convertView = mInflater.inflate(R.layout.bird_general, null);
                    holder.textView = (TextView) convertView.findViewById(R.id.general);
                    break;
                case TYPE_SEPARATOR:
                    convertView = mInflater.inflate(R.layout.separator, null);
                    holder.textView = (TextView) convertView.findViewById(R.id.textSeparator);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView.setText(mData.get(position));

        //holder.textView.setText(filteredData.get(position));

        return convertView;
    }

    /**
     * Method for finding the BirdEntry using the english as the primary key
     * @param english
     * @return BirdEntry The found BirdEntry, null if not found
     */
    private BirdEntry findBird(String english){

        BirdEntry birdEntry = null;

        for (BirdEntry bEn : Globals.Birds.BirdEntries){
            if (bEn.getEnglish().equals(english)){
                birdEntry = bEn;
                System.out.println("FOUND");
            }
        }
        return birdEntry;

    }

    /**
     * Method for getting the english from the selected BirdEntry String
     * @return String The English name of the bird
     */
    private String getEnglish(String bird){
        String selected[] = bird.split("\n", -1);

        String english = "";

        if (Globals.english){
            english = selected[0];
        } else {
            english = selected[1];
        }

        return english;

    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public static class ViewHolder {
        public TextView textView;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<String> list = mData;

            int count = list.size();
            final ArrayList<String> nlist = new ArrayList<String>(count);

            String filterableString ;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i);
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(filterableString);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<String>) results.values;
            notifyDataSetChanged();
        }

    }

}