package com.zentoodevs.login.fragments;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zentoodevs.login.R;
import com.zentoodevs.login.repository.BalanceRepository;
import com.zentoodevs.login.repository.StudentRepository;
import com.zentoodevs.login.repository.callbacks.BalanceCallback;
import com.zentoodevs.login.repository.callbacks.StudentRepositoryCallback;
import com.zentoodevs.login.repository.models.BalanceResponse;
import com.zentoodevs.login.repository.models.Student;
import com.zentoodevs.login.repository.models.Transaction;
import com.zentoodevs.login.repository.models.TransactionAdapter;
import com.zentoodevs.login.viewmodel.StudentViewModel;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link walletFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class walletFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String user;
    private StudentViewModel studentViewModel;

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private final List<Transaction> transactionList = new ArrayList<>();
    public walletFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment walletFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static walletFragment newInstance(String param1, String param2) {
        walletFragment fragment = new walletFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla el layout del fragmento primero
        View rootView = inflater.inflate(R.layout.fragment_wallet, container, false);

        // Inicializa el ViewModel
        studentViewModel = new ViewModelProvider(this).get(StudentViewModel.class);

        // Obtén los argumentos si están disponibles
        if (getArguments() != null) {
            user = getArguments().getString("user");
            Log.d("WalletFragment", "Received user: " + user);
            // Ahora usa rootView para encontrar las vistas dentro del layout inflado
            loadInfo(rootView);
        } else {
            Log.e("WalletFragment", "No arguments received.");
        }

        // Configura el RecyclerView
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TransactionAdapter(transactionList);
        recyclerView.setAdapter(adapter);

        // Obtén el saldo
        if (user != null) {
            fetchTransactions(user);
            BalanceRepository.obtenerSaldoAsync(user, new BalanceCallback() {
                @Override
                public void onSuccess(BalanceResponse saldoResponse) {
                    getActivity().runOnUiThread(() -> {
                        TextView balanceTView = rootView.findViewById(R.id.balance);
                        balanceTView.setText("S./" + saldoResponse.getSaldo());
                    });
                }

                @Override
                public void onError(Exception e) {
                    Log.e("WalletFragment", "Error al obtener el saldo", e);
                }
            });
        } else {
            Toast.makeText(getContext(), "User ID is missing", Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }

    public void loadInfo(View view){
        // Verifica que studentViewModel no sea nulo
        if (studentViewModel != null) {
            studentViewModel.loadStudent(user);
            studentViewModel.getStudentLiveData().observe(getViewLifecycleOwner(), new Observer<Student>() {
                @Override
                public void onChanged(Student student) {
                    TextView userWallet = view.findViewById(R.id.user);
                    userWallet.setText(student.getUsuario());
                    TextView fullNameWallet = view.findViewById(R.id.fullNames);
                    fullNameWallet.setText(student.getNombre() + " " + student.getApellido());

                }
            });
        } else {
            Log.e("WalletFragment", "studentViewModel is null.");
        }
    }

    private void fetchTransactions(String user) {
        StudentRepository studentRepository = new StudentRepository();
        studentRepository.obtenerTransacciones(user, new StudentRepositoryCallback() {
            @Override
            public void onTransactionsLoaded(List<Transaction> transactions) {
                transactionList.clear();
                transactionList.addAll(transactions);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error loading transactions: " + error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
                // Not used in this context
            }

            @Override
            public void onStudentLoaded(Student student) {
                // Not used in this context
            }

            @Override
            public void onStudentCreated(String user) {
                // Not used in this context
            }
        });
    }




}

