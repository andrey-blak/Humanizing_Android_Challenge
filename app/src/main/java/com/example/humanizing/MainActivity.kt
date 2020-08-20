package com.example.humanizing

import android.os.Bundle
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.`object`.conversation.AutonomousReactionImportance
import com.aldebaran.qi.sdk.`object`.conversation.AutonomousReactionValidity
import com.aldebaran.qi.sdk.`object`.conversation.Bookmark
import com.aldebaran.qi.sdk.`object`.conversation.QiChatbot
import com.aldebaran.qi.sdk.builder.ChatBuilder
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder
import com.aldebaran.qi.sdk.builder.TopicBuilder
import com.aldebaran.qi.sdk.design.activity.RobotActivity

class MainActivity : RobotActivity() {

	private lateinit var robotLifecycleCallbacks: RobotLifecycleCallbacks

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
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

	/**
	 * Starts the chat and shows the welcoming message as the robot gaines the focus.
	 */
	private fun startChatBot(qiContext: QiContext) {
		val topic = TopicBuilder.with(qiContext)
			.withResource(R.raw.welcome)
			.build()
		val chatBot = QiChatbotBuilder.with(qiContext)
			.withTopic(topic)
			.build()
		val chat = ChatBuilder.with(qiContext)
			.withChatbot(chatBot)
			.build()

		val welcomingBookmark = topic.bookmarks[WELCOMING_BOOKMARK]
		// go to the "welcoming" bookmark to show the message
		chat.addOnStartedListener {
			showWelcomingMessage(chatBot, welcomingBookmark)
		}
		chat.run()
	}

	private fun showWelcomingMessage(chatBot: QiChatbot, welcomingBookmark: Bookmark?) {
		chatBot.goToBookmark(welcomingBookmark, AutonomousReactionImportance.HIGH, AutonomousReactionValidity.IMMEDIATE)
	}

	companion object {

		private const val WELCOMING_BOOKMARK = "welcoming"
	}
}
