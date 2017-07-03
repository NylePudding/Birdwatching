package uk.co.walesbirds.birdwatching;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListBirdsActivity extends AppCompatActivity {

    private ListView lstBirds;
    private TextView txtUkList;
    private TextView txtAll;
    private TextView txtKey;
    private ArrayAdapter<String> adapter;
    private CustomAdapter mAdapter;
    private EditText inputSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_birds);

        txtUkList = (TextView) findViewById(R.id.txtUkList);
        txtAll = (TextView) findViewById(R.id.txtAll);
        txtKey = (TextView) findViewById(R.id.txtKey);
        lstBirds = (ListView) findViewById(R.id.lstBirds);
        inputSearch = (EditText) findViewById(R.id.txtSearch);

        populateBirdList();

        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                ListBirdsActivity.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });





        txtUkList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){
            }
        });

        txtAll.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){
            }
        });

        txtKey.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){
            }
        });



    }

    private String[] getBirdList(){
        String[] birdNames = new String[Globals.Birds.BirdEntries.size()];

        for (int i = 0; i < birdNames.length; i++){

            birdNames[i] = Globals.Birds.BirdEntries.get(i).getEnglish();
                    //+ "/n" + Globals.Birds.BirdEntries.get(i).getWelsh();

        }
        return birdNames;
    }


    private void populateBirdListCustomAdapter() {

        List<String> birdNames = new ArrayList<String>();

        mAdapter = new CustomAdapter(this);

        String lastCategory = "";

        for (BirdEntry bEn : Globals.Birds.BirdEntries) {
            if (!lastCategory.equals(bEn.getCategory())){
                mAdapter.addSectionHeaderItem(bEn.getCategory());
            }
            lastCategory = bEn.getCategory();
            birdNames.add(bEn.getEnglish() + "\n" + bEn.getWelsh());
            mAdapter.addItem(bEn.getEnglish() + "\n" + bEn.getWelsh());
        }

        lstBirds.setAdapter(mAdapter);

        lstBirds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Globals.selectedBird = mAdapter.getItem(mAdapter.getPosition(position));
                if (mAdapter.getItemViewType(position) == 0) {
                    startActivity(new Intent(ListBirdsActivity.this, ViewBirdActivity.class));
                }
            }
        });


    }

    private void populateBirdList(){

        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.bird_name, getBirdList());
        lstBirds.setAdapter(adapter);

        lstBirds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Globals.selectedBird = adapter.getItem(position);
                startActivity(new Intent(ListBirdsActivity.this, ViewBirdActivity.class));
            }
        });

    }

}
