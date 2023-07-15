package com.elian.typedfragment

import android.os.Parcelable

/**
 * Interface for Fragment classes to provide a type-safe and process-death-safe way of receiving arguments and sending events.
 *
 * The interface requires indicating the types of arguments [TArgs] and events [TEvent] needed, as well as an ID to identify the fragment.
 *
 * This interface is intended to be extended with extension functions to fit specific needs.
 * Refer to the TypedFragmentExt file for some base functions that extends this interface.
 *
 *
 * Example usage:
 *
 * ```
 * class MyFragment : TypedFragment<MyFragment.Args, MyFragment.Event>() {
 *
 * 	[...]
 *
 * 	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *
 * 		val args = getFragmentArgs() ?: return
 *
 * 		tvTitle.text = args.title
 * 		tvMessage.text = args.message
 *
 * 		btnAccept.setOnClickListener {
 * 			sendFragmentEvent(Event.OnAccept(data = "important data"))
 * 		}
 * 		btnReject.setOnClickListener {
 * 			sendFragmentEvent(Event.OnReject)
 * 		}
 * 	}
 *
 *
 * 	@Parcelize
 * 	data class Args(
 * 		val title: String,
 * 		val message: Int,
 * 	) : Parcelable
 *
 * 	sealed interface Event : Parcelable {
 * 		@Parcelize
 * 		class OnAccept(val data: String) : Event
 *
 * 		@Parcelize
 * 		object OnReject : Event
 * 	}
 *
 *
 * 	companion object {
 * 		fun newInstance(id: String? = null, args: Args? = null) = MyFragment().apply {
 * 			if (args != null) {
 * 				arguments = createBundleFromDialogArgs(args)
 * 			}
 *
 * 			fragmentId = id ?: MyFragment::class.qualifiedName
 * 		}
 * 	}
 * }
 *
 *
 * // In another fragment or activity create a new instance
 * val myFragment = MyFragment.newInstance(
 * 	args = MyFragment.Args(
 * 		firstArg = "some data",
 * 		secondArg = "more data",
 * 	)
 * )
 *
 * // Set the listener
 * myFragment.setOnEventListener(this@SomeFragment) { event ->
 * 	when (event) {
 * 		is MyFragment.Event.OnAccept -> {
 * 			// Do something
 * 		}
 * 		is MyFragment.Event.OnReject -> {
 * 			// Do something
 * 		}
 * 	}
 * }
 * ```
 */
interface TypedFragment<TArgs : Parcelable, TEvent : Parcelable> {

	/**
	 * An ID that identifies a single instance of a Fragment.
	 *
	 * If there is only one instance, it's fine to use "this::class.qualifiedName" as the ID value. For multiple instances,
	 * provide different keys for each instance.
	 *
	 * To persist this ID after process death, you need to manually save and restore it. Example:
	 *
	 * ```
	 * override var fragmentId: String? = null
	 * 	private set
	 *
	 * override fun onCreate(savedInstanceState: Bundle?) {
	 * 	super.onCreate(savedInstanceState)
	 *
	 * 	fragmentId = fragmentId ?: savedInstanceState?.getString("id") ?: this::class.qualifiedName
	 * }
	 *
	 * @CallSuper
	 * override fun onSaveInstanceState(outState: Bundle) {
	 * 	super.onSaveInstanceState(outState)
	 *
	 * 	outState.putString("id", fragmentId)
	 * }
	 * ```
	 */
	val fragmentId: String?


	companion object {
		const val EXTRA_ARGS = "TypedFragment.EXTRA_ARGS"
		const val EXTRA_EVENT = "TypedFragment.EXTRA_EVENT"
	}
}