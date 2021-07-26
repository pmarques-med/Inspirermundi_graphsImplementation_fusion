package com.bloomidea.inspirers;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

class IntegerValueFormatter extends ValueFormatter
{
    private DecimalFormat mFormat;

    public IntegerValueFormatter() {
        mFormat = new DecimalFormat("###,###,##0");
    }



    @Override
    public String getFormattedValue(float value) {
        return mFormat.format(value);
    }
}