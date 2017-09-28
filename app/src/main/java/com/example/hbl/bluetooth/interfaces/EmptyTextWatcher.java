package com.example.hbl.bluetooth.interfaces;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by hbl on 2017/9/25.
 */

public class EmptyTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
