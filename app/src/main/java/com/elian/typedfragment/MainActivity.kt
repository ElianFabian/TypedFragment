package com.elian.typedfragment

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setContentView(R.layout.activity_main)

		val sampleFragment = SampleFragment.newInstance(
			args = SampleFragment.Args(
				title = "A regular title",
				message = "A regular message",
			)
		)

		sampleFragment.setFragmentEventListener(this@MainActivity) { event ->
			when (event) {
				is SampleFragment.Event.OnAccept -> {
					Toast.makeText(applicationContext, "Accepted! Data = ${event.data}", Toast.LENGTH_SHORT).show()
				}
				is SampleFragment.Event.OnReject -> {
					Toast.makeText(applicationContext, "Rejected!", Toast.LENGTH_SHORT).show()
				}
			}
		}

		supportFragmentManager.beginTransaction()
			.add(R.id.fragmentContainer, sampleFragment, sampleFragment.fragmentId)
			.commit()
	}
}