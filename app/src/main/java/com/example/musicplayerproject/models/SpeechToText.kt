package com.example.musicplayerproject.models

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.ToggleButton


class SpeechToText(
    val  context: Context

): RecognitionListener {

    lateinit var textPlaceholder: EditText
    private var toggleButton: ToggleButton? = null
    private var progressBar: ProgressBar? = null
    private var speech: SpeechRecognizer? = null
    private var recognizerIntent: Intent? = null

    companion object
    {
        private var instances: SpeechToText? = null
        fun getInstances(context: Context, text: EditText): SpeechToText
        {
            if (instances == null)
            {
                instances = SpeechToText(context, text)
            }
            return instances!!
        }
    }

    constructor(context: Context, text: EditText) : this(context) {
        textPlaceholder = text
    }

    init {
        speech = SpeechRecognizer.createSpeechRecognizer(context)
        speech!!.setRecognitionListener(this)
        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent?.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,"en");
        recognizerIntent?.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,context.packageName);
        recognizerIntent?.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent?.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,context.packageName);
        //recognizerIntent?.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5000);
        //recognizerIntent?.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 5000);
        //recognizerIntent?.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 5000);
        recognizerIntent?.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
    }

    fun startListening()
    {
        speech?.startListening(recognizerIntent)
    }

    fun stopListening()
    {
        speech?.stopListening()
    }

    override fun onReadyForSpeech(p0: Bundle?) {

    }

    override fun onBeginningOfSpeech() {

    }

    override fun onRmsChanged(p0: Float) {

    }

    override fun onBufferReceived(p0: ByteArray?) {

    }

    override fun onEndOfSpeech() {

    }

    override fun onError(p0: Int) {

    }

    override fun onResults(p0: Bundle?) {
        Log.i(context.packageName + "Speech To Text: ", "onResults")
        val matches: ArrayList<String> =
            p0!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!!
        var text = ""
        for (result in matches) text += """
     $result
     
     """.trimIndent()

        textPlaceholder.setText(text.trimEnd())
    }

    override fun onPartialResults(p0: Bundle?) {

    }

    override fun onEvent(p0: Int, p1: Bundle?) {

    }

}