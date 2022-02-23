package org.medida.inhalerdetection;
//package com.google.codelab.mlkit;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptions;
//import com.google.codelab.mlkit.GraphicOverlay.Graphic;
import java.util.ArrayList;
import java.util.List;

public class TextRecognitionActivity extends AppCompatActivity {

    //SOFIA
    //private GraphicOverlay mGraphicOverlay;
    private String originalText;
    private String cleanText;
    private String admissibilidade;
    ArrayList<Integer> listFive = new ArrayList<Integer>();
    private String inhalertype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void recognizeText(Bitmap mSelectedImage, Button inhalertype) {

        InputImage image = InputImage.fromBitmap(mSelectedImage, 0);
        // [START get_detector_default]
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        // [END get_detector_default]

        // [START run_detector]
        Task<Text> result =
                recognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {
                                //SOFIA:
                                //texto detetado
                                originalText = processTextRecognitionResult(visionText);
                                //texto limpo
                                cleanText = processTextRecognitionResult(visionText).replaceAll("[\\D]", "");
                                //System.out.println(cleanText);
                                //intervalo admissivel
                                admissibilidade = intervaloAdmissivel(inhalertype, cleanText);
                                if(admissibilidade == "admissivel")
                                    listFive.add(Integer.valueOf(cleanText));
                                //System.out.println(listFive);
                                //guardar resultados
                                //saveText(String.valueOf(System.currentTimeMillis()/1000), inalador, nomeImagem, originalText, cleanText, admissibilidade, listFive.size(), mostCommonDetected);
                                //saveText(String.valueOf(System.currentTimeMillis() / 1000), inalador, nomeImagem, originalText);


                                /*
                                // Task completed successfully
                                // [START_EXCLUDE]
                                // [START get_text]
                                for (Text.TextBlock block : visionText.getTextBlocks()) {
                                    Rect boundingBox = block.getBoundingBox();
                                    Point[] cornerPoints = block.getCornerPoints();
                                    String text = block.getText();

                                    for (Text.Line line: block.getLines()) {
                                        // ...
                                        for (Text.Element element: line.getElements()) {
                                            // ...
                                        }
                                    }
                                }
                                // [END get_text]
                                // [END_EXCLUDE]*/
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        //SOFIA
                                        e.printStackTrace();
                                    }
                                });
        // [END run_detector]
    }

    //SOFIA
    private String processTextRecognitionResult(Text texts) {
        List<Text.TextBlock> blocks = texts.getTextBlocks();
        String textoDetetado = "";
        //mGraphicOverlay.clear();
        if (blocks.size() > 0) {

            for (int i = 0; i < blocks.size(); i++) {
                List<Text.Line> lines = blocks.get(i).getLines();
                //System.out.println(lines);
                for (int j = 0; j < lines.size(); j++) {
                    List<Text.Element> elements = lines.get(j).getElements();

                    for (int k = 0; k < elements.size(); k++) {
                        //Graphic textGraphic = new TextGraphic(mGraphicOverlay, elements.get(k));
                        //mGraphicOverlay.add(textGraphic);
                        System.out.println("texto detetado" + elements.get(k).getText() + "<");
                        textoDetetado += elements.get(k).getText();
                        //escreveDetectado(elements.get(k).getText());

                    }
                }
            }
        } else {
            textoDetetado = "9999";
        }

        //Log.d("Texto " + textoDetetado);
        return textoDetetado;

    }

    /*
    private void processTextBlock(Text result) {
        // [START mlkit_process_text_block]
        String resultText = result.getText();
        for (Text.TextBlock block : result.getTextBlocks()) {
            String blockText = block.getText();
            Point[] blockCornerPoints = block.getCornerPoints();
            Rect blockFrame = block.getBoundingBox();
            for (Text.Line line : block.getLines()) {
                String lineText = line.getText();
                Point[] lineCornerPoints = line.getCornerPoints();
                Rect lineFrame = line.getBoundingBox();
                for (Text.Element element : line.getElements()) {
                    String elementText = element.getText();
                    Point[] elementCornerPoints = element.getCornerPoints();
                    Rect elementFrame = element.getBoundingBox();
                }
            }
        }
        // [END mlkit_process_text_block]
    }*/

    private TextRecognizer getTextRecognizer() {
        // [START mlkit_local_doc_recognizer]
        TextRecognizer detector = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        // [END mlkit_local_doc_recognizer]

        return detector;
    }

    //SOFIA:
    public static String intervaloAdmissivel(Button inhalertype, String textoDetetado){

        String admissibilidade;
        int doseMin = 0;
        int doseMax;

        if ("diskusButton".equals(inhalertype)) {
            doseMax = 60;
        } else if ("elliptaButton".equals(inhalertype)) {
            doseMax = 30;
        } else if ("flutiformButton".equals(inhalertype)) {
            doseMax = 120;
        } else if ("novolizerButton".equals(inhalertype)) {
            doseMax = 200;
        } else if ("spiromaxButton".equals(inhalertype)) {//doseMax = 120;
            doseMax = 4446;
        } else if ("turbohalerButton".equals(inhalertype)) {//doseMax = 120;
            doseMax = 2040;
        } else {
            throw new IllegalStateException("Unexpected value: " + inhalertype);
        }

        //System.out.println(">" + textoDetetado + "<");

        //verificar se se encontra no intervalo
        //while(textoDetetado != "") {
        if(textoDetetado == "" || textoDetetado.isEmpty()){
            admissibilidade = "inadmissivel por campo vazio"; //detetou texto mas não os digitos
        }else if(Integer.valueOf(textoDetetado) <= doseMax && Integer.valueOf(textoDetetado) > doseMin && Integer.valueOf(textoDetetado) != 9999) {
            admissibilidade = "admissivel"; // encontrou texto e encontra-se dentro do intervalo de admissibilidde
        } else if (Integer.valueOf(textoDetetado) >= doseMax && Integer.valueOf(textoDetetado) != 9999) {
            admissibilidade = "inadmissivel por excesso"; // o texto encontrado não se encontra no intervalo de admissibilidade
        } else if (Integer.valueOf(textoDetetado) == 9999) {
            admissibilidade = "inadmissivel por defeito"; // não existia texto nas imagens
        } else {
            admissibilidade = "inadmissivel"; // inadmissivel por outras razões
        }
        //}

        return admissibilidade;

    }

}