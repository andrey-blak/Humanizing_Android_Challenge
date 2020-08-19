package com.example.humanizing

import android.os.Bundle
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
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
}
