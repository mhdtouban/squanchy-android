package net.squanchy.support.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_event_details.view.*
import net.squanchy.R
import net.squanchy.imageloader.ImageLoader
import net.squanchy.imageloader.imageLoaderComponent
import net.squanchy.speaker.domain.view.Speaker
import net.squanchy.support.kotlin.children
import net.squanchy.support.unwrapToActivityContext

abstract class SpeakerView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private lateinit var imageLoader: ImageLoader
    private lateinit var speakerPhotoContainer: ViewGroup
    val layoutInflater = LayoutInflater.from(context)

    init {
        if (!isInEditMode) {
            val activity = unwrapToActivityContext(context)
            imageLoader = imageLoaderComponent(activity).imageLoader()
        }
        super.setOrientation(LinearLayout.VERTICAL)
    }

    override fun setOrientation(orientation: Int): Nothing {
        throw UnsupportedOperationException("SpeakerView doesn't support changing orientation")
    }

    fun updateWith(speakers: List<Speaker>, listener: OnSpeakerClickListener?) {
        speakerName.text = toCommaSeparatedNames(speakers)
        updateSpeakerPhotos(speakers, listener)
    }

    private fun toCommaSeparatedNames(speakers: List<Speaker>) = speakers.joinToString(", ") { it.name }

    private fun updateSpeakerPhotos(speakers: List<Speaker>, listener: OnSpeakerClickListener?) {
        if (!::imageLoader.isInitialized) {
            throw IllegalStateException("Unable to access the ImageLoader, it hasn't been initialized yet")
        }

        val photoViews: List<ImageView>
        if (speakerPhotoContainer.childCount > 0) {
            photoViews = getAllImageViewsContainedIn(speakerPhotoContainer)
            speakerPhotoContainer.removeAllViews()
        } else {
            photoViews = mutableListOf()
        }

        for (speaker in speakers) {
            val photoView = recycleOrInflatePhotoView(photoViews)
            speakerPhotoContainer.addView(photoView)
            setClickListenerOrNotClickable(photoView, listener, speaker)

            if (speaker.photoUrl.isPresent) {
                loadSpeakerPhoto(photoView, speaker.photoUrl.get(), imageLoader)
            } else {
                photoView.setImageResource(R.drawable.ic_no_avatar)
            }
        }
    }

    private fun getAllImageViewsContainedIn(container: ViewGroup): MutableList<ImageView> {
        return container.children
            .map { it as ImageView }
            .toMutableList()
    }

    private fun setClickListenerOrNotClickable(photoView: ImageView, listener: OnSpeakerClickListener?, speaker: Speaker) {
        if (listener != null) {
            photoView.setOnClickListener { listener.onSpeakerClicked(speaker) }
            photoView.isClickable = true
        } else {
            photoView.setOnClickListener(null)
            photoView.isClickable = false
        }
    }

    private fun recycleOrInflatePhotoView(photoViews: MutableList<ImageView>): ImageView {
        return if (photoViews.isEmpty()) {
            inflatePhotoView(speakerPhotoContainer)
        } else {
            photoViews.removeAt(0)
        }
    }

    protected abstract fun inflatePhotoView(speakerPhotoContainer: ViewGroup): ImageView

    private fun loadSpeakerPhoto(photoView: ImageView, photoUrl: String, imageLoader: ImageLoader) {
        photoView.setImageDrawable(null)
        imageLoader.load(photoUrl)
            .into(photoView)
    }

    interface OnSpeakerClickListener {

        fun onSpeakerClicked(speaker: Speaker)
    }
}