package uk.co.walesbirds.birdwatching;

import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {


    Switch swLanguage;
    Button btnUpdatePictures;
    Button btnReloadPictures;
    Button btnDeletePictures;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        swLanguage = (Switch) findViewById(R.id.swLanguage);
        btnUpdatePictures = (Button) findViewById(R.id.btnUpdatePictures);
        btnReloadPictures = (Button) findViewById(R.id.btnReloadPictures);
        btnDeletePictures = (Button) findViewById(R.id.btnDeletePictures);


        swLanguage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Globals.english = !(Globals.english);
                System.out.println("ENGLISH??? " + Globals.english);
            }

        });



    }
}
