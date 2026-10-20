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
import android.widget.FrameLayout;
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
    private View btnToolbarShortcut;
    private PopupWindow settingsPopup;

    @Override
    public View onCreateInputView() {
        mainKeyboardView = getLayoutInflater().inflate(R.layout.keyboard_view, null);

        SharedPreferences prefs = getSharedPreferences("DeepBoardPrefs", MODE_PRIVATE);
        int savedHeight = prefs.getInt("key_height", 46);
        int savedFont = prefs.getInt("key_font", 18);
        int savedGap = prefs.getInt("key_gap", 4);
        applyCustomSize(mainKeyboardView, savedHeight, savedFont, savedGap);

        layerShortcut = mainKeyboardView.findViewById(R.id.layerShortcut);
        layerAlpha = mainKeyboardView.findViewById(R.id.layerAlpha);
        layerSymbol = mainKeyboardView.findViewById(R.id.layerSymbol);
        btnToolbarShortcut = mainKeyboardView.findViewById(R.id.btnToolbarShortcut);

        // --- TOOLBAR ATAS ---
        btnToolbarShortcut.setOnClickListener(v -> switchLayer("shortcut"));
        mainKeyboardView.findViewById(R.id.btnHideKeyboard).setOnClickListener(v -> requestHideSelf(0));

        // --- PINDAH LAYER ---
        View btnToAlpha1 = mainKeyboardView.findViewById(R.id.btnToAlpha);
        if (btnToAlpha1 != null) btnToAlpha1.setOnClickListener(v -> switchLayer("alpha"));

        View btnToAlpha2 = mainKeyboardView.findViewById(R.id.btnToAlphaFromSymbol);
        if (btnToAlpha2 != null) btnToAlpha2.setOnClickListener(v -> switchLayer("alpha"));

        View btnToSymbol = mainKeyboardView.findViewById(R.id.btnToSymbol);
        if (btnToSymbol != null) btnToSymbol.setOnClickListener(v -> switchLayer("symbol"));

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
        
        setupSpaceSettings(mainKeyboardView.findViewById(R.id.btnSpasi));

        setupTextBtn(mainKeyboardView, R.id.btnKoma, ",");
        setupTextBtn(mainKeyboardView, R.id.btnTitik, ".");
        setupKeyBtn(mainKeyboardView, R.id.btnDelLetter, KeyEvent.KEYCODE_DEL);
        setupKeyBtn(mainKeyboardView, R.id.btnEnterAlpha, KeyEvent.KEYCODE_ENTER);

        View btnShiftAlpha = mainKeyboardView.findViewById(R.id.btnShiftAlpha);
        if (btnShiftAlpha != null) {
            btnShiftAlpha.setOnClickListener(v -> {
                isSelectMode = !isSelectMode;
                if (isSelectMode) {
                    btnShiftAlpha.setBackgroundResource(R.drawable.bg_btn_blue);
                    btnShiftAlpha.setTextColor(0xFFFFFFFF);
                } else {
                    btnShiftAlpha.setBackgroundResource(R.drawable.bg_btn_grey);
                    btnShiftAlpha.setTextColor(0xFFE3E3E3);
                }
            });
        }

        // --- LAYER 3: SIMBOL ---
        int[] numIds = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9};
        String[] numVals = {"0","1","2","3","4","5","6","7","8","9"};
        for (int i = 0; i < numIds.length; i++) {
            final String val = numVals[i];
            View b = mainKeyboardView.findViewById(numIds[i]);
            if (b != null) b.setOnClickListener(v -> sendText(val));
        }
        
        setupTextBtn(mainKeyboardView, R.id.btnAt, "@"); setupTextBtn(mainKeyboardView, R.id.btnHash, "#");
        setupTextBtn(mainKeyboardView, R.id.btnDollar, "$"); setupTextBtn(mainKeyboardView, R.id.btnPercent, "%");
        setupTextBtn(mainKeyboardView, R.id.btnAmpersand, "&"); setupTextBtn(mainKeyboardView, R.id.btnStar, "*");
        setupTextBtn(mainKeyboardView, R.id.btnMinus, "-"); setupTextBtn(mainKeyboardView, R.id.btnPlus, "+");
        setupTextBtn(mainKeyboardView, R.id.btnBukaKurung, "("); setupTextBtn(mainKeyboardView, R.id.btnTutupKurung, ")");
        
        setupTextBtn(mainKeyboardView, R.id.btnBukaKurawal, "{"); setupTextBtn(mainKeyboardView, R.id.btnTutupKurawal, "}");
        setupTextBtn(mainKeyboardView, R.id.btnBukaSiku, "["); setupTextBtn(mainKeyboardView, R.id.btnTutupSiku, "]");
        setupTextBtn(mainKeyboardView, R.id.btnKurang, "<"); setupTextBtn(mainKeyboardView, R.id.btnLebih, ">");
        setupTextBtn(mainKeyboardView, R.id.btnSamaDengan, "="); setupTextBtn(mainKeyboardView, R.id.btnSlash, "/");
        setupTextBtn(mainKeyboardView, R.id.btnTanya, "?"); setupTextBtn(mainKeyboardView, R.id.btnSeru, "!");
        
        setupTextBtn(mainKeyboardView, R.id.btnStrip, "_"); setupTextBtn(mainKeyboardView, R.id.btnPetik2, "\"");
        setupTextBtn(mainKeyboardView, R.id.btnPetik1, "'"); setupTextBtn(mainKeyboardView, R.id.btnTitikKoma, ";");
        setupTextBtn(mainKeyboardView, R.id.btnTitikDua, ":"); setupTextBtn(mainKeyboardView, R.id.btnSpasiSymbol, " ");
        setupTextBtn(mainKeyboardView, R.id.btnPipe, "|"); setupTextBtn(mainKeyboardView, R.id.btnTilde, "~");
        setupTextBtn(mainKeyboardView, R.id.btnBacktick, "`");

        // Tampilkan Mode Shortcut saat pertama dibuka
        switchLayer("shortcut");

        return mainKeyboardView;
    }

    private void switchLayer(String target) {
        layerShortcut.setVisibility(View.GONE);
        layerAlpha.setVisibility(View.GONE);
        layerSymbol.setVisibility(View.GONE);

        if (target.equals("alpha")) {
            layerAlpha.setVisibility(View.VISIBLE);
            btnToolbarShortcut.setVisibility(View.VISIBLE);
        } else if (target.equals("symbol")) {
            layerSymbol.setVisibility(View.VISIBLE);
            btnToolbarShortcut.setVisibility(View.VISIBLE);
        } else {
            layerShortcut.setVisibility(View.VISIBLE);
            btnToolbarShortcut.setVisibility(View.INVISIBLE); // Hilang saat di Shortcut
        }
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

    private void showSettingsPopup() {
        if (settingsPopup != null && settingsPopup.isShowing()) return;

        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_settings, null);
        settingsPopup = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true); 
        settingsPopup.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        settingsPopup.setOutsideTouchable(false);

        SharedPreferences prefs = getSharedPreferences("DeepBoardPrefs", MODE_PRIVATE);
        
        TextView tvHeightLabel = popupView.findViewById(R.id.tvHeightLabel);
        SeekBar seekHeight = popupView.findViewById(R.id.seekHeight);
        TextView tvFontLabel = popupView.findViewById(R.id.tvFontLabel);
        SeekBar seekFont = popupView.findViewById(R.id.seekFont);
        TextView tvGapLabel = popupView.findViewById(R.id.tvGapLabel);
        SeekBar seekGap = popupView.findViewById(R.id.seekGap);
        Button btnClose = popupView.findViewById(R.id.btnClosePopup);

        int currentHeight = prefs.getInt("key_height", 46);
        tvHeightLabel.setText("Tinggi: " + currentHeight + "dp");
        seekHeight.setMax(30);
        seekHeight.setProgress(currentHeight - 30);
        seekHeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int val = progress + 30;
                tvHeightLabel.setText("Tinggi: " + val + "dp");
                prefs.edit().putInt("key_height", val).apply();
                if(mainKeyboardView != null) applyCustomSize(mainKeyboardView, val, prefs.getInt("key_font", 18), prefs.getInt("key_gap", 4));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        int currentFont = prefs.getInt("key_font", 18);
        tvFontLabel.setText("Font: " + currentFont + "sp");
        seekFont.setMax(12);
        seekFont.setProgress(currentFont - 12);
        seekFont.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int val = progress + 12;
                tvFontLabel.setText("Font: " + val + "sp");
                prefs.edit().putInt("key_font", val).apply();
                if(mainKeyboardView != null) applyCustomSize(mainKeyboardView, prefs.getInt("key_height", 46), val, prefs.getInt("key_gap", 4));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        int currentGap = prefs.getInt("key_gap", 4);
        tvGapLabel.setText("Jarak: " + currentGap + "dp");
        seekGap.setMax(10);
        seekGap.setProgress(currentGap - 2); // Min 2, Max 12
        seekGap.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int val = progress + 2;
                tvGapLabel.setText("Jarak: " + val + "dp");
                prefs.edit().putInt("key_gap", val).apply();
                if(mainKeyboardView != null) applyCustomSize(mainKeyboardView, prefs.getInt("key_height", 46), prefs.getInt("key_font", 18), val);
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

        settingsPopup.showAtLocation(mainKeyboardView, Gravity.CENTER, 0, 0);
    }

    private void applyCustomSize(View view, int heightDp, int fontSp, int gapDp) {
        float density = getResources().getDisplayMetrics().density;
        int heightPx = (int) (heightDp * density);
        int gapPx = (int) (gapDp * density);

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            // Atur margin untuk jarak antar tombol
            ViewGroup.LayoutParams params = group.getLayoutParams();
            if (params instanceof ViewGroup.MarginLayoutParams) {
                ((ViewGroup.MarginLayoutParams) params).setMargins(gapPx, gapPx, gapPx, gapPx);
                group.setLayoutParams(params);
            }
            for (int i = 0; i < group.getChildCount(); i++) {
                applyCustomSize(group.getChildAt(i), heightDp, fontSp, gapDp);
            }
        }

        if (view instanceof Button || view instanceof FrameLayout) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (params != null) {
                params.height = heightPx;
                view.setLayoutParams(params);
            }
        }

        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            if ("main_text".equals(tv.getTag())) {
                tv.setTextSize(fontSp);
            } else if ("sub_text".equals(tv.getTag())) {
                tv.setTextSize(Math.max(8, fontSp - 8)); 
            } else if (view instanceof Button) {
                tv.setTextSize(Math.max(10, fontSp - 4)); 
            }
        }
    }

    // Helper Functions
    private void setupTextBtn(View view, int id, String text) {
        View v = view.findViewById(id);
        if (v != null && text != null) v.setOnClickListener(click -> sendText(text));
    }

    private void setupCtrlBtn(View view, int id, int keyCode) {
        View v = view.findViewById(id);
        if (v != null) v.setOnClickListener(click -> sendCtrlKey(keyCode));
    }

    private void setupKeyBtn(View view, int id, int keyCode) {
        View v = view.findViewById(id);
        if (v != null) v.setOnClickListener(click -> sendKey(keyCode));
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
                        handler.postDelayed(runnable, 350); 
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
        if (ic != null) ic.commitText(text, 1);
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
            if (isSelectMode && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN)) {
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
                        if (isSelectMode && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN)) {
                            sendCtrlKey(keyCode); 
                        } else {
                            sendKey(keyCode);
                        }
                        repeatRunnable = new Runnable() {
                            @Override
                            public void run() {
                                if (isSelectMode && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN)) {
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
