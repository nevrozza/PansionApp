package transitions

//fun verticalSlide(animationSpec: FiniteAnimationSpec<Float> = tween()): StackAnimator =
//    stackAnimator(animationSpec = animationSpec) { factor, _, content ->
//        content(Modifier.offsetYFactor(factor = factor))
//    }

//private fun Modifier.offsetYFactor(factor: Float): Modifier =
//    layout { measurable, constraints ->
//        val placeable = measurable.measure(constraints)
//
//        layout(placeable.width, placeable.height) {
//            placeable.placeRelative(x = 0, y = (placeable.height.toFloat() * factor).toInt())
//        }
//    }