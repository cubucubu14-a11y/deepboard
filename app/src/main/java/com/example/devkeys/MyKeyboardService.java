package com.example.devkeys;

import android.inputmethodservice.InputMethodService;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.LinearLayout;

public class MyKeyboardService extends InputMethodService {
    private boolean isSelectMode = false;
    private Handler repeatHandler = new Handler(Looper.getMainLooper());
    private Runnable repeatRunnable;
    private LinearLayout layerShortcut, layerAlpha;

    @Override
    public View onCreateInputView() {
        View view = getLayoutInflater().inflate(R.layout.keyboard_view, null);

        layerShortcut = view.findViewById(R.id.layerShortcut);
        layerAlpha = view.findViewById(R.id.layerAlpha);

        view.findViewById(R.id.btnToAlpha).setOnClickListener(v -> {
            layerShortcut.setVisibility(View.GONE);
            layerAlpha.setVisibility(View.VISIBLE);
        });

        view.findViewById(R.id.btnToShortcut).setOnClickListener(v -> {
            layerAlpha.setVisibility(View.GONE);
            layerShortcut.setVisibility(View.VISIBLE);
        });

        view.findViewById(R.id.btnSelAll).setOnClickListener(v -> sendCtrlKey(KeyEvent.KEYCODE_A));
        view.findViewById(R.id.btnCopy).setOnClickListener(v -> sendCtrlKey(KeyEvent.KEYCODE_C));
        view.findViewById(R.id.btnPaste).setOnClickListener(v -> sendCtrlKey(KeyEvent.KEYCODE_V));
        view.findViewById(R.id.btnFind).setOnClickListener(v -> sendCtrlKey(KeyEvent.KEYCODE_F));
        view.findViewById(R.id.btnUndo).setOnClickListener(v -> sendCtrlKey(KeyEvent.KEYCODE_Z));
        view.findViewById(R.id.btnRedo).setOnClickListener(v -> sendCtrlKey(KeyEvent.KEYCODE_Y));
        view.findViewById(R.id.btnTab).setOnClickListener(v -> sendKey(KeyEvent.KEYCODE_TAB));
        view.findViewById(R.id.btnEnter).setOnClickListener(v -> sendKey(KeyEvent.KEYCODE_ENTER));

        Button btnSelect = view.findViewById(R.id.btnSelect);
        btnSelect.setOnClickListener(v -> {
            isSelectMode = !isSelectMode;
            if (isSelectMode) {
                btnSelect.setBackgroundColor(0xFFFF9800);
                btnSelect.setTextColor(0xFFFFFFFF);
            } else {
                btnSelect.setBackgroundColor(0xFF444444);
                btnSelect.setTextColor(0xFFAAAAAA);
            }
        });

        setupRepeat(view.findViewById(R.id.btnDel), KeyEvent.KEYCODE_DEL);
        setupRepeat(view.findViewById(R.id.btnLeft), KeyEvent.KEYCODE_DPAD_LEFT);
        setupRepeat(view.findViewById(R.id.btnRight), KeyEvent.KEYCODE_DPAD_RIGHT);
        setupRepeat(view.findViewById(R.id.btnUp), KeyEvent.KEYCODE_DPAD_UP);
        setupRepeat(view.findViewById(R.id.btnDown), KeyEvent.KEYCODE_DPAD_DOWN);

        int[] numIds = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9};
        String[] numVals = {"0","1","2","3","4","5","6","7","8","9"};
        for (int i = 0; i < numIds.length; i++) {
            final String val = numVals[i];
            view.findViewById(numIds[i]).setOnClickListener(v -> sendText(val));
        }

        setupTextBtn(view, R.id.btnBukaKurawal, "{");
        setupTextBtn(view, R.id.btnTutupKurawal, "}");
        setupTextBtn(view, R.id.btnBukaKurung, "(");
        setupTextBtn(view, R.id.btnTutupKurung, ")");
        setupTextBtn(view, R.id.btnBukaSiku, "[");
        setupTextBtn(view, R.id.btnTutupSiku, "]");
        setupTextBtn(view, R.id.btnKurang, "<");
        setupTextBtn(view, R.id.btnLebih, ">");
        setupTextBtn(view, R.id.btnSamaDengan, "=");
        setupTextBtn(view, R.id.btnTambah, "+");

        setupTextBtn(view, R.id.btnQ, "q"); setupTextBtn(view, R.id.btnW, "w");
        setupTextBtn(view, R.id.btnE, "e"); setupTextBtn(view, R.id.btnR, "r");
        setupTextBtn(view, R.id.btnT, "t"); setupTextBtn(view, R.id.btnY, "y");
        setupTextBtn(view, R.id.btnU, "u"); setupTextBtn(view, R.id.btnI, "i");
        setupTextBtn(view, R.id.btnO, "o"); setupTextBtn(view, R.id.btnP, "p");
        
        setupTextBtn(view, R.id.btnA, "a"); setupTextBtn(view, R.id.btnS, "s");
        setupTextBtn(view, R.id.btnD, "d"); setupTextBtn(view, R.id.btnF, "f");
        setupTextBtn(view, R.id.btnG, "g"); setupTextBtn(view, R.id.btnH, "h");
        setupTextBtn(view, R.id.btnJ, "j"); setupTextBtn(view, R.id.btnK, "k");
        setupTextBtn(view, R.id.btnL, "l");
        
        setupTextBtn(view, R.id.btnZ, "z"); setupTextBtn(view, R.id.btnX, "x");
        setupTextBtn(view, R.id.btnC, "c"); setupTextBtn(view, R.id.btnV, "v");
        setupTextBtn(view, R.id.btnB, "b"); setupTextBtn(view, R.id.btnN, "n");
        setupTextBtn(view, R.id.btnM, "m"); setupTextBtn(view, R.id.btnSpasi, " ");

        return view;
    }

    private void setupTextBtn(View view, int id, String text) {
        view.findViewById(id).setOnClickListener(v -> sendText(text));
    }

    private void sendText(String text) {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.commitText(text, 1);
        }
    }

    private void sendKey(int keyCode) {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
            ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, keyCode));
        }
    }

    private void sendCtrlKey(int keyCode) {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            int metaState = isSelectMode ? KeyEvent.META_SHIFT_ON : KeyEvent.META_CTRL_ON;
            int mainKeyCode = keyCode;

            if (isSelectMode && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || 
                                 keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || 
                                 keyCode == KeyEvent.KEYCODE_DPAD_UP || 
                                 keyCode == KeyEvent.KEYCODE_DPAD_DOWN)) {
                ic.sendKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, mainKeyCode, 0, metaState));
                ic.sendKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP, mainKeyCode, 0, metaState));
                return;
            }

            int ctrlKeyCode = KeyEvent.KEYCODE_CTRL_LEFT;
            ic.sendKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, ctrlKeyCode, 0, metaState));
            ic.sendKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, mainKeyCode, 0, metaState));
            ic.sendKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP, mainKeyCode, 0, metaState));
            ic.sendKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP, ctrlKeyCode, 0, 0));
        }
    }

    private void setupRepeat(View button, int keyCode) {
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setPressed(true);
                        if (isSelectMode) sendCtrlKey(keyCode); else sendKey(keyCode);
                        
                        repeatRunnable = new Runnable() {
                            @Override
                            public void run() {
                                if (isSelectMode) sendCtrlKey(keyCode); else sendKey(keyCode);
                                repeatHandler.postDelayed(this, 50);
                            }
                        };
                        repeatHandler.postDelayed(repeatRunnable, 300);
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.setPressed(false);
                        repeatHandler.removeCallbacks(repeatRunnable);
                        return true;
                }
                return false;
            }
        });
    }
}
