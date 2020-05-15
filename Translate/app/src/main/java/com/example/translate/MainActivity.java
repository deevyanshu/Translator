package com.example.translate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SPEECH_INPUT =1000 ;
    private EditText txt;
    private TextView txt2;
private ImageButton ibtn;
private ImageButton clear;
private ImageButton translate;
private String sourcetext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt=(EditText) findViewById(R.id.textview);
        txt2=(TextView) findViewById(R.id.textview2);
        ibtn=(ImageButton)findViewById(R.id.imagebutton);

        clear=(ImageButton)findViewById(R.id.iclear);
        translate=(ImageButton)findViewById(R.id.itranslate);


        ibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt.setText("");
                txt2.setText("");
            }
        });
        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
identify();

            }
        });
    }


    private void speak()
    {
        Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"hi speak something");
        try{
            startActivityForResult(intent,REQUEST_CODE_SPEECH_INPUT);
        }catch (Exception e)
        {
            Toast.makeText(this,""+e.getMessage(),Toast.LENGTH_SHORT).show();}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_SPEECH_INPUT:{
                if(resultCode==RESULT_OK&&null!=data)
                {
                    ArrayList<String> result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txt.setText(result.get(0));
                }
                break;
            }
        }
    }

    private  void identify()
    {
      sourcetext=txt.getText().toString();
        FirebaseLanguageIdentification identifier= FirebaseNaturalLanguage.getInstance().getLanguageIdentification();
        identifier.identifyLanguage(sourcetext).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
           if(s.equals("und"))
           {
             Toast.makeText(getApplicationContext(),"language not identified",Toast.LENGTH_SHORT).show();
           }
           else
           {
               getLanguage(s);
           }
            }
        });
    }

private void getLanguage(String language)
{
    int langcode;
    switch (language)
    {
        case "en":
            langcode= FirebaseTranslateLanguage.EN;
            break;
        case "ar":
            langcode=FirebaseTranslateLanguage.AR;
            break;
        case "ur":
            langcode=FirebaseTranslateLanguage.DE;
            default:
                langcode=0;
    }
    translateText(langcode);
}
private void translateText(int langcode)
{
    FirebaseTranslatorOptions options =
            new FirebaseTranslatorOptions.Builder()
                    .setSourceLanguage(langcode)
                    .setTargetLanguage(FirebaseTranslateLanguage.HI)
                    .build();
    final FirebaseTranslator translator=FirebaseNaturalLanguage.getInstance().getTranslator(options);

    FirebaseModelDownloadConditions conditions= new FirebaseModelDownloadConditions.Builder().build();
    translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
          translator.translate(sourcetext).addOnSuccessListener(new OnSuccessListener<String>() {
              @Override
              public void onSuccess(String s) {
                  txt2.setText(s);
              }
          })  ;
        }
    });
}
}
