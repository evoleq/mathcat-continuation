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
import org.evoleq.math.cat.marker.MathCatDsl
import org.evoleq.math.cat.suspend.morphism.ScopedSuspended
import org.evoleq.math.cat.suspend.morphism.by

interface Continuation<R, A> : ScopedSuspended<suspend CoroutineScope.(A)->R, R> {

    suspend infix fun <B> map(f: suspend CoroutineScope.(A)->B): Continuation<R, B> = Continuation<R, B> {
        h: suspend CoroutineScope.(B)->R ->  by(this@Continuation)(by(ScopedSuspended(h) o ScopedSuspended(f)))
    }
    
    suspend fun <B> bind(arrow: suspend CoroutineScope.(A)->Continuation<R, B>): Continuation<R, B> = (this map arrow).multiply()
}



@MathCatDsl
@Suppress("FunctionName")
fun <R, A> Continuation(arrow: suspend CoroutineScope.(suspend CoroutineScope.(A)->R)->R): Continuation<R, A> = object : Continuation<R, A> {
    override val morphism: suspend CoroutineScope.(suspend CoroutineScope.(A) -> R) -> R  = arrow
}

@MathCatDsl
fun <R, A> eta(a: A): Continuation<R, A> = Continuation<R, A> {
    f -> f(a)
}

@MathCatDsl
suspend fun <R, A > Continuation<R, Continuation<R, A>>.multiply(): Continuation<R, A> = Continuation<R, A>{
    h -> (by(this@multiply)) {cont: Continuation<R, A> -> by(cont)(h)}
}


interface KlContinuation<R, A, B> : ScopedSuspended<A, Continuation<R, B>>

@MathCatDsl
@Suppress("FunctionName")
fun <R, A, B> KlContinuation(arrow: suspend CoroutineScope.(A)->Continuation<R, B>): KlContinuation<R, A, B> = object : KlContinuation<R, A, B> {
    override val morphism: suspend CoroutineScope.(A) -> Continuation<R, B> = arrow
}

suspend fun <R, A, B, C> KlContinuation<R, A, B>.times(other: KlContinuation<R, B, C>): KlContinuation<R, A, C> = KlContinuation {
    a -> (by(this@times)(a) map by(other)).multiply()
}

