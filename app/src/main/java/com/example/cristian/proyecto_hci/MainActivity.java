package com.example.cristian.proyecto_hci;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Region;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.LanguageCodes;
import com.microsoft.projectoxford.vision.contract.Line;
import com.microsoft.projectoxford.vision.contract.OCR;
import com.microsoft.projectoxford.vision.contract.Word;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity {

    private VisionServiceClient visionServiceClient = new VisionServiceRestClient("8d763d52e385402ca29c742cd41e1f20");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.gmail);
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setImageBitmap(mBitmap);

        Button btnProcess = (Button)findViewById(R.id.btnProcess);

        //Convert bitmap to stream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask<InputStream,String,String> recognizeTestTask = new AsyncTask<InputStream, String, String>() {
                   ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
                    @Override
                    protected String doInBackground(InputStream... params) {
                        try {

                            publishProgress("Recognizing...");
                            OCR ocr = visionServiceClient.recognizeText(params[0], LanguageCodes.English,true);
                            String result = new Gson().toJson(ocr);
                            return result;

                        }
                        catch (Exception ex)
                        {
                            return null;
                        }
                    }

                    @Override
                    protected void onPreExecute() {
                        mDialog.show();

                    }

                    @Override
                    protected void onPostExecute(String s) {
                        mDialog.dismiss();
                        OCR ocr = new Gson().fromJson(s,OCR.class);
                        TextView txtDescription = (TextView)findViewById(R.id.txtDescription);
                        StringBuilder stringBuilder = new StringBuilder();

                        for(com.microsoft.projectoxford.vision.contract.Region region:ocr.regions)
                        {
                            for(Line line:region.lines ){
                                for(Word word:line.words)
                                    stringBuilder.append(word.text+"");
                                stringBuilder.append("\n");

                            }
                            stringBuilder.append("\n\n");





                        }

                        txtDescription.setText(stringBuilder);


                    }

                    @Override
                    protected void onProgressUpdate(String... values) {
                        mDialog.setMessage(values[0]);
                    }
                };

              recognizeTestTask.execute(inputStream);
            }
        });

    }
}
