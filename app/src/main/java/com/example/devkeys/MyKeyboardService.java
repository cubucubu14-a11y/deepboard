package com.example.devkeys;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.inputmethodservice.InputMethodService;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

public class MyKeyboardService extends InputMethodService {
    private boolean isSelectMode = false;
    private Handler repeatHandler = new Handler(Looper.getMainLooper());
    private Runnable repeatRunnable;
    private LinearLayout layerShortcut, layerAlpha, layerSymbol;
    private View mainKeyboardView;
    private PopupWindow settingsPopup;

    @Override
    public View onCreateInputView() {
        mainKeyboardView = getLayoutInflater().inflate(R.layout.keyboard_view, null);

        SharedPreferences prefs = getSharedPreferences("DeepBoardPrefs", MODE_PRIVATE);
        int savedHeight = prefs.getInt("key_height", 46);
        int savedFont = prefs.getInt("key_font", 16);
        applyCustomSize(mainKeyboardView, savedHeight, savedFont);

        layerShortcut = mainKeyboardView.findViewById(R.id.layerShortcut);
        layerAlpha = mainKeyboardView.findViewById(R.id.layerAlpha);
        layerSymbol = mainKeyboardView.findViewById(R.id.layerSymbol);

        // --- LOGIKA PINDAH LAYER ---
        View btnToAlpha1 = mainKeyboardView.findViewById(R.id.btnToAlpha);
        if (btnToAlpha1 != null) btnToAlpha1.setOnClickListener(v -> switchLayer("alpha"));

        View btnToAlpha2 = mainKeyboardView.findViewById(R.id.btnToAlphaFromSymbol);
        if (btnToAlpha2 != null) btnToAlpha2.setOnClickListener(v -> switchLayer("alpha"));

        View btnToSymbol = mainKeyboardView.findViewById(R.id.btnToSymbol);
        if (btnToSymbol != null) btnToSymbol.setOnClickListener(v -> switchLayer("symbol"));

        View btnToShortcut = mainKeyboardView.findViewById(R.id.btnToShortcut);
        if (btnToShortcut != null) btnToShortcut.setOnClickListener(v -> switchLayer("shortcut"));

        View btnToShortcutSymbol = mainKeyboardView.findViewById(R.id.btnToShortcutSymbol);
        if (btnToShortcutSymbol != null) btnToShortcutSymbol.setOnClickListener(v -> switchLayer("shortcut"));

        // --- LAYER 1: SHORTCUT ---
        setupCtrlBtn(mainKeyboardView, R.id.btnSelAll, KeyEvent.KEYCODE_A);
        setupCtrlBtn(mainKeyboardView, R.id.btnCopy, KeyEvent.KEYCODE_C);
        setupCtrlBtn(mainKeyboardView, R.id.btnPaste, KeyEvent.KEYCODE_V);
        setupCtrlBtn(mainKeyboardView, R.id.btnFind, KeyEvent.KEYCODE_F);
        setupCtrlBtn(mainKeyboardView, R.id.btnUndo, KeyEvent.KEYCODE_Z);
        setupCtrlBtn(mainKeyboardView, R.id.btnRedo, KeyEvent.KEYCODE_Y);
        setupKeyBtn(mainKeyboardView, R.id.btnTab, KeyEvent.KEYCODE_TAB);
        setupKeyBtn(mainKeyboardView, R.id.btnEnter, KeyEvent.KEYCODE_ENTER);

        Button btnSelect = mainKeyboardView.findViewById(R.id.btnSelect);
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

        setupRepeat(mainKeyboardView.findViewById(R.id.btnDel), KeyEvent.KEYCODE_DEL);
        setupRepeat(mainKeyboardView.findViewById(R.id.btnLeft), KeyEvent.KEYCODE_DPAD_LEFT);
        setupRepeat(mainKeyboardView.findViewById(R.id.btnRight), KeyEvent.KEYCODE_DPAD_RIGHT);
        setupRepeat(mainKeyboardView.findViewById(R.id.btnUp), KeyEvent.KEYCODE_DPAD_UP);
        setupRepeat(mainKeyboardView.findViewById(R.id.btnDown), KeyEvent.KEYCODE_DPAD_DOWN);

        // --- LAYER 2: HURUF & LONG PRESS ---
        setupLongPress(mainKeyboardView.findViewById(R.id.btnQ), "q", "1");
        setupLongPress(mainKeyboardView.findViewById(R.id.btnW), "w", "2");
        setupLongPress(mainKeyboardView.findViewById(R.id.btnE), "e", "3");
        setupLongPress(mainKeyboardView.findViewById(R.id.btnR), "r", "4");
        setupLongPress(mainKeyboardView.findViewById(R.id.btnT), "t", "5");
        setupLongPress(mainKeyboardView.findViewById(R.id.btnY), "y", "6");
        setupLongPress(mainKeyboardView.findViewById(R.id.btnU), "u", "7");
        setupLongPress(mainKeyboardView.findViewById(R.id.btnI), "i", "8");
        setupLongPress(mainKeyboardView.findViewById(R.id.btnO), "o", "9");
        setupLongPress(mainKeyboardView.findViewById(R.id.btnP), "p", "0");

        setupLongPress(mainKeyboardView.findViewById(R.id.btnA), "a", "@");
        setupLongPress(mainKeyboardView.findViewById(R.id.btnS), "s", "#");
        setupLongPress(mainKeyboardView.findViewById(R.id.btnD), "d", "$");
        setupLongPress(mainKeyboardView.findViewById(R.id.btnF), "f", "%");
        setupLongPress(mainKeyboardView.findViewById(R.id.btnG), "g", "&");
        setupLongPress(mainKeyboardView.findViewById(R.id.btnH), "h", "-");
        setupLongPress(mainKeyboardView.findViewById(R.id.btnJ), "j", "+");
        setupLongPress(mainKeyboardView.findViewById(R.id.btnK), "k", "*");
        setupLongPress(mainKeyboardView.findViewById(R.id.btnL), "l", "/");

        setupTextBtn(mainKeyboardView, R.id.btnZ, "z"); setupTextBtn(mainKeyboardView, R.id.btnX, "x");
        setupTextBtn(mainKeyboardView, R.id.btnC, "c"); setupTextBtn(mainKeyboardView, R.id.btnV, "v");
        setupTextBtn(mainKeyboardView, R.id.btnB, "b"); setupTextBtn(mainKeyboardView, R.id.btnN, "n");
        setupTextBtn(mainKeyboardView, R.id.btnM, "m");
        
        // SPASI DENGAN FITUR TEKAN LAMA UNTUK PENGATURAN
        setupSpaceSettings(mainKeyboardView.findViewById(R.id.btnSpasi));

        setupTextBtn(mainKeyboardView, R.id.btnKoma, ","); setupTextBtn(mainKeyboardView, R.id.btnTitik, ".");
        setupKeyBtn(mainKeyboardView, R.id.btnDelLetter, KeyEvent.KEYCODE_DEL);

        // --- LAYER 3: SIMBOL ---
        int[] numIds = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9};
        String[] numVals = {"0","1","2","3","4","5","6","7","8","9"};
        for (int i = 0; i < numIds.length; i++) {
            final String val = numVals[i];
            View b = mainKeyboardView.findViewById(numIds[i]);
            if (b != null) b.setOnClickListener(v -> sendText(val));
        }
        
        setupTextBtn(mainKeyboardView, R.id.btnBukaKurawal, "{");
        setupTextBtn(mainKeyboardView, R.id.btnTutupKurawal, "}");
        setupTextBtn(mainKeyboardView, R.id.btnBukaKurung, "(");
        setupTextBtn(mainKeyboardView, R.id.btnTutupKurung, ")");
        setupTextBtn(mainKeyboardView, R.id.btnBukaSiku, "[");
        setupTextBtn(mainKeyboardView, R.id.btnTutupSiku, "]");
        setupTextBtn(mainKeyboardView, R.id.btnKurang, "<");
        setupTextBtn(mainKeyboardView, R.id.btnLebih, ">");
        setupTextBtn(mainKeyboardView, R.id.btnSamaDengan, "=");
        setupTextBtn(mainKeyboardView, R.id.btnTambah, "+");
        
        setupTextBtn(mainKeyboardView, R.id.btnStrip, "_");
        setupTextBtn(mainKeyboardView, R.id.btnPetik2, "\"");
        setupTextBtn(mainKeyboardView, R.id.btnPetik1, "'");
        setupTextBtn(mainKeyboardView, R.id.btnTitikKoma, ";");
        setupTextBtn(mainKeyboardView, R.id.btnTitikDua, ":");
        setupTextBtn(mainKeyboardView, R.id.btnSpasiSymbol, " ");

        return mainKeyboardView;
    }

    private void setupSpaceSettings(View spasiBtn) {
        if (spasiBtn == null) return;

        spasiBtn.setOnTouchListener(new View.OnTouchListener() {
            private long timeDown;
            private boolean isLongPressed = false;
            private final Handler handler = new Handler(Looper.getMainLooper());
            private final Runnable runnable = () -> {
                isLongPressed = true;
                showSettingsPopup();
                spasiBtn.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setPressed(true);
                        timeDown = System.currentTimeMillis();
                        isLongPressed = false;
                        handler.postDelayed(runnable, 500); // Tahan 500ms
                        return true;
                    case MotionEvent.ACTION_UP:
                        handler.removeCallbacks(runnable);
                        v.setPressed(false);
                        if (!isLongPressed && (System.currentTimeMillis() - timeDown) < 500) {
                            sendText(" "); 
                        }
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                        handler.removeCallbacks(runnable);
                        v.setPressed(false);
                        return true;
                }
                return false;
            }
        });
    }

    // --- FUNGSI POPUP WINDOW (ANTI CRASH) ---
    private void showSettingsPopup() {
        if (settingsPopup != null && settingsPopup.isShowing()) return;

        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_settings, null);
        
        // Focusable = true agar slider bisa digeser
        settingsPopup = new PopupWindow(
                popupView, 
                ViewGroup.LayoutParams.WRAP_CONTENT, 
                ViewGroup.LayoutParams.WRAP_CONTENT, 
                true); 

        settingsPopup.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        settingsPopup.setOutsideTouchable(false); // Wajib pencet tombol TUTUP untuk keluar

        SharedPreferences prefs = getSharedPreferences("DeepBoardPrefs", MODE_PRIVATE);
        
        TextView tvHeightLabel = popupView.findViewById(R.id.tvHeightLabel);
        SeekBar seekHeight = popupView.findViewById(R.id.seekHeight);
        TextView tvFontLabel = popupView.findViewById(R.id.tvFontLabel);
        SeekBar seekFont = popupView.findViewById(R.id.seekFont);
        Button btnClose = popupView.findViewById(R.id.btnClosePopup);

        int currentHeight = prefs.getInt("key_height", 46);
        tvHeightLabel.setText("Tinggi Tombol: " + currentHeight + "dp");
        seekHeight.setMax(30);
        seekHeight.setProgress(currentHeight - 30);
        seekHeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int val = progress + 30;
                tvHeightLabel.setText("Tinggi Tombol: " + val + "dp");
                prefs.edit().putInt("key_height", val).apply();
                if(mainKeyboardView != null) applyCustomSize(mainKeyboardView, val, prefs.getInt("key_font", 16));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        int currentFont = prefs.getInt("key_font", 16);
        tvFontLabel.setText("Ukuran Huruf: " + currentFont + "sp");
        seekFont.setMax(12);
        seekFont.setProgress(currentFont - 12);
        seekFont.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int val = progress + 12;
                tvFontLabel.setText("Ukuran Huruf: " + val + "sp");
                prefs.edit().putInt("key_font", val).apply();
                if(mainKeyboardView != null) applyCustomSize(mainKeyboardView, prefs.getInt("key_height", 46), val);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnClose.setOnClickListener(v -> {
            if (settingsPopup != null) {
                settingsPopup.dismiss();
                settingsPopup = null;
            }
        });

        // Tampilkan di tengah layar
        settingsPopup.showAtLocation(mainKeyboardView, Gravity.CENTER, 0, 0);
    }

    private void applyCustomSize(View view, int heightDp, int fontSp) {
        float density = getResources().getDisplayMetrics().density;
        int heightPx = (int) (heightDp * density);
        
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyCustomSize(group.getChildAt(i), heightDp, fontSp);
            }
        } else if (view instanceof Button) {
            Button btn = (Button) view;
            ViewGroup.LayoutParams params = btn.getLayoutParams();
            if (params != null) {
                params.height = heightPx;
                btn.setLayoutParams(params);
            }
            btn.setTextSize(fontSp);
        }
    }

    private void switchLayer(String target) {
        layerShortcut.setVisibility(View.GONE);
        layerAlpha.setVisibility(View.GONE);
        layerSymbol.setVisibility(View.GONE);

        if (target.equals("alpha")) {
            layerAlpha.setVisibility(View.VISIBLE);
        } else if (target.equals("symbol")) {
            layerSymbol.setVisibility(View.VISIBLE);
        } else {
            layerShortcut.setVisibility(View.VISIBLE);
        }
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
            private long timeDown;
            private boolean isLongPressed = false;
            private final Handler handler = new Handler(Looper.getMainLooper());
            private final Runnable runnable = () -> {
                isLongPressed = true;
                sendText(subChar);
                button.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setPressed(true);
                        timeDown = System.currentTimeMillis();
                        isLongPressed = false;
                        handler.postDelayed(runnable, 350); // Tahan 350ms
                        return true;
                    case MotionEvent.ACTION_UP:
                        handler.removeCallbacks(runnable);
                        v.setPressed(false);
                        if (!isLongPressed && (System.currentTimeMillis() - timeDown) < 350) {
                            sendText(mainChar);
                        }
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                        handler.removeCallbacks(runnable);
                        v.setPressed(false);
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

    private void sendCtrlKey(int keyCode) {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            if (isSelectMode && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || 
                                 keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || 
                                 keyCode == KeyEvent.KEYCODE_DPAD_UP || 
                                 keyCode == KeyEvent.KEYCODE_DPAD_DOWN)) {
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SHIFT_LEFT));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, keyCode));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT));
                return;
            }

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
