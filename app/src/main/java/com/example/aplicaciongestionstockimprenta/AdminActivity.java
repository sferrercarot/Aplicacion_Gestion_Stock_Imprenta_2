package com.example.aplicaciongestionstockimprenta;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    private static final String TAG = "AdminActivity";
    private Button btnVerStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Log.d(TAG, "🛠️ AdminActivity arrancado");

        btnVerStock = findViewById(R.id.btnVerStock);
        if (btnVerStock == null) {
            Log.e(TAG, "❌ btnVerStock es null. Verifica el ID en el XML.");
            Toast.makeText(this, "Error interno: botón no encontrado", Toast.LENGTH_LONG).show();
            return;
        }

        btnVerStock.setOnClickListener(v -> {
            Log.d(TAG, "➡️ Pulsado btnVerStock. Abriendo StockListActivity...");

            Intent i = new Intent(AdminActivity.this, StockListActivity.class);

            // Recuperar extras recibidos
            int uid = getIntent().getIntExtra("uid", -1);
            String usuario   = getIntent().getStringExtra("usuario");
            String password  = getIntent().getStringExtra("password");
            String rol       = getIntent().getStringExtra("rol");
            String sessionId = getIntent().getStringExtra("sessionId");

            if (uid == -1 || usuario == null || password == null) {
                Log.w(TAG, "⚠️ Datos de sesión incompletos");
                Toast.makeText(this, "Datos de sesión inválidos", Toast.LENGTH_SHORT).show();
                return;
            }

            i.putExtra("uid", uid);
            i.putExtra("usuario", usuario);
            i.putExtra("password", password);
            i.putExtra("rol", rol);
            i.putExtra("sessionId", sessionId);

            startActivity(i);
        });
    }
}
