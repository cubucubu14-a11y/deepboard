package com.example.devkeys;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 150, 60, 50);
        layout.setBackgroundColor(0xFF121212);

        TextView title = new TextView(this);
        title.setText("DeepBoard Keyboard");
        title.setTextColor(0xFFFFFFFF);
        title.setTextSize(28);
        title.setPadding(0, 0, 0, 30);

        TextView desc = new TextView(this);
        desc.setText("Cara pakai:\n\n1. Klik 'Buka Pengaturan'.\n2. Aktifkan DeepBoard.\n3. Klik 'Pilih Keyboard'.\n4. Pilih DeepBoard.\n\nSelamat ngoding!");
        desc.setTextColor(0xFFAAAAAA);
        desc.setTextSize(16);
        desc.setPadding(0, 0, 0, 60);

        Button btnSettings = new Button(this);
        btnSettings.setText("1. Buka Pengaturan Keyboard");
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
            startActivity(intent);
        });

        Button btnPicker = new Button(this);
        btnPicker.setText("2. Pilih Keyboard (Ganti Input)");
        btnPicker.setOnClickListener(v -> {
            InputMethodManager imeManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imeManager.showInputMethodPicker();
        });

        layout.addView(title);
        layout.addView(desc);
        layout.addView(btnSettings);
        layout.addView(btnPicker);

        setContentView(layout);
    }
}
