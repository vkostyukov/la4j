/*
 * Copyright 2011-2014, by Vladimir Kostyukov and Contributors.
 *
 * This file is part of the la4j project (http://la4j.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributor(s):
 */

package org.la4j.vector;

import java.lang.reflect.ParameterizedType;

/**
 * An abstract vector factory.
 *
 * @param <T>
 */
public abstract class VectorFactory<T extends Vector> {

    @SuppressWarnings("unchecked cast")
    public final Class<T> outputClass = (Class<T>) ((ParameterizedType)
            getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    /**
     *
     */
    public abstract T apply(int length);
}
