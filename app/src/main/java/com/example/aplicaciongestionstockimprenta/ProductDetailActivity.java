package com.example.aplicaciongestionstockimprenta;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView tvProductName, tvCurrentStock;
    private EditText etNewStock;
    private Button btnUpdateStock;

    private int productId, currentStock, uid;
    private String productName, password;

    private OdooService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        tvProductName = findViewById(R.id.tvProductName);
        tvCurrentStock = findViewById(R.id.tvCurrentStock);
        etNewStock = findViewById(R.id.etNewStock);
        btnUpdateStock = findViewById(R.id.btnUpdateStock);

        // Obtener datos del Intent
        productId = getIntent().getIntExtra("id", -1);
        productName = getIntent().getStringExtra("name");
        currentStock = getIntent().getIntExtra("cantidad_stock", 0);
        uid = getIntent().getIntExtra("uid", -1);
        password = getIntent().getStringExtra("password");

        if (productId == -1 || productName == null || uid == -1 || password == null) {
            Toast.makeText(this, "Datos incompletos", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvProductName.setText(productName);
        tvCurrentStock.setText("Stock actual: " + currentStock);

        service = RetrofitClient.getOdooService();

        btnUpdateStock.setOnClickListener(v -> {
            String newStockStr = etNewStock.getText().toString().trim();
            if (newStockStr.isEmpty()) {
                Toast.makeText(this, "Introduce un valor de stock", Toast.LENGTH_SHORT).show();
                return;
            }

            int newStock;
            try {
                newStock = Integer.parseInt(newStockStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Número no válido", Toast.LENGTH_SHORT).show();
                return;
            }

            actualizarStockEnOdoo(newStock);
        });
    }

    private void actualizarStockEnOdoo(int nuevoStock) {
        JsonObject params = new JsonObject();
        params.addProperty("producto_id", productId);
        params.addProperty("nueva_cantidad", nuevoStock);

        JsonObject body = new JsonObject();
        body.add("params", params);

        Log.d("PRODUCT_DETAIL", "Actualizando stock con JSON: " + body.toString());

        service.actualizarStock(body).enqueue(new Callback<ResultadoWrapper>() {
            @Override
            public void onResponse(Call<ResultadoWrapper> call, Response<ResultadoWrapper> response) {
                if (response.isSuccessful() && response.body() != null && response.body().result != null) {
                    String mensaje = response.body().result.resultado;
                    Log.d("PRODUCT_DETAIL", "✅ Stock actualizado correctamente: " + mensaje);
                    Toast.makeText(ProductDetailActivity.this, mensaje, Toast.LENGTH_SHORT).show();

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("id_actualizado", productId);
                    resultIntent.putExtra("nuevo_stock", nuevoStock);
                    setResult(RESULT_OK, resultIntent);
                    finish();

                } else {
                    Log.e("PRODUCT_DETAIL", "⚠️ Respuesta inesperada o incompleta");
                    Toast.makeText(ProductDetailActivity.this, "Respuesta inesperada del servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResultadoWrapper> call, Throwable t) {
                Log.e("PRODUCT_DETAIL", "❌ Error de red", t);
                Toast.makeText(ProductDetailActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
