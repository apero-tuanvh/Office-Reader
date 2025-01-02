package com.apero.reader.office.macro;

public interface OnOpenFileListener {
    void onOpenFileSuccess();

    void onOpenFileFailure(String message);
}
