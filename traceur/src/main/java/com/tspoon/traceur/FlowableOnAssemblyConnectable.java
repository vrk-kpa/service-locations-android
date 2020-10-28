/*
 * Copyright 2016-2017 David Karnok
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

package com.tspoon.traceur;

import org.reactivestreams.Subscriber;

import io.reactivex.disposables.Disposable;
import io.reactivex.flowables.ConnectableFlowable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.fuseable.ConditionalSubscriber;

/**
 * Wraps a Publisher and inject the assembly info.
 *
 * @param <T> the value type
 */
final class FlowableOnAssemblyConnectable<T> extends ConnectableFlowable<T> {

    final ConnectableFlowable<T> source;

    final TraceurException assembled;

    FlowableOnAssemblyConnectable(ConnectableFlowable<T> source) {
        this.source = source;
        this.assembled = TraceurException.create();
    }

    @Override
    protected void subscribeActual(Subscriber<? super T> s) {
        if (s instanceof ConditionalSubscriber) {
            source.subscribe(new FlowableOnAssembly.OnAssemblyConditionalSubscriber<T>((ConditionalSubscriber<? super T>)s, assembled));
        } else {
            source.subscribe(new FlowableOnAssembly.OnAssemblySubscriber<T>(s, assembled));
        }
    }

    @Override
    public void connect(Consumer<? super Disposable> connection) {
        source.connect(connection);
    }
}
