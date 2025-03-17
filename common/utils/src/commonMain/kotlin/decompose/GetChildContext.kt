package decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext

fun ComponentContext.getChildContext(key: String) = childContext(key+"CONTEXT", null)