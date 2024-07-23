package com.zentoodevs.login.repository;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.zentoodevs.login.repository.callbacks.BalanceCallback;
import com.zentoodevs.login.repository.models.BalanceResponse;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BalanceRepository {
    private static final String BASE_URL = "http://192.168.18.12:5000";

    public static void obtenerSaldoAsync(String usuario, BalanceCallback callback) {
        new ObtenerSaldoTask(usuario, callback).execute();
    }

    private static class ObtenerSaldoTask extends AsyncTask<Void, Void, BalanceResponse> {
        private final String usuario;
        private final BalanceCallback callback;
        private Exception exception;

        public ObtenerSaldoTask(String usuario, BalanceCallback callback) {
            this.usuario = usuario;
            this.callback = callback;
        }

        @Override
        protected BalanceResponse doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();

            String url = BASE_URL + "/saldo/" + usuario;
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Error en la solicitud: " + response);
                }

                String responseBody = response.body().string();
                Gson gson = new Gson();
                return gson.fromJson(responseBody, BalanceResponse.class);
            } catch (IOException e) {
                this.exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(BalanceResponse saldoResponse) {
            if (exception != null) {
                callback.onError(exception);
            } else {
                callback.onSuccess(saldoResponse);
            }
        }
    }

}
