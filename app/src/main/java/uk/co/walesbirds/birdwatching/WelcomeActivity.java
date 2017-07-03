package uk.co.walesbirds.birdwatching;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {

    ImageButton btnListBirds;
    ImageButton btnUpdate;
    ImageButton btnSettings;
    TextView txtWelcome;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        btnListBirds = (ImageButton) findViewById(R.id.ibtnListBirds);
        btnUpdate = (ImageButton) findViewById(R.id.ibtnUpdate);
        btnSettings = (ImageButton) findViewById(R.id.ibtnSettings);
        txtWelcome = (TextView) findViewById(R.id.txtWelcome);

        if (Globals.english){
            txtWelcome.setText("Select a category for translations");
        }
        else {
            txtWelcome.setText("<WELSH PLACEHOLDER TEXT>");
        }

        if (isFileUpToDate()){
            btnUpdate.setEnabled(false);
        } else {
            btnUpdate.setEnabled(true);
        }

        btnListBirds.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                if (!isFileExistant()){
                    download();
                }
                if (Globals.Birds.BirdEntries.size() == 0){
                    initiateBirdEntries();
                }

                startActivity(new Intent(WelcomeActivity.this, ListBirdsActivity.class));
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                download();
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (Globals.Birds.BirdEntries.size() == 0){
                    initiateBirdEntries();
                }
                startActivity(new Intent(WelcomeActivity.this, SettingsActivity.class));
            }
        });

    }

    private boolean isFileExistant(){
        File file = getBaseContext().getFileStreamPath("birds.csv");
        return file.exists();
    }

    private boolean isFileUpToDate(){
        return true;
    }

    private void initiateBirdEntries(){

        InputStream is = null;
        String cvsSplitBy = ",";

        try {

            FileInputStream fis = openFileInput("birds.csv");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            List<String> lines = new ArrayList<>();
            String line = null;
            int count = 0;

            int[] indexes = null;

            while ((line = br.readLine()) != null) {

                if (count == 0){
                    count++;
                    continue;
                }
                if (count == 1){
                    indexes = getColumnIndexes(line);
                    count++;
                    continue;
                }

                lines.add(line);
                String row[] = line.split(cvsSplitBy, -1);

                if (!row[indexes[8]].equals("")){
                    BirdEntry bEn = new BirdEntry();
                    bEn.setType(row[indexes[0]]);
                    bEn.setWelshType(row[indexes[1]]);
                    bEn.setLatin(row[indexes[2]]);
                    bEn.setEnglish(row[indexes[3]]);
                    bEn.setWelsh(row[indexes[4]]);
                    bEn.setPlural(row[indexes[5]]);
                    bEn.setGender(row[indexes[6]]);
                    bEn.setUnknown(row[indexes[7]]);
                    bEn.setCategory(row[indexes[8]]);
                    bEn.setSubcategory(row[indexes[9]]);
                    bEn.setWelsheDetails(row[indexes[10]]);
                    bEn.setEnglishDetails(row[indexes[11]]);
                    bEn.setWikiLink(row[indexes[12]]);
                    bEn.setPictureCredit(row[indexes[13]]);
                    bEn.setPictureLink(row[indexes[14]]);
                    bEn.setVisitor(row[indexes[15]]);
                    bEn.setRare(row[indexes[16]]);
                    bEn.setLocation(row[indexes[17]]);

                    Globals.Birds.BirdEntries.add(bEn);
                }

                count++;
            }

            for (BirdEntry bEn : Globals.Birds.BirdEntries){
                //System.out.println(bEn.getEnglish());
            }

            System.out.println("Number of birds: " + Globals.Birds.BirdEntries.size());

        } catch (IOException e) {
            e.printStackTrace();
        }

        Collections.sort(Globals.Birds.BirdEntries);
    }

    private static int[] getColumnIndexes(String line){

        int[] indexes = new int[18];
        String[] indexNames = {"Type", "Welsh Type","Latin","English","Welsh","Plural","Gender",
                "unknown","Category","SubCategory","Welsh Details","English Details","Wiki Link",
                "Picture Credit","Picture Link","Visitor","Rare","Location"};

        String row[] = line.split(",");

        for(int i=0; i< indexes.length; i++){

            int count = 0;

            for(String str : row){
                if (str.equals(indexNames[i])){
                    indexes[i] = count;
                    break;
                }
                count++;
            }
        }
        return indexes;
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
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
                File file = new File(directory, "birds.csv");

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

    private void download(){

        mProgressDialog = new ProgressDialog(WelcomeActivity.this);
        mProgressDialog.setMessage("Downloading data...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);

        final DownloadTask downloadTask = new DownloadTask(WelcomeActivity.this);
        downloadTask.execute("https://docs.google.com/spreadsheets/d/1fmy92PagupIXIo8zExLhGSNjxVKKz493jgTxOly595A/export?format=csv");

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTask.cancel(true);
            }

        });
    }
}
