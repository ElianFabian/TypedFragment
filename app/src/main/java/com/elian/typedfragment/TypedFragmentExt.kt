@file:Suppress("NOTHING_TO_INLINE")

package com.elian.typedfragment

import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner

/**
 * Retrieves the fragment arguments casted to the specified type [TArgs].
 *
 * @return The fragment arguments, or null if not found.
 */
@Suppress("Deprecation")
inline fun <T, TArgs : Parcelable> T.getFragmentArgs(): TArgs?
	where T : Fragment, T : TypedFragment<TArgs, *> {

	return arguments?.getParcelable(TypedFragment.EXTRA_ARGS)
}

/**
 * Creates a bundle containing the fragment arguments.
 *
 * @param args The arguments to be put in the bundle.
 * @return The created bundle.
 */
inline fun <T, TArgs : Parcelable> T.createArgsBundle(args: TArgs): Bundle
	where T : Fragment, T : TypedFragment<TArgs, *> {

	return bundleOf(TypedFragment.EXTRA_ARGS to args)
}


/**
 * Sends a fragment event to the parent fragment or activity.
 *
 * @param event The event to send.
 */
inline fun <T, TEvent : Parcelable> T.sendFragmentEvent(event: TEvent)
	where T : Fragment, T : TypedFragment<*, TEvent> {

	parentFragmentManager.setFragmentResult(fragmentId ?: return, bundleOf(TypedFragment.EXTRA_EVENT to event))
}


/**
 * Sets the event listener for the specified fragment manager and lifecycle owner, using a lambda to handle events.
 *
 * @param fragmentManager The fragment manager to set the event listener on.
 * @param lifecycleOwner The lifecycle owner (fragment or activity) associated with the event listener.
 * @param onEvent The lambda function to handle events.
 *
 * @throws IllegalArgumentException if the fragmentId is not set.
 */
@Suppress("Deprecation")
inline fun <T, TEvent : Parcelable> T.setFragmentEventListener(
	fragmentManager: FragmentManager,
	lifecycleOwner: LifecycleOwner,
	crossinline onEvent: (event: TEvent) -> Unit,
) where T : Fragment, T : TypedFragment<*, TEvent> {
	fragmentManager.setFragmentResultListener(
		fragmentId ?: return,
		lifecycleOwner
	) { _, bundle ->

		val event = bundle.getParcelable<TEvent>(TypedFragment.EXTRA_EVENT)

		onEvent(event ?: return@setFragmentResultListener)
	}
}

/**
 * Sets the event listener for the specified fragment, using a lambda to handle events.
 *
 * @param fragment The fragment to set the event listener on.
 * @param onEvent The lambda function to handle events.
 *
 * @throws IllegalArgumentException if the fragmentId is not set or if the provided fragment is the same as the current fragment.
 */
inline fun <T, TEvent : Parcelable> T.setFragmentEventListener(
	fragment: Fragment,
	crossinline onEvent: (event: TEvent) -> Unit,
) where T : Fragment, T : TypedFragment<*, TEvent> {

	require(fragment != this) {
		"Cannot set the event listener on the same fragment instance: ${fragment::class.qualifiedName}."
	}

	setFragmentEventListener(fragment.childFragmentManager, fragment.viewLifecycleOwner, onEvent)
}

/**
 * Sets the event listener for the specified activity, using a lambda to handle events.
 *
 * @param activity The activity to set the event listener on.
 * @param onEvent The lambda function to handle events.
 *
 * @throws IllegalArgumentException if the fragmentId is not set.
 */
inline fun <T, TEvent : Parcelable> T.setFragmentEventListener(
	activity: FragmentActivity,
	crossinline onEvent: (event: TEvent) -> Unit,
) where T : Fragment, T : TypedFragment<*, TEvent> {

	setFragmentEventListener(activity.supportFragmentManager, activity, onEvent)
}


/**
 * Clears the stored event.
 */
inline fun <T> T.clearFragmentEvent() where T : Fragment, T : TypedFragment<*, *> {

	parentFragmentManager.clearFragmentResult(fragmentId ?: return)
}

/**
 * Clears the fragment stored event listener.
 */
inline fun <T> T.clearFragmentEventListener() where T : Fragment, T : TypedFragment<*, *> {

	parentFragmentManager.clearFragmentResultListener(fragmentId ?: return)
}


/**
 * Shows the dialog fragment with optional args.
 *
 * @param fragmentManager The FragmentManager this fragment will be added to.
 * @param tag The tag for this fragment, as per [androidx.fragment.app.FragmentTransaction.add].
 * @param args The arguments that can passed to the dialog fragment.
 */
inline fun <T, TArgs : Parcelable> T.showDialog(
	fragmentManager: FragmentManager,
	tag: String,
	args: TArgs? = null,
) where T : DialogFragment, T : TypedFragment<TArgs, *> {

	if (args != null) {
		arguments = createArgsBundle(args)
	}

	show(fragmentManager, tag)
}

/**
 * Shows the dialog fragment with optional args.
 *
 * @param fragmentManager The FragmentManager this fragment will be added to.
 * @param args The arguments that can passed to the dialog fragment.
 */
inline fun <T, TArgs : Parcelable> T.showDialog(
	fragmentManager: FragmentManager,
	args: TArgs? = null,
) where T : DialogFragment, T : TypedFragment<TArgs, *> {

	showDialog(fragmentManager, fragmentId ?: return, args)
}

/**
 * Shows the dialog fragment with optional args.
 *
 * @param fragment The fragment from which the dialog fragment it's going to be shown.
 * @param args The arguments that can passed to the dialog fragment.
 */
inline fun <T, TArgs : Parcelable> T.showDialog(
	fragment: Fragment,
	args: TArgs? = null,
) where T : DialogFragment, T : TypedFragment<TArgs, *> {

	require(fragment != this) {
		"Cannot set event listener on the same dialog fragment instance: ${fragment::class.qualifiedName}."
	}

	showDialog(fragment.childFragmentManager, args)
}

/**
 * Shows the dialog fragment with optional args.
 *
 * @param activity The activity from which the dialog fragment it's going to be shown.
 * @param args The arguments that can passed to the dialog fragment.
 */
inline fun <T, TArgs : Parcelable> T.showDialog(
	activity: FragmentActivity,
	args: TArgs? = null,
) where T : DialogFragment, T : TypedFragment<TArgs, *> {

	showDialog(activity.supportFragmentManager, args)
}


/**
 * Dismisses the dialog fragment safely, even outside the dialog fragment itself.
 *
 * @param fragmentManager The fragment manager associated with the dialog fragment.
 */
inline fun <T> T.dismissDialog(
	fragmentManager: FragmentManager,
) where T : DialogFragment, T : TypedFragment<*, *> {

	kotlin.runCatching {
		val self = fragmentManager.findFragmentByTag(fragmentId) as? DialogFragment
		self?.dismiss()
	}
}

/**
 * Dismisses the dialog fragment safely, even outside the dialog fragment itself.
 *
 * @param fragment The fragment associated with the dialog fragment.
 */
inline fun <T> T.dismissDialog(
	fragment: Fragment,
) where T : DialogFragment, T : TypedFragment<*, *> {

	dismissDialog(fragment.childFragmentManager)
}

/**
 * Dismisses the dialog fragment safely, even outside the dialog fragment itself.
 *
 * @param activity The activity associated with the dialog fragment.
 */
inline fun <T> T.dismissDialog(
	activity: FragmentActivity,
) where T : DialogFragment, T : TypedFragment<*, *> {

	dismissDialog(activity.supportFragmentManager)
}