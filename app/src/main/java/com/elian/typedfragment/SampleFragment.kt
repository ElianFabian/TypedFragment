package com.elian.typedfragment

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.elian.typedfragment.databinding.LayoutSampleBinding
import kotlinx.parcelize.Parcelize

class SampleFragment : Fragment(),
	TypedFragment<SampleFragment.Args, SampleFragment.Event> {

	private lateinit var binding: LayoutSampleBinding

	override var fragmentId: String? = null
		private set


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		fragmentId = fragmentId ?: savedInstanceState?.getString(EXTRA_FRAGMENT_ID) ?: this::class.qualifiedName
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)

		outState.putString(EXTRA_FRAGMENT_ID, fragmentId)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

		binding = LayoutSampleBinding.inflate(layoutInflater)

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val args = getFragmentArgs() ?: return

		binding.apply {

			tvTitle.text = args.title
			tvMessage.text = args.message

			btnAccept.setOnClickListener {
				sendFragmentEvent(
					Event.OnAccept(
						data = "This is your title: ${args.title} and this is your message = ${args.message}"
					)
				)
			}
			btnReject.setOnClickListener {
				sendFragmentEvent(Event.OnReject)
			}
		}
	}


	@Parcelize
	data class Args(
		val title: String,
		val message: String,
	) : Parcelable

	sealed interface Event : Parcelable {
		@Parcelize
		class OnAccept(val data: String) : Event

		@Parcelize
		object OnReject : Event
	}


	companion object {

		private const val EXTRA_FRAGMENT_ID = "EXTRA_FRAGMENT_ID"


		fun newInstance(id: String? = null, args: Args? = null) = SampleFragment().apply {
			if (args != null) {
				arguments = createArgsBundle(args)
			}

			this.fragmentId = id ?: SampleFragment::class.qualifiedName
		}
	}
}