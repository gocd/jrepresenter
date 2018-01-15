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

import cd.go.jrepresenter.util.FalseFunction;
import cd.go.jrepresenter.util.NullBiConsumer;
import cd.go.jrepresenter.util.NullFunction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Target(ElementType.METHOD)
public @interface Property {
    String modelAttributeName() default "";

    Class<?> modelAttributeType() default Void.class;

    Class<? extends Function> serializer() default NullFunction.class;

    Class<? extends Function> deserializer() default NullFunction.class;

    Class<? extends Function> getter() default NullFunction.class;

    Class<? extends BiConsumer> setter() default NullBiConsumer.class;

    boolean embedded() default false;

    Class<?> representer() default Void.class;

    Class<? extends Function> skipParse() default FalseFunction.class;

    Class<? extends Function> skipRender() default FalseFunction.class;

    boolean renderNull() default true;
}
