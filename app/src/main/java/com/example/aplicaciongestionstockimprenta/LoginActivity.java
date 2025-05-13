package com.example.aplicaciongestionstockimprenta;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText usuarioEt, passwordEt;
    private Button loginBtn;
    private ProgressBar progress;
    private OdooService service;
    private final String DB_NAME = "gestion_almacen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SessionManager.init(getApplicationContext());
        service = RetrofitClient.getOdooService();

        usuarioEt = findViewById(R.id.emailLoginEditText);
        passwordEt = findViewById(R.id.passwordLoginEditText);
        loginBtn = findViewById(R.id.loginButton);
        progress = findViewById(R.id.progressBar);

        loginBtn.setOnClickListener(view -> {
            String usuario = usuarioEt.getText().toString().trim();
            String password = passwordEt.getText().toString().trim();

            if (usuario.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            progress.setVisibility(View.VISIBLE);
            loginBtn.setEnabled(false);

            // Construye el cuerpo correcto para /web/session/authenticate
            JsonObject loginRequest = new JsonObject();
            loginRequest.addProperty("jsonrpc", "2.0");
            JsonObject params = new JsonObject();
            params.addProperty("db", DB_NAME);
            params.addProperty("login", usuario);
            params.addProperty("password", password);
            loginRequest.add("params", params);

            Log.d("LOGIN", "🔐 Enviando login a /web/session/authenticate");

            service.login(loginRequest).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> resp) {
                    progress.setVisibility(View.GONE);
                    loginBtn.setEnabled(true);

                    if (resp.isSuccessful() && resp.body() != null && resp.body().has("result")) {
                        JsonObject result = resp.body().getAsJsonObject("result");
                        if (!result.has("uid")) {
                            Toast.makeText(LoginActivity.this, "Respuesta sin UID", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        int uid = result.get("uid").getAsInt();
                        Log.d("LOGIN", "✅ UID recibido: " + uid);

                        if (uid <= 0) {
                            Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        obtenerRolYContinuar(usuario, password);

                    } else {
                        Toast.makeText(LoginActivity.this, "Login incorrecto", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    progress.setVisibility(View.GONE);
                    loginBtn.setEnabled(true);
                    Log.e("LOGIN", "❗ Error de red en login", t);
                    Toast.makeText(LoginActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void obtenerRolYContinuar(String login, String password) {
        JsonObject body = new JsonObject();
        body.addProperty("jsonrpc", "2.0");

        Log.d("LOGIN", "🔐 Enviando login a /api/rol_usuario");

        service.obtenerRol(body).enqueue(new Callback<RolWrapper>() {
            @Override
            public void onResponse(Call<RolWrapper> call, Response<RolWrapper> response) {
                if (response.isSuccessful() && response.body() != null && response.body().result != null) {
                    RolWrapper.RolResponse data = response.body().result;

                    Log.d("LOGIN", "🧪 Rol recibido desde Odoo: " + data.rol);
                    Log.d("LOGIN", "🧪 Usuario: " + data.usuario + " | UID: " + data.id);

                    if (data.rol == null || data.rol.isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Rol vacío o inválido", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    i.putExtra("uid", data.id);
                    i.putExtra("usuario", data.usuario);
                    i.putExtra("password", password);
                    i.putExtra("rol", data.rol);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Error al obtener el rol", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RolWrapper> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error de red al obtener rol", Toast.LENGTH_SHORT).show();
                Log.e("LOGIN", "❗ Error al obtener rol", t);
            }
        });
    }
}
