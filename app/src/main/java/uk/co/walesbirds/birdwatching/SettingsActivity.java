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
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SettingsActivity extends AppCompatActivity {


    Switch swLanguage;
    Button btnUpdatePictures;
    Button btnReloadPictures;
    Button btnDeletePictures;
    ProgressDialog mProgressDialog;


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

        btnUpdatePictures.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                System.out.println("Number of pictures: " + countPictures());

                updatePictures();

            }

        });

        btnDeletePictures.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                deletePictures();
            }

        });
    }

    private int countPictures(){
        int count = 0;

        for (BirdEntry bEn : Globals.Birds.BirdEntries){
            if (!bEn.getPictureLink().equals("")){
                count++;
            }
        }

        return count;
    }

    private void updatePictures(){

        int totalPictures = countPictures();
        int count = 1;

        mProgressDialog = new ProgressDialog(SettingsActivity.this);
        mProgressDialog.setMessage("Downloading pictures... " + count + "/" + totalPictures);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);

        final DownloadTask downloadTask = new SettingsActivity.DownloadTask(SettingsActivity.this, getFileNames());
        downloadTask.execute(getPictureLinks());

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTask.cancel(true);
            }

        });

        count++;
    }

    private void deletePictures() {

        File dir = getFilesDir();

        int count = 0;

        for (BirdEntry bEn : Globals.Birds.BirdEntries){

            if (!bEn.getPictureLink().equals("")){

                String fileName = bEn.getEnglish().replace("","_");
                File file = getBaseContext().getFileStreamPath(fileName);
                boolean deleted = file.delete();

                if (deleted)
                    System.out.println("DELETED - " + count++);
                else
                    System.out.println("NOT DELETED");
            }
        }

    }

    private void redownloadPictures(){

    }

    private String [] getPictureLinks() {
        String [] fileNames = new String[countPictures()];

        int i = 0;

        for (BirdEntry bEn : Globals.Birds.BirdEntries){
            if(!bEn.getPictureLink().equals("")) fileNames[i++] = bEn.getPictureLink();
        }

        return fileNames;
    }

    private String [] getFileNames() {
        String [] pictureLinks = new String [countPictures()];

        int i = 0;

        for (BirdEntry bEn : Globals.Birds.BirdEntries){
            if(!bEn.getPictureLink().equals("")) pictureLinks[i++] = bEn.getEnglish().replace("","_") + ".jpg";
        }

        return pictureLinks;
    }


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
