package com.applaudotest.projectcjab.functions;

import android.content.Context;
import android.graphics.Typeface;



public class Fonts {
    public Typeface MuseoRegular;
    public Typeface ArialBold;
    public Typeface Arial;


    public Fonts(Context contexto){
        try {
            MuseoRegular = Typeface.createFromAsset(contexto.getAssets(), "fonts/MuseoRegular.ttf");
            ArialBold = Typeface.createFromAsset(contexto.getAssets(), "fonts/ArialBold.ttf");
            Arial = Typeface.createFromAsset(contexto.getAssets(), "fonts/Arial.ttf");


        } catch (Exception e) {
        }
    }
}

