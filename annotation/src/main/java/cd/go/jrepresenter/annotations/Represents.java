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

import cd.go.jrepresenter.EmptyLinksProvider;
import cd.go.jrepresenter.LinksProvider;
import cd.go.jrepresenter.RequestContext;
import cd.go.jrepresenter.util.NullBiFunction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.function.BiFunction;

@Target(ElementType.TYPE)
public @interface Represents {
    Class<?> value();

    Class<? extends LinksProvider> linksProvider() default EmptyLinksProvider.class;

    boolean skipSerialize() default false;

    boolean skipDeserialize() default false;

    Class<? extends BiFunction> deserializer() default NullBiFunction.class;

    Class<? extends RequestContext> requestContextType() default RequestContext.class;
}
