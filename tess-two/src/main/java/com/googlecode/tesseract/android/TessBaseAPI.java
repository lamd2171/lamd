package com.googlecode.tesseract.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class TessBaseAPI {

    static {
        System.loadLibrary("tess");
    }

    // Tesseract 기본 경로
    private String dataPath;

    public TessBaseAPI() {
        dataPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tesseract/";
    }

    // Tesseract 초기화
    public void init(String language) {
        if (!initTesseract(dataPath, language)) {
            Log.e("TessBaseAPI", "Tesseract initialization failed.");
        }
    }

    // 언어 파일 로드
    private native boolean initTesseract(String dataPath, String language);

    // 이미지로부터 텍스트 추출
    public String getText(Bitmap bitmap) {
        return extractText(bitmap);
    }

    // 텍스트 추출하는 네이티브 메서드
    private native String extractText(Bitmap bitmap);

    // 트레이닝 데이터 파일 로드
    public void loadTrainedData() {
        File tessDataDir = new File(dataPath);
        if (!tessDataDir.exists()) {
            tessDataDir.mkdirs();
        }

        // 파일 로딩을 위한 기본 예시
        File trainedDataFile = new File(tessDataDir, "eng.traineddata");
        if (!trainedDataFile.exists()) {
            try {
                FileInputStream fis = new FileInputStream(trainedDataFile);
                // 데이터 파일을 Tesseract로 로드하는 방법 (예시)
            } catch (IOException e) {
                Log.e("TessBaseAPI", "Trained data file not found.", e);
            }
        }
    }

    // 정리 작업 (메모리 해제 등)
    public void end() {
        endTesseract();
    }

    // 네이티브 종료 메서드
    private native void endTesseract();
}
