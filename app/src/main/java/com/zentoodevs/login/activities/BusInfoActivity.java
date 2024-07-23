package com.zentoodevs.login.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.bumptech.glide.Glide;
import com.zentoodevs.login.R;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class BusInfoActivity extends AppCompatActivity {

    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bus_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageView = findViewById(R.id.imageView);
        FunBack();
        setParmsFromIntent();
        Intent intent = getIntent();
        String busName = intent.getStringExtra("busName");
        String query = busName + "+bus+lima";
        String urlString = "http://images.google.com.uy/images?q=" + query;
        new FetchImageTask().execute(urlString);
    }

    private void FunBack(){
        Button btnBack = findViewById(R.id.back_button);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setParmsFromIntent(){
        Intent intent = getIntent();
        String busName = intent.getStringExtra("busName");
        String busDistance = intent.getStringExtra("busDistance");
        String busDuration = intent.getStringExtra("busDuration");
        String durationAll = intent.getStringExtra("DurationAll");
        String distanceAll = intent.getStringExtra("DistanceAll");
        TextView shortBus = findViewById(R.id.titleBusName);
        shortBus.setText(busName);
        TextView distanceBus = findViewById(R.id.txtBusDistance);
        distanceBus.setText(busDistance);
        TextView durationBus = findViewById(R.id.txtDuration);
        durationBus.setText(busDuration);
        TextView txtDurationAll = findViewById(R.id.txtDurationAll);
        txtDurationAll.setText(durationAll);
        TextView txtDistanceAll = findViewById(R.id.txtDistanceAll);
        txtDistanceAll.setText(distanceAll);
    }

    private class FetchImageTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            String urlString = urls[0];
            StringBuilder htmlContent = new StringBuilder();
            try {
                URL google = new URL(urlString);
                BufferedReader in = new BufferedReader(new InputStreamReader(google.openStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    htmlContent.append(inputLine);
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String html = htmlContent.toString();
            return extractFirstImageUrl(html);
        }

        protected void onPostExecute(String imageUrl) {
            if (imageUrl != null) {
                // Usar Glide para cargar la imagen en el ImageView
                Glide.with(BusInfoActivity.this).load(imageUrl).into(imageView);
            }
        }

        private String extractFirstImageUrl(String html) {
            String imgPattern = "http[s]?://[^\\s]*?\\.jpg";
            Pattern pattern = Pattern.compile(imgPattern);
            Matcher matcher = pattern.matcher(html);

            if (matcher.find()) {
                return matcher.group();
            }
            return null;
        }

    }
}
