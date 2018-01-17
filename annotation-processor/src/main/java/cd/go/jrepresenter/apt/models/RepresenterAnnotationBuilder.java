/*
 * Copyright 2018 ThoughtWorks, Inc.
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

package cd.go.jrepresenter.apt.models;

import cd.go.jrepresenter.RequestContext;
import com.squareup.javapoet.ClassName;

import java.util.Optional;

public final class RepresenterAnnotationBuilder {
    private ClassName representerClass;
    private ClassName modelClass;
    private ClassName linksProviderClass;
    private ClassName requestContextClass = ClassName.get(RequestContext.class);
    private boolean skipSerialize;
    private boolean skipDeserialize;
    private ClassName deserializerClass;
    private Optional<RepresentsSubClassesAnnotation> subClassInfo;

    private RepresenterAnnotationBuilder() {
    }

    public static RepresenterAnnotationBuilder aRepresenterAnnotation() {
        return new RepresenterAnnotationBuilder();
    }

    public RepresenterAnnotationBuilder withRepresenterClass(ClassName representerClass) {
        this.representerClass = representerClass;
        return this;
    }

    public RepresenterAnnotationBuilder withModelClass(ClassName modelClass) {
        this.modelClass = modelClass;
        return this;
    }

    public RepresenterAnnotationBuilder withLinksProviderClass(ClassName linksProviderClass) {
        this.linksProviderClass = linksProviderClass;
        return this;
    }

    public RepresenterAnnotationBuilder withRequestContextClass(ClassName requestContextClass) {
        this.requestContextClass = requestContextClass;
        return this;
    }

    public RepresenterAnnotationBuilder withSkipSerialize(boolean skipSerialize) {
        this.skipSerialize = skipSerialize;
        return this;
    }

    public RepresenterAnnotationBuilder withSkipDeserialize(boolean skipDeserialize) {
        this.skipDeserialize = skipDeserialize;
        return this;
    }

    public RepresenterAnnotationBuilder withDeserializerClass(ClassName deserializerClass) {
        this.deserializerClass = deserializerClass;
        return this;
    }

    public RepresenterAnnotationBuilder withSubClassInfo(Optional<RepresentsSubClassesAnnotation> subClassInfo) {
        this.subClassInfo = subClassInfo;
        return this;
    }

    public RepresenterAnnotation build() {
        return new RepresenterAnnotation(representerClass, modelClass, linksProviderClass, requestContextClass, skipSerialize, skipDeserialize, deserializerClass, subClassInfo);
    }
}
