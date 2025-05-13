package com.example.aplicaciongestionstockimprenta;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private String rol, user, password;
    private int uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Oculta pantalla inicial para evitar parpadeos si redirige
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
        super.onCreate(savedInstanceState);

        rol      = getIntent().getStringExtra("rol");
        user     = getIntent().getStringExtra("usuario");
        password = getIntent().getStringExtra("password");
        uid      = getIntent().getIntExtra("uid", -1);

        Log.d("MainActivity", "🧪 Rol recibido: '" + rol + "'");
        Log.d("MainActivity", "🧪 Usuario: " + user + " | UID: " + uid);

        if (rol != null && !rol.trim().isEmpty()) {
            Intent intent;
            switch (rol) {
                case "admin":
                    intent = new Intent(this, AdminActivity.class);
                    break;
                case "contabilidad":
                    intent = new Intent(this, ContabilidadActivity.class);
                    break;
                case "fabrica":
                    intent = new Intent(this, FabricaActivity.class);
                    break;
                case "stock":
                    intent = new Intent(this, StockListActivity.class);
                    break;
                default:
                    Log.w("MainActivity", "⚠️ Rol desconocido: " + rol + ", redirigiendo a LoginActivity");
                    intent = new Intent(this, LoginActivity.class);
                    break;
            }

            intent.putExtra("rol", rol);
            intent.putExtra("usuario", user);
            intent.putExtra("password", password);
            intent.putExtra("uid", uid);

            Log.d("MainActivity", "➡️ Redirigiendo a actividad según rol: " + rol);
            startActivity(intent);
            finish();
            return;
        }

        Log.e("MainActivity", "❌ Rol es nulo o vacío, mostrando pantalla por defecto");
        setContentView(R.layout.activity_main);
    }
}
