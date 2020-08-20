package com.example.humanizing

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.`object`.conversation.AutonomousReactionImportance
import com.aldebaran.qi.sdk.`object`.conversation.AutonomousReactionValidity
import com.aldebaran.qi.sdk.`object`.conversation.Bookmark
import com.aldebaran.qi.sdk.`object`.conversation.QiChatbot
import com.aldebaran.qi.sdk.`object`.conversation.Topic
import com.aldebaran.qi.sdk.builder.AnimateBuilder
import com.aldebaran.qi.sdk.builder.AnimationBuilder
import com.aldebaran.qi.sdk.builder.ChatBuilder
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder
import com.aldebaran.qi.sdk.builder.TopicBuilder
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import com.example.humanizing.databinding.ActivityMainBinding
import com.example.jokes.JokesDataSource
import com.example.jokes.dto.Joke
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class MainActivity : RobotActivity() {

	private lateinit var robotLifecycleCallbacks: RobotLifecycleCallbacks
	private lateinit var viewBinding: ActivityMainBinding
	private var chatbot: QiChatbot? = null
	private var topic: Topic? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		viewBinding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(viewBinding.root)
		registerRobotCallback()
	}

	override fun onDestroy() {
		unregisterRobotCallback()
		super.onDestroy()
	}

	private fun registerRobotCallback() {
		robotLifecycleCallbacks = object : RobotLifecycleCallbacks {
			override fun onRobotFocusGained(qiContext: QiContext) {
				startChatBot(qiContext)
				viewBinding.initListeners(qiContext)
			}

			override fun onRobotFocusLost() {
			}

			override fun onRobotFocusRefused(reason: String?) {
			}
		}
		QiSDK.register(this, robotLifecycleCallbacks)
	}

	private fun unregisterRobotCallback() {
		QiSDK.unregister(this, robotLifecycleCallbacks)
	}

	private fun ActivityMainBinding.initListeners(qiContext: QiContext) {
		buttonA.setOnClickListener {
			startBowAnimation(qiContext)
		}
		buttonB.setOnClickListener {
			getJoke()
		}
	}

	/**
	 * Starts the chat and shows the welcoming message as the robot gaines the focus.
	 */
	private fun startChatBot(qiContext: QiContext) {
		val topic = TopicBuilder.with(qiContext)
			.withResource(R.raw.welcome)
			.build()
		this.topic = topic
		val chatbot = QiChatbotBuilder.with(qiContext)
			.withTopic(topic)
			.build()
		this.chatbot = chatbot
		val chat = ChatBuilder.with(qiContext)
			.withChatbot(chatbot)
			.build()

		// go to the "welcoming" bookmark to show the message
		chat.addOnStartedListener {
			showWelcomingMessage()
		}
		chat.async().run()
	}

	/**
	 * Starts a bow animation. A goodbye message will be shown whe the animation is started. A message will be logged into Logcat when the animation stops.
	 */
	private fun startBowAnimation(qiContext: QiContext) {
		val animation = AnimationBuilder.with(qiContext)
			.withResources(R.raw.bowing_b001)
			.buildAsync()
		animation.andThenConsume { animation ->
			val animate = AnimateBuilder.with(qiContext)
				.withAnimation(animation)
				.build()
			animate.addOnStartedListener {
				showGoodbyeMessage()
			}
			val animateFuture = animate.async().run()

			animateFuture.thenConsume {
				Log.d(LOG_TAG, "Bow animation stopped.")
			}
		}
	}

	/**
	 * Requests a joke and shows it's content.
	 */
	private fun getJoke() {
		val exceptionHandler = CoroutineExceptionHandler { _, e ->
			showError()
		}
		lifecycleScope.launch(exceptionHandler) {
			val jokesSource = JokesDataSource()
			val joke = jokesSource.getJoke()
			showJoke(joke)
		}
	}

	/**
	 * Shows the message bookmarked as "welcoming".
	 */
	private fun showWelcomingMessage() {
		val welcomingBookmark = findBookmark(WELCOMING_BOOKMARK)
		goToBookmark(welcomingBookmark)
	}

	/**
	 * Shows the message bookmarked as "goodbye".
	 */
	private fun showGoodbyeMessage() {
		val bookmark = findBookmark(GOODBYE_BOOKMARK)
		goToBookmark(bookmark)
	}

	/**
	 * Tries to find a bookmark named as [name] in the topic.
	 */
	private fun findBookmark(name: String): Bookmark? {
		return topic?.bookmarks?.get(name)
	}

	/**
	 * Goes to the [bookmark]
	 */
	private fun goToBookmark(bookmark: Bookmark?) {
		chatbot?.async()?.goToBookmark(bookmark, AutonomousReactionImportance.HIGH, AutonomousReactionValidity.IMMEDIATE)
	}

	/**
	 * Shows the [joke]'s content if it's not null, an error message otherwise.
	 */
	private fun showJoke(joke: Joke?) {
		if (joke == null) {
			showError()
			return
		}

		val message = joke.content
		viewBinding.jokeEditText.setText(message)
	}

	/**
	 * Shows the default error message.
	 */
	private fun showError() {
		viewBinding.jokeEditText.setText(R.string.error)
	}

	companion object {

		private const val WELCOMING_BOOKMARK = "welcoming"
		private const val GOODBYE_BOOKMARK = "goodbye"
		private const val LOG_TAG = "Humanizing"
	}
}
