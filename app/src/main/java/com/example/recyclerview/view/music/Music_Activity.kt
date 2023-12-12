package com.example.recyclerview.view.music

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.example.recyclerview.R

class music_Activity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var seekBar: SeekBar
    private var isPaused = false
    private var pausedPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)

        val audioId = intent.getIntExtra("shadow", -1)
        mediaPlayer = MediaPlayer.create(this, audioId)

        val playButton: Button = findViewById(R.id.btn_play_audio)

        playButton.setOnClickListener {
            if (!mediaPlayer.isPlaying) {
                if (isPaused) {
                    // Jika sedang di-pause, lanjutkan dari posisi terakhir
                    mediaPlayer.seekTo(pausedPosition)
                    mediaPlayer.start()
                    isPaused = false
                } else {
                    // Jika baru dimulai, mulai dari awal
                    mediaPlayer.start()
                }
                playButton.text = "Pause Audio"
                setupSeekBar()
            } else {
                // Jika sedang berjalan, pause dan simpan posisi
                mediaPlayer.pause()
                playButton.text = "Play Audio"
                isPaused = true
                pausedPosition = mediaPlayer.currentPosition
            }
        }
    }

    private fun setupSeekBar() {
        // Inisialisasi seekBar di sini jika belum diinisialisasi
        if (!::seekBar.isInitialized) {
            seekBar = findViewById(R.id.seekBar)
            seekBar.max = mediaPlayer.duration

            // Handler untuk pembaruan periodik pada UI
            val handler = Handler(Looper.getMainLooper())

            // Runnable untuk pembaruan periodik SeekBar
            val updateSeekBar = object : Runnable {
                override fun run() {
                    try {
                        if (mediaPlayer != null && mediaPlayer.isPlaying) {
                            runOnUiThread {
                                seekBar.progress = mediaPlayer.currentPosition
                            }
                        }
                        handler.postDelayed(this, 100) // Pembaruan setiap 100 milidetik
                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                        // Tangani IllegalStateException yang mungkin terjadi saat MediaPlayer dilepaskan
                    }
                }
            }

            // Jalankan pembaruan pertama
            handler.post(updateSeekBar)

            // SeekBar listener for manual seek
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        try {
                            mediaPlayer.seekTo(progress)
                        } catch (e: IllegalStateException) {
                            e.printStackTrace()
                            // Tangani IllegalStateException yang mungkin terjadi saat MediaPlayer dilepaskan
                        }
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}
