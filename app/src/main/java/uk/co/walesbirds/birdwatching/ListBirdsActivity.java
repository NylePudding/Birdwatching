package uk.co.walesbirds.birdwatching;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class ListBirdsActivity extends AppCompatActivity {

    private ListView lstBirds;
    private TextView txtUkList;
    private TextView txtAll;
    private TextView txtKey;
    private ArrayAdapter<String> adapter;
    private EditText inputSearch;
    private boolean ukList = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_birds);

        txtUkList = (TextView) findViewById(R.id.txtUkList);
        txtKey = (TextView) findViewById(R.id.txtKey);
        lstBirds = (ListView) findViewById(R.id.lstBirds);
        inputSearch = (EditText) findViewById(R.id.txtSearch);


        populateBirdList();

        txtUkList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){

                ukList = (!ukList);
                if (ukList){
                    txtUkList.setText("Global list");
                } else {
                    txtUkList.setText("UK list");
                }
                populateBirdList();
            }
        });

        txtKey.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){
                displayKey();
            }
        });

    }

    /**
     * Method that returns a String array of the name of all Bird Entires
     * @return String[] List of Bird names, English and Welsh
     */
    private String[] getBirdList(){
        String[] birdNames = new String[Globals.Birds.BirdEntries.size()];

        for (int i = 0; i < birdNames.length; i++){

            birdNames[i] = Globals.Birds.BirdEntries.get(i).getEnglish();

        }
        return birdNames;
    }

    private void displayKey(){
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage("Yellow - Summer visitors to UK\nBlue - Winder visitors to UK\nRed - Rare visitors or bird that has escaped from a collection");
        dlgAlert.setTitle("Bird Key");
        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss the dialog
                    }
                });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    public String getKey(BirdEntry bEn) {
        String key = "GENERAL";
        if (bEn.getRare().toUpperCase().equals("R")){
            key = "RARE";
        }
        if (bEn.getVisitor().toUpperCase().equals("WINTER")){
            key = "WINTER";
        }
        if (bEn.getVisitor().toUpperCase().equals("SUMMER")){
            key = "SUMMER";
        }

        return key;
    }

    /**
     * Populates the adapter with a list of all the birds
     */
    private void populateBirdList(){

        ArrayList<Item> birdList = new ArrayList<Item>();

        String lastCategory = "";

        for (BirdEntry bEn : Globals.Birds.BirdEntries) {

            if (ukList) {
                if (bEn.getLocation().equals("UK")) {
                    if (!lastCategory.equals(bEn.getSubcategory())) {
                        birdList.add(new SectionItem(bEn.getCategory() + " / " + bEn.getSubcategory()));
                    }
                    lastCategory = bEn.getSubcategory();
                    if (Globals.english) {

                        String key = getKey(bEn);

                        switch (key){
                            case "GENERAL":
                                birdList.add(new EntryItem(bEn.getEnglish() + "\n" + bEn.getWelsh()));
                                break;
                            case "RARE":
                                birdList.add(new RareItem(bEn.getEnglish() + "\n" + bEn.getWelsh()));
                                break;
                            case "SUMMER":
                                birdList.add(new SummerItem(bEn.getEnglish() + "\n" + bEn.getWelsh()));
                                break;
                            case "WINTER":
                                birdList.add(new WinterItem(bEn.getEnglish() + "\n" + bEn.getWelsh()));
                                break;
                        }
                    } else {
                        birdList.add(new EntryItem(bEn.getWelsh() + "\n" + bEn.getEnglish()));
                    }
                }
            }
            else {
                if (!lastCategory.equals(bEn.getSubcategory())) {
                    birdList.add(new SectionItem(bEn.getCategory() + " / " + bEn.getSubcategory()));
                }
                lastCategory = bEn.getSubcategory();
                if (Globals.english) {

                    String key = getKey(bEn);

                    switch (key){
                        case "GENERAL":
                            birdList.add(new EntryItem(bEn.getEnglish() + "\n" + bEn.getWelsh()));
                            break;
                        case "RARE":
                            birdList.add(new RareItem(bEn.getEnglish() + "\n" + bEn.getWelsh()));
                            break;
                        case "SUMMER":
                            birdList.add(new SummerItem(bEn.getEnglish() + "\n" + bEn.getWelsh()));
                            break;
                        case "WINTER":
                            birdList.add(new WinterItem(bEn.getEnglish() + "\n" + bEn.getWelsh()));
                            break;
                    }
                } else {
                    birdList.add(new EntryItem(bEn.getWelsh() + "\n" + bEn.getEnglish()));
                }
            }
        }

        // set adapter
        final CountryAdapter adapter = new CountryAdapter(this, birdList);
        lstBirds.setAdapter(adapter);
        lstBirds.setTextFilterEnabled(true);

        // filter on text change
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(adapter != null)
                {
                    adapter.getFilter().filter(s.toString());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        lstBirds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {

                Item selectedItem = adapter.getItem(position);

                if (selectedItem.getItemType() != 'H') {
                    Globals.selectedBird = selectedItem.getTitle();
                    startActivity(new Intent(ListBirdsActivity.this, ViewBirdActivity.class));
                }
            }
        });



    }

    /**
     * row item
     */
    public interface Item {
        public char getItemType();
        public String getTitle();
    }

    /**
     * Section Item
     */
    public class SectionItem implements Item {
        private final String title;

        public SectionItem(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        @Override
        public char getItemType() {
            return 'H';
        }
    }

    /**
     * Entry Item
     */
    public class EntryItem implements Item {
        public final String title;

        public EntryItem(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        @Override
        public char getItemType() {
            return 'G';
        }
    }

    public class RareItem implements Item {
        public final String title;

        public RareItem(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        @Override
        public char getItemType() {
            return 'R';
        }
    }

    public class SummerItem implements Item {
        public final String title;

        public SummerItem(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        @Override
        public char getItemType() {
            return 'S';
        }
    }

    public class WinterItem implements Item {
        public final String title;

        public WinterItem(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        @Override
        public char getItemType() {
            return 'W';
        }
    }

    /**
     * Adapter
     */
    public class CountryAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<Item> item;
        private ArrayList<Item> originalItem;

        public CountryAdapter() {
            super();
        }

        public CountryAdapter(Context context, ArrayList<Item> item) {
            this.context = context;
            this.item = item;
            //this.originalItem = item;
        }

        @Override
        public int getCount() {
            return item.size();
        }

        @Override
        public Item getItem(int position) {
            return item.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (item.get(position).getItemType() == 'H') {
                // if section header
                convertView = inflater.inflate(R.layout.layout_section, parent, false);
                TextView tvSectionTitle = (TextView) convertView.findViewById(R.id.tvSectionTitle);
                tvSectionTitle.setText(((SectionItem) item.get(position)).getTitle());
            }
            else if (item.get(position).getItemType() == 'R') {
                // if section header
                convertView = inflater.inflate(R.layout.layout_rare, parent, false);
                TextView tvSectionTitle = (TextView) convertView.findViewById(R.id.tvRare);
                tvSectionTitle.setText(((RareItem) item.get(position)).getTitle());
                ImageView imgIcon = (ImageView) convertView.findViewById(R.id.imgIconBird);
                String title = ((RareItem) item.get(position)).getTitle();
                setIcon(imgIcon,title);
            }
            else if (item.get(position).getItemType() == 'S') {
                // if section header
                convertView = inflater.inflate(R.layout.layout_summer, parent, false);
                TextView tvSectionTitle = (TextView) convertView.findViewById(R.id.tvSummer);
                tvSectionTitle.setText(((SummerItem) item.get(position)).getTitle());
                ImageView imgIcon = (ImageView) convertView.findViewById(R.id.imgIconBird);
                String title = ((SummerItem) item.get(position)).getTitle();
                setIcon(imgIcon,title);
            }
            else if (item.get(position).getItemType() == 'W') {
                // if section header
                convertView = inflater.inflate(R.layout.layout_winter, parent, false);
                TextView tvSectionTitle = (TextView) convertView.findViewById(R.id.tvWinter);
                tvSectionTitle.setText(((WinterItem) item.get(position)).getTitle());
                ImageView imgIcon = (ImageView) convertView.findViewById(R.id.imgIconBird);
                String title = ((WinterItem) item.get(position)).getTitle();
                setIcon(imgIcon,title);
            }
            else
            {
                // if item
                convertView = inflater.inflate(R.layout.layout_bird, parent, false);
                TextView tvItemTitle = (TextView) convertView.findViewById(R.id.tvItemTitle);
                tvItemTitle.setText(((EntryItem) item.get(position)).getTitle());
                ImageView imgIcon = (ImageView) convertView.findViewById(R.id.imgIconBird);
                String title = ((EntryItem) item.get(position)).getTitle();
                setIcon(imgIcon,title);
            }

            return convertView;
        }

        private boolean isImageExistant(String fileName){
            File file = getBaseContext().getFileStreamPath(fileName);
            return file.exists();
        }

        private String getEnglish(String birdName){
            String selected[] = birdName.split("\n", -1);
            String english = "";

            if (Globals.english){
                english = selected[0];
            } else {
                english = selected[1];
            }

            return english;
        }

        private void setIcon(ImageView imgIcon, String title){

            String fileName = getEnglish(title).replace(" ","_") + ".jpg";

            if (isImageExistant(fileName)){
                File imgFile = getBaseContext().getFileStreamPath(fileName);
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getPath());
                imgIcon.setImageBitmap(myBitmap);
            }
        }

        /**
         * Filter
         */
        public Filter getFilter()
        {
            Filter filter = new Filter() {

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    item = (ArrayList<Item>) results.values;
                    notifyDataSetChanged();
                }

                @SuppressWarnings("null")
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    FilterResults results = new FilterResults();
                    ArrayList<Item> filteredArrayList = new ArrayList<Item>();


                    if(originalItem == null || originalItem.size() == 0)
                    {
                        originalItem = new ArrayList<Item>(item);
                    }

                    /*
                     * if constraint is null then return original value
                     * else return filtered value
                     */
                    if(constraint == null && constraint.length() == 0)
                    {
                        results.count = originalItem.size();
                        results.values = originalItem;
                    }
                    else
                    {
                        constraint = constraint.toString().toLowerCase(Locale.ENGLISH);
                        for (int i = 0; i < originalItem.size(); i++)
                        {
                            String title = originalItem.get(i).getTitle().toLowerCase(Locale.ENGLISH);
                            if(title.contains(constraint.toString()))
                            {
                                filteredArrayList.add(originalItem.get(i));
                            }
                        }
                        results.count = filteredArrayList.size();
                        results.values = filteredArrayList;
                    }

                    return results;
                }
            };

            return filter;
        }
    }

}
