package org.evoleq.math.cat.suspend.monad.continuation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import org.evoleq.math.cat.marker.MathCatDsl
import org.evoleq.math.cat.suspend.morphism.by

/**
 * Prepare for running [Continuation] on a fixed function
 */
@MathCatDsl
suspend fun <R, A> continueWith(f: (suspend CoroutineScope.(A)->R)): suspend CoroutineScope.(Continuation<R, A>)->R = {
    continuation -> continuation.run(f)
}

/**
 *
 */
@MathCatDsl
suspend infix fun <R, A> Continuation<R, Continuation<R, A>>.continueOn(f: suspend CoroutineScope.(A)->R): R =
    coroutineScope { (by(this@continueOn)) ( continueWith(f) ) }


 