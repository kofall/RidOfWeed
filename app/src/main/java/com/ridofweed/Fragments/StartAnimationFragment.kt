package com.ridofweed.Fragments

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import com.ridofweed.R

class StartAnimationFragment : Fragment() {
    private lateinit var mSceneView: View
    private lateinit var mTitleView: View
    private lateinit var mSunView: View
    private lateinit var mSkyView: View
    private var mBlueSkyColor: Int? = null
    private var mSunsetSkyColor: Int? = null
    private var mNightSkyColor: Int? = null
    private lateinit var mAndroidView: View
    private lateinit var mHeadView: View
    private lateinit var mBodyView: View
    private lateinit var mArmRightView: View
    private lateinit var mArmLeftView: View
    private lateinit var mGrass1: View
    private lateinit var mGrass2: View
    private lateinit var mGrass3: View
    private lateinit var mGrass4: View
    private lateinit var mGrass5: View
    private lateinit var mGrass6: View
    private lateinit var mScythe: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_android, container, false)
        mSceneView = view
        mTitleView = view.findViewById(R.id.title)

        mSunView = view.findViewById(R.id.sun)
        mSkyView = view.findViewById(R.id.sky)
        mBlueSkyColor = resources.getColor(R.color.blue_sky)
        mSunsetSkyColor = resources.getColor(R.color.sunset_sky)
        mNightSkyColor = resources.getColor(R.color.night_sky)

        mAndroidView = view.findViewById(R.id.android)
        mHeadView = view.findViewById(R.id.head)
        mBodyView = view.findViewById(R.id.body)
        mArmRightView = view.findViewById(R.id.arm_right)
        mArmLeftView = view.findViewById(R.id.arm_left)

        mGrass1 = view.findViewById(R.id.grass_1)
        mGrass2 = view.findViewById(R.id.grass_2)
        mGrass3 = view.findViewById(R.id.grass_3)
        mGrass4 = view.findViewById(R.id.grass_4)
        mGrass5 = view.findViewById(R.id.grass_5)
        mGrass6 = view.findViewById(R.id.grass_6)

        mScythe = view.findViewById(R.id.scythe)

        return view
    }

    fun startAnimation() {
        sun()
        grass()
        android()
    }

    fun sun() {
        mSunView.translationY = 700f
        mSunView.translationX = -800f
        mSunView.pivotX = mSunView.width / 2 - mSunView.translationX

        if (mSceneView.height > mSceneView.width) {
            mTitleView.translationY = mSceneView.height.toFloat() * 18 / 100
        } else {
            mTitleView.translationY = mSceneView.height.toFloat() * 10 / 100
        }

        val titleShowUpDuration: Long = 2000
        val titleShowUpDelay: Long = 1000
        val sunDuration: Long = 3000
        val colorDuration = sunDuration


        val titleShowUpAnimator = ObjectAnimator.ofFloat(mTitleView, View.ALPHA, 0f, 1f)
        titleShowUpAnimator.duration = titleShowUpDuration
        titleShowUpAnimator.interpolator = AccelerateInterpolator()
        titleShowUpAnimator.startDelay = titleShowUpDelay

        val rotateSunriseAnimator = ObjectAnimator.ofFloat(mSunView, View.ROTATION, 0f, 90f)
        rotateSunriseAnimator.duration = sunDuration
        rotateSunriseAnimator.interpolator = DecelerateInterpolator()

        val nightToSunriseSkyAnimator = ObjectAnimator.ofInt(mSkyView, "backgroundColor",
            mNightSkyColor!!, mSunsetSkyColor!!)
        nightToSunriseSkyAnimator.duration = colorDuration * 1 / 3
        nightToSunriseSkyAnimator.setEvaluator(ArgbEvaluator())

        val sunriseToFullSkyAnimator = ObjectAnimator.ofInt(mSkyView, "backgroundColor",
            mSunsetSkyColor!!, mBlueSkyColor!!)
        sunriseToFullSkyAnimator.duration = colorDuration * 2 / 3
        sunriseToFullSkyAnimator.setEvaluator(ArgbEvaluator())

        val animatorSetSunrise = AnimatorSet()
        animatorSetSunrise
            .play(rotateSunriseAnimator)
            .with(sunriseToFullSkyAnimator)
            .with(titleShowUpAnimator)
            .after(nightToSunriseSkyAnimator)
        animatorSetSunrise.start()
    }

    fun createGrassAnimator(view: View, translation: Float, duration: Long): ObjectAnimator {
        val shakeAnimator = ObjectAnimator.ofFloat(view,
            View.TRANSLATION_X,
            view.translationX,
            view.translationX + translation)
        shakeAnimator.duration = duration / 2
        shakeAnimator.interpolator = LinearInterpolator()
        shakeAnimator.repeatCount = ObjectAnimator.INFINITE
        shakeAnimator.repeatMode = ObjectAnimator.REVERSE

        return shakeAnimator
    }

    fun grass() {
        val shakeDuration: Long = 1000

        val grass1Animator = createGrassAnimator(mGrass1, 10f, shakeDuration)
        val grass2Animator = createGrassAnimator(mGrass2, -10f, shakeDuration)
        val grass3Animator = createGrassAnimator(mGrass3, 10f, shakeDuration)
        val grass4Animator = createGrassAnimator(mGrass4, -10f, shakeDuration)
        val grass5Animator = createGrassAnimator(mGrass5, 10f, shakeDuration)
        val grass6Animator = createGrassAnimator(mGrass6, -10f, shakeDuration)

        val animatorSet = AnimatorSet()
        animatorSet
            .play(grass1Animator)
            .with(grass2Animator)
            .with(grass3Animator)
            .with(grass4Animator)
            .with(grass5Animator)
            .with(grass6Animator)

        animatorSet.start()
    }

    fun createJumpAnimator(view: View, duration: Long): List<ObjectAnimator> {
        val jumpUpFastAnimator = ObjectAnimator.ofFloat(view,
            View.TRANSLATION_Y,
            view.translationY,
            view.translationY - 100f)
        jumpUpFastAnimator.duration = duration / 4
        jumpUpFastAnimator.interpolator = LinearInterpolator()
        val jumpUpSlowAnimator = ObjectAnimator.ofFloat(view,
            View.TRANSLATION_Y,
            view.translationY - 100f,
            view.translationY - 200f)
        jumpUpSlowAnimator.duration = duration / 4
        jumpUpSlowAnimator.interpolator = DecelerateInterpolator()
        val jumpDownFastAnimator = ObjectAnimator.ofFloat(view,
            View.TRANSLATION_Y,
            view.translationY - 200f,
            view.translationY - 100f)
        jumpDownFastAnimator.duration = duration / 4
        jumpDownFastAnimator.interpolator = AccelerateInterpolator()
        val jumpDownSlowAnimator = ObjectAnimator.ofFloat(view,
            View.TRANSLATION_Y,
            view.translationY - 100f,
            view.translationY)
        jumpDownSlowAnimator.duration = duration / 4
        jumpDownSlowAnimator.interpolator = DecelerateInterpolator()

        return listOf(jumpUpFastAnimator,
            jumpUpSlowAnimator,
            jumpDownFastAnimator,
            jumpDownSlowAnimator)
    }

    fun android() {
        mArmLeftView.pivotX = 300f
        mArmLeftView.pivotY = mArmLeftView.height.toFloat() * 45 / 100
        mArmLeftView.rotation = 15f
        mScythe.pivotX = mScythe.width.toFloat()
        mScythe.pivotY = -mScythe.height.toFloat()
        mScythe.rotation = 10f
        mArmRightView.pivotX = mArmRightView.width.toFloat() * 70 / 100
        mArmRightView.pivotY = mArmRightView.height.toFloat() * 45 / 100

        val wakeUpDelay: Long = 750
        val jumpDuration: Long = 1000
        val headDuration: Long = 750
        val firstHandDuration: Long = 500
        val handDuration: Long = 750

        val jumpAndroidAnimators = createJumpAnimator(mAndroidView, jumpDuration)
        val jumpScytheAnimator = createJumpAnimator(mScythe, jumpDuration)

        val headAnimator = ObjectAnimator.ofFloat(mHeadView,
            View.TRANSLATION_Y,
            mHeadView.y,
            mHeadView.y - 20f)
        headAnimator.duration = headDuration / 2
        headAnimator.interpolator = LinearInterpolator()
        headAnimator.repeatCount = ObjectAnimator.INFINITE
        headAnimator.repeatMode = ObjectAnimator.REVERSE
        headAnimator.startDelay = handDuration / 2

        val armRightUpFirstHighAnimator =
            ObjectAnimator.ofFloat(mArmRightView, View.ROTATION, 0f, -180f)
        armRightUpFirstHighAnimator.duration = firstHandDuration
        armRightUpFirstHighAnimator.interpolator = LinearInterpolator()
        val armRightAnimator =
            ObjectAnimator.ofFloat(mArmRightView, View.ROTATION, -180f, -135f)
        armRightAnimator.duration = handDuration / 2
        armRightAnimator.interpolator = LinearInterpolator()
        armRightAnimator.repeatCount = ObjectAnimator.INFINITE
        armRightAnimator.repeatMode = ObjectAnimator.REVERSE

        val armLeftAnimator = ObjectAnimator.ofFloat(mArmLeftView, View.ROTATION, 15f, 30f)
        armLeftAnimator.duration = handDuration / 2
        armLeftAnimator.interpolator = LinearInterpolator()
        armLeftAnimator.repeatCount = ObjectAnimator.INFINITE
        armLeftAnimator.repeatMode = ObjectAnimator.REVERSE

        val scytheAnimator = ObjectAnimator.ofFloat(mScythe, View.ROTATION, 10f, 13f)
        scytheAnimator.duration = handDuration / 2
        scytheAnimator.interpolator = LinearInterpolator()
        scytheAnimator.repeatCount = ObjectAnimator.INFINITE
        scytheAnimator.repeatMode = ObjectAnimator.REVERSE

        val animatorSetJumpAndroid = AnimatorSet()
        animatorSetJumpAndroid
            .play(jumpAndroidAnimators[0])
            .before(jumpAndroidAnimators[1])
            .before(jumpAndroidAnimators[2])
            .before(jumpAndroidAnimators[3])

        val animatorSetJumpScythe = AnimatorSet()
        animatorSetJumpScythe
            .play(jumpScytheAnimator[0])
            .before(jumpScytheAnimator[1])
            .before(jumpScytheAnimator[2])
            .before(jumpScytheAnimator[3])

        animatorSetJumpAndroid.startDelay = wakeUpDelay
        animatorSetJumpScythe.startDelay = wakeUpDelay

        val animatorSetHeadArm = AnimatorSet()
        animatorSetHeadArm
            .play(headAnimator)
            .with(armRightAnimator)
            .with(armLeftAnimator)
            .with(scytheAnimator)

        animatorSetJumpAndroid.doOnEnd {
            armRightUpFirstHighAnimator.start()
        }
        armRightUpFirstHighAnimator.doOnEnd {
            animatorSetHeadArm.start()
        }
        animatorSetJumpAndroid.start()
        animatorSetJumpScythe.start()
    }
}