package com.zentoodevs.login.repository.callbacks;

import com.zentoodevs.login.repository.models.Student;
import com.zentoodevs.login.repository.models.Transaction;

import java.util.List;

public interface StudentRepositoryCallback {
    void onStudentLoaded(Student student);
    void onError(String message);
    void onStudentCreated(String user);
    void onSuccess();
    void onTransactionsLoaded(List<Transaction> transactions);

}
