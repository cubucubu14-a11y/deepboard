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
        View btnToAlpha1 = view.findViewById(R.id.btnToAlpha);
        if (btnToAlpha1 != null) {
            btnToAlpha1.setOnClickListener(v -> {
                layerShortcut.setVisibility(View.GONE);
                layerSymbol.setVisibility(View.GONE);
                layerAlpha.setVisibility(View.VISIBLE);
            });
        }

        View btnToAlpha2 = view.findViewById(R.id.btnToAlphaFromSymbol);
        if (btnToAlpha2 != null) {
            btnToAlpha2.setOnClickListener(v -> {
                layerShortcut.setVisibility(View.GONE);
                layerSymbol.setVisibility(View.GONE);
                layerAlpha.setVisibility(View.VISIBLE);
            });
        }

        View btnToSymbol = view.findViewById(R.id.btnToSymbol);
        if (btnToSymbol != null) {
            btnToSymbol.setOnClickListener(v -> {
                layerShortcut.setVisibility(View.GONE);
                layerAlpha.setVisibility(View.GONE);
                layerSymbol.setVisibility(View.VISIBLE);
            });
        }

        View btnToShortcut = view.findViewById(R.id.btnToShortcut);
        if (btnToShortcut != null) {
            btnToShortcut.setOnClickListener(v -> {
                layerAlpha.setVisibility(View.GONE);
                layerSymbol.setVisibility(View.GONE);
                layerShortcut.setVisibility(View.VISIBLE);
            });
        }

        View btnToShortcutSymbol = view.findViewById(R.id.btnToShortcutSymbol);
        if (btnToShortcutSymbol != null) {
            btnToShortcutSymbol.setOnClickListener(v -> {
                layerAlpha.setVisibility(View.GONE);
                layerSymbol.setVisibility(View.GONE);
                layerShortcut.setVisibility(View.VISIBLE);
            });
        }

        // --- LAYER 1: SHORTCUT ---
        setupTextBtn(view, R.id.btnSelAll, null);
        setupCtrlBtn(view, R.id.btnSelAll, KeyEvent.KEYCODE_A);
        setupCtrlBtn(view, R.id.btnCopy, KeyEvent.KEYCODE_C);
        setupCtrlBtn(view, R.id.btnPaste, KeyEvent.KEYCODE_V);
        setupCtrlBtn(view, R.id.btnFind, KeyEvent.KEYCODE_F);
        setupCtrlBtn(view, R.id.btnUndo, KeyEvent.KEYCODE_Z);
        setupCtrlBtn(view, R.id.btnRedo, KeyEvent.KEYCODE_Y);
        setupKeyBtn(view, R.id.btnTab, KeyEvent.KEYCODE_TAB);
        setupKeyBtn(view, R.id.btnEnter, KeyEvent.KEYCODE_ENTER);

        Button btnSelect = view.findViewById(R.id.btnSelect);
        if (btnSelect != null) {
            btnSelect.setOnClickListener(v -> {
                isSelectMode = !isSelectMode;
                if (isSelectMode) {
                    btnSelect.setBackgroundResource(R.drawable.bg_btn_blue); 
                    btnSelect.setTextColor(0xFFFFFFFF);
                } else {
                    btnSelect.setBackgroundResource(R.drawable.bg_btn_grey);
                    btnSelect.setTextColor(0xFFAAAAAA);
                }
            });
        }

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
        setupKeyBtn(view, R.id.btnDelLetter, KeyEvent.KEYCODE_DEL);

        // --- LAYER 3: SIMBOL ---
        int[] numIds = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9};
        String[] numVals = {"0","1","2","3","4","5","6","7","8","9"};
        for (int i = 0; i < numIds.length; i++) {
            final String val = numVals[i];
            View b = view.findViewById(numIds[i]);
            if (b != null) b.setOnClickListener(v -> sendText(val));
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

    private void setupTextBtn(View view, int id, String text) {
        View v = view.findViewById(id);
        if (v != null && text != null) {
            v.setOnClickListener(click -> sendText(text));
        }
    }

    private void setupCtrlBtn(View view, int id, int keyCode) {
        View v = view.findViewById(id);
        if (v != null) {
            v.setOnClickListener(click -> sendCtrlKey(keyCode));
        }
    }

    private void setupKeyBtn(View view, int id, int keyCode) {
        View v = view.findViewById(id);
        if (v != null) {
            v.setOnClickListener(click -> sendKey(keyCode));
        }
    }

    private void setupLongPress(View button, String mainChar, String subChar) {
        if (button == null) return;

        button.setOnTouchListener(new View.OnTouchListener() {
            private Handler handler = new Handler(Looper.getMainLooper());
            private Runnable runnable;
            private boolean isLongPressed = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    isLongPressed = false;
                    v.setPressed(true);
                    runnable = () -> {
                        isLongPressed = true;
                        sendText(subChar); 
                        v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
                    };
                    handler.postDelayed(runnable, 400);
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    handler.removeCallbacks(runnable);
                    v.setPressed(false);
                    if (!isLongPressed) {
                        sendText(mainChar);
                    }
                    return true;
                }
                return false;
            }
        });
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

    // --- LOGIKA UTAMA YANG DIRUBAH ---
    private void sendCtrlKey(int keyCode) {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            // JIKA MODE SELECT AKTIF DAN YANG DITEKAN ADALAH PANAH
            if (isSelectMode && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || 
                                 keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || 
                                 keyCode == KeyEvent.KEYCODE_DPAD_UP || 
                                 keyCode == KeyEvent.KEYCODE_DPAD_DOWN)) {
                
                // Trik: Kirim sinyal Shift Ditekan -> Panah Ditekan -> Panah Dilepas -> Shift Dilepas
                // Ini lebih ampuh untuk aplikasi catatan standar (EditText)
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SHIFT_LEFT));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, keyCode));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT));
                return;
            }

            // Logika Normal (Ctrl + Huruf untuk Copy/Paste/Select All)
            int metaState = KeyEvent.META_CTRL_ON;
            int ctrlKeyCode = KeyEvent.KEYCODE_CTRL_LEFT;
            ic.sendKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, ctrlKeyCode, 0, metaState));
            ic.sendKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, keyCode, 0, metaState));
            ic.sendKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP, keyCode, 0, metaState));
            ic.sendKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP, ctrlKeyCode, 0, 0));
        }
    }

    private void setupRepeat(View button, int keyCode) {
        if (button == null) return;

        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setPressed(true);
                        
                        // Panggil fungsi yang menangani Select Mode
                        if (isSelectMode && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || 
                                             keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || 
                                             keyCode == KeyEvent.KEYCODE_DPAD_UP || 
                                             keyCode == KeyEvent.KEYCODE_DPAD_DOWN)) {
                            sendCtrlKey(keyCode); 
                        } else {
                            sendKey(keyCode);
                        }
                        
                        repeatRunnable = new Runnable() {
                            @Override
                            public void run() {
                                if (isSelectMode && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || 
                                                     keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || 
                                                     keyCode == KeyEvent.KEYCODE_DPAD_UP || 
                                                     keyCode == KeyEvent.KEYCODE_DPAD_DOWN)) {
                                    sendCtrlKey(keyCode); 
                                } else {
                                    sendKey(keyCode);
                                }
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
