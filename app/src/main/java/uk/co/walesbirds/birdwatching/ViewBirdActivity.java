package uk.co.walesbirds.birdwatching;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.io.FileInputStream;

public class ViewBirdActivity extends AppCompatActivity {

    private TextView txtBirdName;
    private TextView txtBirdDetails;
    private ImageView imgBird;
    private BirdEntry bird;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bird);

        txtBirdName = (TextView) findViewById(R.id.txtBirdName);
        txtBirdDetails = (TextView) findViewById(R.id.txtBirdDetails);
        imgBird = (ImageView) findViewById(R.id.imgBird);

        String english = getEnglish();

        bird = findBird(english);

        populateDetails(bird);
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
     * Method for populating the details with the BirdEntry for reading
     * @param birdEntry
     */
    private void populateDetails(BirdEntry birdEntry){

        String englishName = birdEntry.getEnglish();
        String englishCaps = englishName.substring(0,1).toUpperCase() + englishName.substring(1);
        String welshName = birdEntry.getWelsh();
        String welshCaps = welshName.substring(0,1).toUpperCase() + welshName.substring(1);

        if (Globals.english){


            txtBirdName.setText(englishCaps);
            txtBirdDetails.setText("Latin: " + birdEntry.getLatin() + "\nWelsh: " +
                    welshCaps + "\nWelsh Plural: " + birdEntry.getPlural());
        }
        else {
            txtBirdName.setText(welshCaps);
            txtBirdDetails.setText("Latin: " + birdEntry.getLatin() + "\nEnglish: " +
                    englishCaps);
        }


        if (!birdEntry.getPictureLink().equals("")){

            String fileName = birdEntry.getEnglish().replace(" ","_") + ".jpg";

            if (isImageExistant(fileName)){

                File imgFile = getBaseContext().getFileStreamPath(fileName);
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getPath());
                imgBird.setImageBitmap(myBitmap);
            }
        }
    }

    /**
     * Method to check if there is an existing image
     * @param fileName
     * @return boolean If the image exists
     */
    private boolean isImageExistant(String fileName){
        File file = getBaseContext().getFileStreamPath(fileName);
        return file.exists();
    }

    /**
     * Method for getting the english from the selected BirdEntry String
     * @return String The English name of the bird
     */
    private String getEnglish(){
        String selected[] = Globals.selectedBird.split("\n", -1);

        String english = "";

        if (Globals.english){
            english = selected[0];
        } else {
            english = selected[1];
        }

        return english;
    }
}
