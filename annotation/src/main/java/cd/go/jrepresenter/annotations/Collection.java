/*
 * Copyright 2017 ThoughtWorks, Inc.
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

package cd.go.jrepresenter.annotations;

import cd.go.jrepresenter.util.FalseBiFunction;
import cd.go.jrepresenter.util.NullBiFunction;
import cd.go.jrepresenter.util.NullTriConsumer;
import cd.go.jrepresenter.util.TriConsumer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.function.BiFunction;

@Target(ElementType.METHOD)
public @interface Collection {
    String modelAttributeName() default "";

    Class<?> modelAttributeType();

    Class<? extends BiFunction> serializer() default NullBiFunction.class;

    Class<? extends BiFunction> deserializer() default NullBiFunction.class;

    Class<? extends BiFunction> getter() default NullBiFunction.class;

    Class<? extends TriConsumer> setter() default NullTriConsumer.class;

    boolean embedded() default false;

    Class<?> representer() default Void.class;

    Class<? extends BiFunction> skipParse() default FalseBiFunction.class;

    Class<? extends BiFunction> skipRender() default FalseBiFunction.class;

    boolean renderEmpty() default true;
}
