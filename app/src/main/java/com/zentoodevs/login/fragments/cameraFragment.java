package com.zentoodevs.login.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import com.zentoodevs.login.R;
import com.zentoodevs.login.activities.PayActivity;
import com.zentoodevs.login.repository.StudentRepository;
import com.zentoodevs.login.repository.callbacks.StudentRepositoryCallback;
import com.zentoodevs.login.repository.models.Payment;
import com.zentoodevs.login.repository.models.Student;
import com.zentoodevs.login.repository.models.Transaction;

import java.util.List;

public class cameraFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;
    private CompoundBarcodeView barcodeView;

    private boolean isScanning = true; // Variable para controlar el estado de escaneo

    public cameraFragment() {
        // Required empty public constructor
    }

    public static cameraFragment newInstance(String param1, String param2) {
        cameraFragment fragment = new cameraFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        barcodeView = view.findViewById(R.id.barcode_scanner);
        barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (isScanning) { // Verifica si el escaneo está permitido
                    isScanning = false; // Desactiva el escaneo
                    handleResult(result);
                    // Reactiva el escaneo después de 3 segundos
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isScanning = true;
                        }
                    }, 3000);
                }
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
            }
        });

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            barcodeView.resume();
        }

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                barcodeView.resume();
            } else {
                // Permiso denegado, maneja esto según sea necesario.
            }
        }
    }

    private void handleResult(BarcodeResult result) {
        String qrText = result.getText();
        Log.d("RESULTADO", qrText);

        // Parse JSON from QR code
        Gson gson = new Gson();
        Payment payment;
        try {
            payment = gson.fromJson(qrText, Payment.class);
        } catch (Exception e) {
            Log.e("ERROR", "Error parsing QR JSON", e);
            return;
        }

        // Assuming the user ID is passed as an argument to this fragment
        String user = getArguments().getString("user");

        if (user != null) {
            payment.setUsuario(user);
            StudentRepository studentRepository = new StudentRepository();
            studentRepository.realizarPago(user, payment, new StudentRepositoryCallback() {
                @Override
                public void onSuccess() {
                    Intent intent = new Intent(requireContext(), PayActivity.class);
                    intent.putExtra("Bus", payment.getBus());
                    intent.putExtra("Mount", String.valueOf(payment.getMonto()));
                    intent.putExtra("Description", payment.getDescripcion());
                    startActivity(intent);
                    Log.d("Pago", "Pago realizado correctamente");
                }

                @Override
                public void onTransactionsLoaded(List<Transaction> transactions) {
                }

                @Override
                public void onStudentLoaded(Student student) {
                }

                @Override
                public void onError(String error) {
                    Log.e("Pago", "Error al realizar el pago: " + error);
                }

                @Override
                public void onStudentCreated(String user) {
                }
            });
        } else {
            Log.e("ERROR", "User ID is missing");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeView.pause();
    }
}
