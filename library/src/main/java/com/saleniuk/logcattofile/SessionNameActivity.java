package com.saleniuk.logcattofile;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;

import com.afollestad.materialdialogs.MaterialDialog;

public class SessionNameActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.ThemeTransparent);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_name);
        getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);

        new MaterialDialog.Builder(this)
                .title(R.string.log_session_prefix)
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL)
                .input("", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        LogcatToFile.startLogs(SessionNameActivity.this, input.toString());
                        dialog.dismiss();
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                })
                .show();
    }
}

