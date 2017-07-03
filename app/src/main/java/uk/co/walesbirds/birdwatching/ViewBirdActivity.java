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

import java.io.File;

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


        if (!birdEntry.getPictureLink().equals("")){

            String fileName = birdEntry.getEnglish().replace("","_") + ".jpg";

            if (isImageExistant(fileName)){
                Toast.makeText(ViewBirdActivity.this,"EXISTS", Toast.LENGTH_LONG).show();
                File imgFile = getBaseContext().getFileStreamPath(fileName);
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imgBird.setImageBitmap(myBitmap);
            } else {
                Toast.makeText(ViewBirdActivity.this,"DOES NOT EXIST", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isImageExistant(String fileName){
        File file = getBaseContext().getFileStreamPath(fileName);
        return file.exists();
    }

    private String getEnglish(){
        String selected[] = Globals.selectedBird.split("\n", -1);
        return selected[0];

    }
}
