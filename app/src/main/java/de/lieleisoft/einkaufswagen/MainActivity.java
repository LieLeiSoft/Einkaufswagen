package de.lieleisoft.einkaufswagen;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import de.lieleisoft.einkaufswagen.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int FILE_SELECT_CODE = 1;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            btnImportCSV();
            return true;
        }

        if (id == R.id.action_exit) {
            finish();
            System.exit(0);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK) {
            try {
                Uri returnUri = data.getData(); //The uri with the location of the file

                //https://developer.android.com/training/secure-file-sharing/retrieve-info
                //https://developer.android.com/training/secure-file-sharing/request-file

                /*
                    File security is preserved in this process because the content URI is the only piece of data that the client app receives.
                    Since this URI doesn't contain a directory path, the client app can't discover and open any other files in the server app.
                    Only the client app gets access to the file, and only for the permissions granted by the server app.
                    The permissions are temporary, so once the client app's task stack is finished, the file is no longer accessible outside the server app.
                 */
                Cursor returnCursor;
                returnCursor = getContentResolver().query(returnUri, null, null, null, null);
                /*
                 * Get the column indexes of the data in the Cursor,
                 * move to the first row in the Cursor, get the data,
                 * and display it.
                 */
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                returnCursor.moveToFirst();

                Log.d(TAG, "Dateiname: "+returnCursor.getString(nameIndex));
                Log.d(TAG, "Größe: "    +Long.toString(returnCursor.getLong(sizeIndex)));

                readData(returnUri, this);

                returnCursor.close();

                /*
                TextView nameView = (TextView) findViewById(R.id.filename_text);
                TextView sizeView = (TextView) findViewById(R.id.filesize_text);
                nameView.setText(returnCursor.getString(nameIndex));
                sizeView.setText(Long.toString(returnCursor.getLong(sizeIndex)));
                */
            } catch (Exception ex) {
                Log.e(TAG, "onActivityResult: "+ex.toString());
            }
        }
    } // onActivityResult

    public void btnImportCSV()
    {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "Select a file"), FILE_SELECT_CODE);
        } catch (Exception ex) {
            Log.e(TAG, "btnImportCSV: "+ex.toString());
        }
    }

    private void readData(Uri uri, Context context)
            throws IOException
    {
        InputStream is = context.getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8")));
        String line = "";

        try {
            while ((line = reader.readLine()) != null) {
                // Split the line into different tokens (using the comma as a separator).
                String[] tokens = line.split(":"); // Trennzeichen = Doppelpunkt (als Beispiel)

                //Log.d(TAG, "Inhalt: "+line);
                for (int i = 0; i < tokens.length; i++) {
                    Log.d(TAG, "Token "+String.valueOf(i)+": "+tokens[i]);
                }
            }
        } catch (IOException e1) {
            Log.e("MainActivity", "Error" + line, e1);
            e1.printStackTrace();
        }
    }

}