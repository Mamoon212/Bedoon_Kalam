package com.mo2a.example.charades_

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("scoreOne")
fun TextView.setScoreOneString(scoreViewModel: ScoreViewModel?) {
    scoreViewModel?.let {
        text = "${scoreViewModel.scoreOneLive.value}"

    }
}
@BindingAdapter("scoreTwo")
fun TextView.setScoreTwoString(scoreViewModel: ScoreViewModel?) {
    scoreViewModel?.let {
        text = "${scoreViewModel.scoreTwoLive.value}"

    }
}
@BindingAdapter("scoreThree")
fun TextView.setScoreThreeString(scoreViewModel: ScoreViewModel?) {
    scoreViewModel?.let {
        text = "${scoreViewModel.scoreThreeLive.value}"

    }
}

@BindingAdapter("scoreFour")
fun TextView.setScoreFourString(scoreViewModel: ScoreViewModel?) {
    scoreViewModel?.let {
        text = "${scoreViewModel.scoreFourLive.value}"

    }
}
