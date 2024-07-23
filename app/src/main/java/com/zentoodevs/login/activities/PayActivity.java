package com.zentoodevs.login.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.zentoodevs.login.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pay);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setParmsFromIntent();
        Button btnBack = findViewById(R.id.backButton);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setParmsFromIntent(){
        Intent intent = getIntent();
        String busName = intent.getStringExtra("Bus");
        String mount = intent.getStringExtra("Mount");
        String description = intent.getStringExtra("Description");

        TextView mountTView = findViewById(R.id.mount);
        mountTView.setText("S./"+mount);
        TextView descriptionTView = findViewById(R.id.description);
        descriptionTView.setText(busName + " - " + description);
        TextView dateTView = findViewById(R.id.date);
        dateTView.setText(getCurrentFormattedDate());

    }
    public static String getCurrentFormattedDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM. yyyy - HH:mm", new Locale("es", "ES"));
        Date date = new Date();
        return dateFormat.format(date);
    }

}