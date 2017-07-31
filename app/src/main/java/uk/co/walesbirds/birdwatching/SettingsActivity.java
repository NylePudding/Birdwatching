package uk.co.walesbirds.birdwatching;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 */
public class SettingsActivity extends AppCompatActivity {


    Switch swLanguage;
    Button btnUpdatePictures;
    Button btnReloadPictures;
    Button btnDeletePictures;
    TextView txtUpdate;
    TextView txtReload;
    TextView txtDelete;
    ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        swLanguage = (Switch) findViewById(R.id.swLanguage);
        btnUpdatePictures = (Button) findViewById(R.id.btnUpdatePictures);
        btnReloadPictures = (Button) findViewById(R.id.btnReloadPictures);
        btnDeletePictures = (Button) findViewById(R.id.btnDeletePictures);
        txtUpdate = (TextView) findViewById(R.id.txtUpdate);
        txtReload = (TextView) findViewById(R.id.txtReload);
        txtDelete = (TextView) findViewById(R.id.txtDelete);

        setLanguage(Globals.english);

        swLanguage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Globals.english = !(Globals.english);
                setLanguage(Globals.english);
            }

        });

        btnUpdatePictures.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                System.out.println("Number of pictures: " + countPictures());
                updatePictures();

            }

        });

        btnReloadPictures.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                System.out.println("Number of pictures: " + countPictures());
                redownloadPictures();
            }

        });

        btnDeletePictures.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                deletePictures();
            }

        });
    }

    private void setLanguage(boolean english){
        if (english){
            swLanguage.setChecked(false);
            swLanguage.setText("English");
            btnUpdatePictures.setText("UPDATE PICTURES");
            txtUpdate.setText("Download any pictures you don't have.");
            btnReloadPictures.setText("RELOAD PICTURES");
            txtReload.setText("Delete and reload all pictures");
            btnDeletePictures.setText("DELETE PICTURES");
            txtDelete.setText("This will delete all pictures");
        } else {
            swLanguage.setChecked(true);
            swLanguage.setText("Cyrmraeg");
            btnUpdatePictures.setText("WW UPDATE PICTURES WW");
            txtUpdate.setText("WW Download any pictures you don't have. WW");
            btnReloadPictures.setText("WW RELOAD PICTURES WW");
            txtReload.setText("WW Delete and reload all pictures WW");
            btnDeletePictures.setText("WW DELETE PICTURES WW");
            txtDelete.setText("WW This will delete all pictures WW");
        }
    }

    /**
     * Method to check if there is an existing image
     * @param fileName
     * @return boolean If the image exists
     */
    private boolean isImageExistant(String fileName){
        File file = getBaseContext().getFileStreamPath(fileName.replace("/","-"));
        return file.exists();
    }

    /**
     * Counts the number of picture links in the list of Bird Entries
     * @return int number of picture links
     */
    private int countPictures(){
        int count = 0;

        for (BirdEntry bEn : Globals.Birds.BirdEntries){
            if (!bEn.getPictureLink().equals("")){
                count++;
            }
        }

        return count;
    }

    /**
     * Counts the number of picture links in the list of Bird Entries
     * @return int number of picture links
     */
    private int countUpdatedPictures(){
        int count = 0;

        for (BirdEntry bEn : Globals.Birds.BirdEntries){
            if (!bEn.getPictureLink().equals("")){
                if (!isImageExistant(bEn.getEnglish().replace(" ","_") + ".jpg")) {
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * Downloads and updates all the pictures saved in the internal storage
     */
    private void updatePictures(){

        int totalPictures = countUpdatedPictures();

        //Create a process dialogue for the download
        mProgressDialog = new ProgressDialog(SettingsActivity.this);
        mProgressDialog.setMessage("Downloading "+ totalPictures + " total pictures");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);

        //Create a Download task and supply in with all the file names
        final DownloadTask downloadTask = new SettingsActivity.DownloadTask(SettingsActivity.this, getUpdatedFileNames());
        //Execute the Download Task with all the picture links
        downloadTask.execute(getUpdatedPictureLinks());

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTask.cancel(true);
            }

        });

    }

    /**
     *  Deletes all the picture in the internal storage
     */
    private void deletePictures() {


        int count = 0;

        for (BirdEntry bEn : Globals.Birds.BirdEntries){

            if (!bEn.getPictureLink().equals("")){

                String fileName = bEn.getEnglish().replace(" ","_") + ".jpg";
                File file = getBaseContext().getFileStreamPath(fileName);
                boolean deleted = file.delete();

                if (deleted)
                    System.out.println("DELETED - " + count++);
                else
                    System.out.println("NOT DELETED");
            }
        }

        Toast.makeText(getBaseContext(),"Pictures deleted...", Toast.LENGTH_SHORT).show();

    }

    /**
     * Redownloads all the pictures to the internal storage
     */
    private void redownloadPictures(){
        int totalPictures = countPictures();

        //Create a process dialogue for the download
        mProgressDialog = new ProgressDialog(SettingsActivity.this);
        mProgressDialog.setMessage("Downloading " + totalPictures + " total pictures...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);

        //Create a Download task and supply in with all the file names
        final DownloadTask downloadTask = new SettingsActivity.DownloadTask(SettingsActivity.this, getFileNames());
        //Execute the Download Task with all the picture links
        downloadTask.execute(getPictureLinks());

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTask.cancel(true);
            }

        });
    }

    private String convertPictureLink(String pictureLink){
        int index = pictureLink.indexOf("open?id=");

        String id = pictureLink.substring(index + 8);

        String finalLink = "https://drive.google.com/uc?export=download&id=" + id;

        return finalLink;
    }



    /**
     * Gets all the picture links in the list of Bird Entries
     * @return String[] All the picture links
     */
    private String [] getPictureLinks() {
        String [] fileNames = new String[countPictures()];

        int i = 0;

        for (BirdEntry bEn : Globals.Birds.BirdEntries){
            //If the picture link is not blank
            if(!bEn.getPictureLink().equals("")) fileNames[i++] = convertPictureLink(bEn.getPictureLink());
        }

        return fileNames;
    }

    /**
     * Gets all the picture links in the list of Bird Entries
     * @return String[] All the picture links
     */
    private String [] getUpdatedPictureLinks() {
        String [] fileNames = new String[countUpdatedPictures()];

        int i = 0;

        for (BirdEntry bEn : Globals.Birds.BirdEntries){
            //If the picture link is not blank
            if (!isImageExistant(bEn.getEnglish().replace(" ","_") + ".jpg")) {
                if (!bEn.getPictureLink().equals("")) fileNames[i++] = convertPictureLink(bEn.getPictureLink());
            }
        }

        return fileNames;
    }

    /**
     * Gets all the file names of the pictures to-be saved to internal storage
     * @return String[] All the file names
     */
    private String [] getFileNames() {
        String [] pictureLinks = new String [countPictures()];

        int i = 0;

        for (BirdEntry bEn : Globals.Birds.BirdEntries){
            if(!bEn.getPictureLink().equals("")) pictureLinks[i++] = bEn.getEnglish().replace(" ","_") + ".jpg";
        }

        return pictureLinks;
    }

    private String [] getUpdatedFileNames() {
        String [] pictureLinks = new String [countPictures()];

        int i = 0;

        for (BirdEntry bEn : Globals.Birds.BirdEntries){
            if (!isImageExistant(bEn.getEnglish().replace(" ","_") + ".jpg")) {
                if (!bEn.getPictureLink().equals(""))
                    pictureLinks[i++] = bEn.getEnglish().replace(" ", "_") + ".jpg";
            }
        }

        return pictureLinks;
    }

    /**
     * Asynchronous task for Downloading multiple pictures
     */
    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private String [] fileNames;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context, String [] fileNames) {
            this.context = context;
            this.fileNames = fileNames;
        }

        @Override
        protected String doInBackground(String... sUrl) {

            for (int i=0; i < sUrl.length; i++) {

                InputStream input = null;
                OutputStream output = null;
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(sUrl[i]);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    // expect HTTP 200 OK, so we don't mistakenly save error report
                    // instead of the file
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        return "Server returned HTTP " + connection.getResponseCode()
                                + " " + connection.getResponseMessage();
                    }

                    // this will be useful to display download percentage
                    // might be -1: server did not report the length
                    int fileLength = connection.getContentLength();

                    // download the file
                    input = connection.getInputStream();

                    File directory = getFilesDir(); //or getExternalFilesDir(null); for external storage
                    File file = new File(directory, fileNames[i]);

                    output = new FileOutputStream(file);

                    byte data[] = new byte[4096];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        // allow canceling with back button
                        if (isCancelled()) {
                            input.close();
                            return null;
                        }
                        total += count;
                        // publishing the progress....
                        if (fileLength > 0) // only if total length is known
                            publishProgress((int) (total * 100 / fileLength));
                        output.write(data, 0, count);
                    }
                } catch (Exception e) {
                    return e.toString();
                } finally {
                    try {
                        if (output != null)
                            output.close();
                        if (input != null)
                            input.close();
                    } catch (IOException ignored) {
                    }

                    if (connection != null)
                        connection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null)
                Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
        }
    }
}
