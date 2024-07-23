package com.zentoodevs.login.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.zentoodevs.login.repository.models.Student;
import com.zentoodevs.login.repository.StudentRepository;
import com.zentoodevs.login.repository.callbacks.StudentRepositoryCallback;
import com.zentoodevs.login.repository.models.Transaction;

import java.util.List;

public class StudentViewModel extends ViewModel {
    private final StudentRepository studentRepository;
    private final MutableLiveData<Student> studentLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public StudentViewModel() {
        studentRepository = new StudentRepository();
    }

    public void loadStudent(String xd) {
        studentRepository.getStudent(new StudentRepositoryCallback() {
            @Override
            public void onStudentLoaded(Student student) {
                studentLiveData.setValue(student);
            }

            @Override
            public void onError(String message) {
                errorLiveData.setValue(message);
            }

            @Override
            public void onStudentCreated(String user) {

            }

            @Override
            public void onSuccess() {

            }

            @Override
            public void onTransactionsLoaded(List<Transaction> transactions) {

            }
        }, xd);
    }

    public void addStudent(Student student){
        studentRepository.addStudent(student, new StudentRepositoryCallback() {
            @Override
            public void onStudentLoaded(Student student) {

            }

            @Override
            public void onError(String message) {

            }
            public void onStudentCreated(String user) {

            }

            @Override
            public void onSuccess() {

            }

            @Override
            public void onTransactionsLoaded(List<Transaction> transactions) {

            }
        });
    }


    public LiveData<Student> getStudentLiveData() {
        return studentLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }
}



