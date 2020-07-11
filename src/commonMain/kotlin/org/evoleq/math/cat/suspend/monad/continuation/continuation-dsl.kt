/**
 * Copyright (c) 2020 Dr. Florian Schmidt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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


 