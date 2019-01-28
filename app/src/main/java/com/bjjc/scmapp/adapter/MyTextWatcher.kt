package com.bjjc.scmapp.adapter

import android.text.Editable
import android.text.TextWatcher

/**
 * Created by Allen on 2019/01/24 9:42
 */
open class MyTextWatcher:TextWatcher {
    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}