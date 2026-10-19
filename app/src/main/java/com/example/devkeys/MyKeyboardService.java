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
    private LinearLayout layerShortcut, layerAlpha, layerSymbol;

    @Override
    public View onCreateInputView() {
        View view = getLayoutInflater().inflate(R.layout.keyboard_view, null);

        layerShortcut = view.findViewById(R.id.layerShortcut);
        layerAlpha = view.findViewById(R.id.layerAlpha);
        layerSymbol = view.findViewById(R.id.layerSymbol);

        // --- LOGIKA PINDAH LAYER ---
        view.findViewById(R.id.btnToAlpha).setOnClickListener(v -> {
            layerShortcut.setVisibility(View.GONE);
            layerSymbol.setVisibility(View.GONE);
            layerAlpha.setVisibility(View.VISIBLE);
        });

        view.findViewById(R.id.btnToSymbol).setOnClickListener(v -> {
            layerShortcut.setVisibility(View.GONE);
            layerAlpha.setVisibility(View.GONE);
            layerSymbol.setVisibility(View.VISIBLE);
        });

        view.findViewById(R.id.btnToShortcut).setOnClickListener(v -> {
            layerAlpha.setVisibility(View.GONE);
            layerSymbol.setVisibility(View.GONE);
            layerShortcut.setVisibility(View.VISIBLE);
        });

        view.findViewById(R.id.btnToShortcutSymbol).setOnClickListener(v -> {
            layerAlpha.setVisibility(View.GONE);
            layerSymbol.setVisibility(View.GONE);
            layerShortcut.setVisibility(View.VISIBLE);
        });

        view.findViewById(R.id.btnToAlpha).setOnClickListener(v -> {
            layerShortcut.setVisibility(View.GONE);
            layerSymbol.setVisibility(View.GONE);
            layerAlpha.setVisibility(View.VISIBLE);
        });

        // --- LAYER 1: SHORTCUT ---
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
                btnSelect.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFF9800));
                btnSelect.setTextColor(0xFFFFFFFF);
            } else {
                btnSelect.setBackgroundResource(R.drawable.bg_btn_primary);
                btnSelect.setTextColor(0xFFAAAAAA);
            }
        });

        setupRepeat(view.findViewById(R.id.btnDel), KeyEvent.KEYCODE_DEL);
        setupRepeat(view.findViewById(R.id.btnLeft), KeyEvent.KEYCODE_DPAD_LEFT);
        setupRepeat(view.findViewById(R.id.btnRight), KeyEvent.KEYCODE_DPAD_RIGHT);
        setupRepeat(view.findViewById(R.id.btnUp), KeyEvent.KEYCODE_DPAD_UP);
        setupRepeat(view.findViewById(R.id.btnDown), KeyEvent.KEYCODE_DPAD_DOWN);

        // --- LAYER 2: HURUF & LONG PRESS ---
        setupLongPress(view.findViewById(R.id.btnQ), "q", "1");
        setupLongPress(view.findViewById(R.id.btnW), "w", "2");
        setupLongPress(view.findViewById(R.id.btnE), "e", "3");
        setupLongPress(view.findViewById(R.id.btnR), "r", "4");
        setupLongPress(view.findViewById(R.id.btnT), "t", "5");
        setupLongPress(view.findViewById(R.id.btnY), "y", "6");
        setupLongPress(view.findViewById(R.id.btnU), "u", "7");
        setupLongPress(view.findViewById(R.id.btnI), "i", "8");
        setupLongPress(view.findViewById(R.id.btnO), "o", "9");
        setupLongPress(view.findViewById(R.id.btnP), "p", "0");

        setupLongPress(view.findViewById(R.id.btnA), "a", "@");
        setupLongPress(view.findViewById(R.id.btnS), "s", "#");
        setupLongPress(view.findViewById(R.id.btnD), "d", "$");
        setupLongPress(view.findViewById(R.id.btnF), "f", "%");
        setupLongPress(view.findViewById(R.id.btnG), "g", "&");
        setupLongPress(view.findViewById(R.id.btnH), "h", "-");
        setupLongPress(view.findViewById(R.id.btnJ), "j", "+");
        setupLongPress(view.findViewById(R.id.btnK), "k", "*");
        setupLongPress(view.findViewById(R.id.btnL), "l", "/");

        setupTextBtn(view, R.id.btnZ, "z"); setupTextBtn(view, R.id.btnX, "x");
        setupTextBtn(view, R.id.btnC, "c"); setupTextBtn(view, R.id.btnV, "v");
        setupTextBtn(view, R.id.btnB, "b"); setupTextBtn(view, R.id.btnN, "n");
        setupTextBtn(view, R.id.btnM, "m"); setupTextBtn(view, R.id.btnSpasi, " ");
        setupTextBtn(view, R.id.btnKoma, ","); setupTextBtn(view, R.id.btnTitik, ".");
        view.findViewById(R.id.btnDelLetter).setOnClickListener(v -> sendKey(KeyEvent.KEYCODE_DEL));

        // --- LAYER 3: SIMBOL ---
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
        
        setupTextBtn(view, R.id.btnStrip, "_");
        setupTextBtn(view, R.id.btnPetik2, "\"");
        setupTextBtn(view, R.id.btnPetik1, "'");
        setupTextBtn(view, R.id.btnTitikKoma, ";");
        setupTextBtn(view, R.id.btnTitikDua, ":");
        setupTextBtn(view, R.id.btnSpasiSymbol, " ");

        return view;
    }

    // --- FUNGSI LONG PRESS (TEKAN LAMA) ---
    private void setupLongPress(View button, String mainChar, String subChar) {
        button.setOnTouchListener(new View.OnTouchListener() {
            private Handler handler = new Handler(Looper.getMainLooper());
            private Runnable runnable;
            private boolean isLongPressed = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    isLongPressed = false;
                    v.setPressed(true);
                    // Timer 400ms untuk mendeteksi tekan lama
                    runnable = () -> {
                        isLongPressed = true;
                        sendText(subChar); // Ketik simbol
                        v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
                    };
                    handler.postDelayed(runnable, 400);
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    handler.removeCallbacks(runnable);
                    v.setPressed(false);
                    if (!isLongPressed) {
                        sendText(mainChar); // Ketik huruf biasa jika tidak sampai 400ms
                    }
                    return true;
                }
                return false;
            }
        });
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
