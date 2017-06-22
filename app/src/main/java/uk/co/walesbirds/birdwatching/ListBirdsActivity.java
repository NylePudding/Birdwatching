package uk.co.walesbirds.birdwatching;

import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListBirdsActivity extends AppCompatActivity {

    private ListView lstBirds;
    private TextView txtUkList;
    private TextView txtAll;
    private TextView txtKey;
    private CustomAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_birds);

        txtUkList = (TextView) findViewById(R.id.txtUkList);
        txtAll = (TextView) findViewById(R.id.txtAll);
        txtKey = (TextView) findViewById(R.id.txtKey);
        lstBirds = (ListView) findViewById(R.id.lstBirds);

        populateBirdList();

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

    private void populateBirdList() {

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
                System.out.println("Position - " + mAdapter.getPosition(position));
                System.out.println("String - " + mAdapter.getItem(mAdapter.getPosition(position) + 1));
                Globals.selectedBird = mAdapter.getItem(mAdapter.getPosition(position) + 1);
                startActivity(new Intent(ListBirdsActivity.this, ViewBirdActivity.class));
            }
        });


    }

}
