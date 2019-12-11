package com.car;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class Speech {
	public static String RECOGNITION_COMPLETE = "recognition_complete";
	private String TAG = "swallow.speech";

	private SpeechRecognizer recognizer;
	private Context context;

	Speech(Context context){
		//初始化识别引擎。由于华为手机内置科大讯飞服务，故此处直接调用该服务接口。
		ComponentName component = new ComponentName("com.iflytek.speechsuite", "com.iflytek.iatservice.SpeechService");
		recognizer = SpeechRecognizer.createSpeechRecognizer(context, component);
		recognizer.setRecognitionListener(new SpeechListener());

		this.context = context;
	}

	public void start(){
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.CHINESE.toString());
		recognizer.startListening(intent);
	}

	public void close(){
		recognizer.cancel();
		recognizer.destroy();
	}

	private class SpeechListener implements RecognitionListener {
		@Override
		public void onReadyForSpeech(Bundle params) { }

		@Override
		public void onBeginningOfSpeech() {
			Toast.makeText(context, "请开始", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onEndOfSpeech() {
			Toast.makeText(context, "请稍候", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onError(int error) {
			Toast.makeText(context, "语音识别出错", Toast.LENGTH_SHORT).show();
			switch(error){
				case SpeechRecognizer.ERROR_NETWORK:
				case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
					Log.e(TAG, "Network error.");
					break;
				case SpeechRecognizer.ERROR_AUDIO:
					Log.e(TAG, "Audio error.");
			}
		}

		@Override
		public void onResults(Bundle results) {
			ArrayList<String> strings = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
			if(strings!=null && strings.size()>0){
				Intent intent = new Intent(RECOGNITION_COMPLETE);
				intent.putExtra("result", strings.get(0));
				context.sendBroadcast(intent);
			}
		}

		@Override
		public void onPartialResults(Bundle bundle) { }
		@Override
		public void onEvent(int eventType, Bundle params) { }
		@Override
		public void onRmsChanged(float rmsdB) { }
		@Override
		public void onBufferReceived(byte[] buffer) { }
	}
}
