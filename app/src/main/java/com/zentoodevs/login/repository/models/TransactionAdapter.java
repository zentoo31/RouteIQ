package com.zentoodevs.login.repository.models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.zentoodevs.login.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private final List<Transaction> transactionList;

    public TransactionAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.nameBus.setText(transaction.getBus());
        holder.date.setText(formatDate(transaction.getFechaTransaccion()));
        holder.mount.setText("S./" + transaction.getMonto());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView nameBus, date, mount;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            nameBus = itemView.findViewById(R.id.NameBus);
            date = itemView.findViewById(R.id.date);
            mount = itemView.findViewById(R.id.mount);
        }
    }

    public String formatDate(String dateString) {
        if (dateString == null) {
            System.out.println("La cadena de fecha es nula");
            return ""; // o devuelve un valor por defecto adecuado para tu caso
        }

        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH);
        Date date = null;
        try {
            // Parsear la fecha
            date = inputFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("Error al parsear la fecha: " + e.getMessage());
            return ""; // o devuelve un valor por defecto adecuado para tu caso
        }

        // Verificar si date no es null antes de formatearlo
        if (date != null) {
            // Formatear la fecha en el nuevo formato deseado
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm EEE dd yyyy", new Locale("es", "ES"));
            return outputFormat.format(date);
        } else {
            System.out.println("No se pudo parsear la fecha correctamente");
            return ""; // o devuelve un valor por defecto adecuado para tu caso
        }
    }




}
