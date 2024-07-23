package com.zentoodevs.login.fragments;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.zentoodevs.login.R;
import com.google.common.util.concurrent.ListenableFuture;
import com.zentoodevs.login.activities.MainActivity;
import com.zentoodevs.login.activities.MainActivity2;
import com.zentoodevs.login.repository.models.Message;
import com.zentoodevs.login.repository.models   .MessageAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ChatAIFragment extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private EditText editTextMessage;
    private Button buttonSend;
    private ChatFutures chat;
    private Executor executor;
    private static final String API_KEY = "AIzaSyBGGXiJeCIJISfnmtGVYrMPod5HjaLonr4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_ai);
        FunB();
        recyclerView = findViewById(R.id.recycler_view_messages);
        initChat();


    }

    private void FunB(){
        Button btnBack = findViewById(R.id.backButton);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initChat(){
        RecyclerView recyclerView = findViewById(R.id.recycler_view_messages);
        editTextMessage = findViewById(R.id.edit_text_message);
        buttonSend = findViewById(R.id.button_send);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = editTextMessage.getText().toString().trim();
                if (!messageText.isEmpty()) {
                    Message message = new Message("user", messageText);
                    messageAdapter.addMessage(message);
                    editTextMessage.setText("");
                    recyclerView.smoothScrollToPosition(messageList.size() - 1);
                    initModelAI();
                }
            }
        });
    }

    private void initModelAI(){
        executor = Executors.newSingleThreadExecutor();
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", API_KEY);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        // Crear historial previo si es necesario
        Content.Builder userContentBuilder = new Content.Builder();
        userContentBuilder.setRole("user");
        userContentBuilder.addText("Hola, en este chat vas a intentar guiarme de acuerdo a direcciones y lugares a los que quiero ir, a" +
                                    "demás hablaremos español en todo momento, Vas a tener que intentar conseguir de mi la dirección de " +
                                    "partida y la dirección de destino(Todas las direcciones y lugares se encuentran en Peru)" +
                                    "solo y solo cuando consigas las dos direcciones, damelas en este formato" +
                                    ": Ubicación de partida: //XXXXX//, Ubicación de destino: //XXXXX// (es importante que pongas esos signos de //)" +
                                    ", además los destinos damelos en formato de dirección detallada o como lo daria en google maps, sino puedes saber " +
                                    "la direccion pide mas detalles" +
                                    "DEBES VERIFICAR QUE SEAN DIRECCIONES REALES" +
                                    "Puedes hacer uso de emojis para que la experiencia del chat sea mas interactiva");
        Content userContent = userContentBuilder.build();

        Content.Builder modelContentBuilder = new Content.Builder();
        modelContentBuilder.setRole("model");
        modelContentBuilder.addText("Muy bien!, a donde te gustaría ir hoy?");
        Content modelContent = modelContentBuilder.build();
        List<Content> history = Arrays.asList(userContent, modelContent);

        // Inicializar el chat con historial
        chat = model.startChat(history);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMessageText = editTextMessage.getText().toString();
                if (!userMessageText.isEmpty()) {
                    // Añadir el mensaje del usuario al RecyclerView
                    Message userMessage = new Message(userMessageText, "user");
                    messageAdapter.addMessage(userMessage);
                    recyclerView.scrollToPosition(messageList.size() - 1);
                    editTextMessage.setText("");

                    // Crear y enviar el mensaje al modelo
                    Content.Builder userMessageC = new Content.Builder();
                    userMessageC.setRole("user");
                    userMessageC.addText(userMessageText);
                    Content userMessageCM = userMessageC.build();
                    ListenableFuture<GenerateContentResponse> response = chat.sendMessage(userMessageCM);
                    // Manejar la respuesta del modelo
                    Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                        @Override
                        public void onSuccess(GenerateContentResponse result) {
                            String resultText = result.getText();
                            // Añadir la respuesta del modelo al RecyclerView
                            runOnUiThread(() -> {
                                Message modelMessage = new Message(resultText, "model");
                                messageAdapter.addMessage(modelMessage);
                                searchLocationInMessage(modelMessage.getContent());
                                recyclerView.scrollToPosition(messageList.size() - 1);
                            });
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            t.printStackTrace();
                        }
                    }, executor);
                }
            }
        });
    }

    public void searchLocationInMessage(String input) {
        int counterMatch = 0;
        String startLocation = null;
        String endLocation = null;
        Pattern pattern = Pattern.compile("//(.*?)//");
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            if (counterMatch == 0) {
                startLocation = matcher.group(1);
            } else if (counterMatch == 1) {
                endLocation = matcher.group(1);
            }
            counterMatch++;
        }
        Log.d("StartEnd", startLocation + endLocation);
        if (counterMatch > 1 && startLocation != null && endLocation != null) {
            launchMapFragment(startLocation, endLocation);
        }
    }

    private void launchMapFragment(String startLocation, String endLocation) {
        Intent intent = new Intent(this, MainActivity2.class);
        intent.putExtra("startLocation", startLocation);
        intent.putExtra("endLocation", endLocation);
        Intent intento = getIntent();
        String user = intento.getStringExtra("user");
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }




}



