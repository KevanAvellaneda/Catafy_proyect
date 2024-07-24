package com.example.winestastic;

import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;


public class privacidad_activity extends ScrollingActivity {

    private ImageView toolbar_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        title = "Términos y Privacidad";
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_privacidad);

        /* Change Icon of top_background
        toolbar_icon = findViewById(R.id.toolbar_icon);
        toolbar_icon.setImageResource(R.mipmap.ic_launcher_foreground);
        toolbar_icon.setScaleType(ImageView.ScaleType.FIT_XY);*/

        //incrustar activity
        NestedScrollView nscrollv;
        nscrollv = findViewById(R.id.nestedScrollView);
        LayoutInflater inflater = getLayoutInflater();
        View myLayout = inflater.inflate(R.layout.activity_privacidad, nscrollv, false);
        nscrollv.removeAllViews();
        nscrollv.addView(myLayout);

        // Obtener el texto de privacidad del archivo de recursos y aplicar formato HTML
        TextView priv1TextView = findViewById(R.id.priv1TextView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            priv1TextView.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }
        // Obtener el texto de privacidad del archivo de recursos y aplicar formato HTML
        String privacidadText = getString(R.string.aviso_de_privacidad);
        priv1TextView.setText(Html.fromHtml(privacidadText));

        // Obtener y aplicar formato HTML al texto de términos y condiciones
        TextView termsTextView = findViewById(R.id.termsTextView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            termsTextView.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }
        String termsText = getString(R.string.terminos_y_condiciones);
        termsTextView.setText(Html.fromHtml(termsText));

    }

    private boolean handleBackPressed = false;

    @Override
    public void onBackPressed() {
        if (handleBackPressed) {
            super.onBackPressed();  // Llama a super.onBackPressed() solo si handleBackPressed es true
        } else {
            // Establece el resultado como RESULT_OK y finaliza la actividad
            setResult(RESULT_OK);
            finish();
        }
    }
}