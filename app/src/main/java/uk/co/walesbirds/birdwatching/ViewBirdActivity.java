package uk.co.walesbirds.birdwatching;

import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ViewBirdActivity extends AppCompatActivity {

    private TextView txtBirdName;
    private TextView txtBirdDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bird);

        txtBirdName = (TextView) findViewById(R.id.txtBirdName);
        txtBirdDetails = (TextView) findViewById(R.id.txtBirdDetails);

        String english = getEnglish();

        populateDetails(findBird(english));

    }

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

    private void populateDetails(BirdEntry birdEntry){

        if (Globals.english){
            txtBirdName.setText(birdEntry.getEnglish());
            txtBirdDetails.setText("Latin: " + birdEntry.getLatin() + "\nEnglish: " +
                    birdEntry.getEnglish());
        }
        else {
            txtBirdName.setText(birdEntry.getWelsh());
            txtBirdDetails.setText("Latin: " + birdEntry.getLatin() + "\nWelsh: " +
                    birdEntry.getEnglish() + "\n Welsh Plural: " + birdEntry.getPlural());
        }

    }

    private String getEnglish(){
        String selected[] = Globals.selectedBird.split("\n", -1);
        return selected[0];
    }
}
