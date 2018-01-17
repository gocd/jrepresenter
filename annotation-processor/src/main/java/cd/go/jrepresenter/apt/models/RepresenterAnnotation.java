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

package cd.go.jrepresenter.apt.models;

import cd.go.jrepresenter.EmptyLinksProvider;
import com.squareup.javapoet.ClassName;

import java.util.Optional;

import static cd.go.jrepresenter.apt.models.BaseAnnotation.NULL_BI_FUNCTION;

public class RepresenterAnnotation {
    private static final String MAPPER_CLASS_SUFFIX = "Mapper";
    private static final String PACKAGE_NAME_PREFIX = "gen.";

    private final ClassName representerClass;
    private final ClassName modelClass;
    private final ClassName linksProviderClass;
    private final ClassName requestContextClass;
    private final boolean skipSerialize;
    private final boolean skipDeserialize;
    private final Optional<RepresentsSubClassesAnnotation> subClassInfo;

    private final ClassName deserializerClass;

    RepresenterAnnotation(ClassName representerClass, ClassName modelClass, ClassName linksProviderClass, ClassName requestContextClass, boolean skipSerialize, boolean skipDeserialize, ClassName deserializerClass, Optional<RepresentsSubClassesAnnotation> subClassInfo) {
        this.representerClass = representerClass;
        this.modelClass = modelClass;
        this.linksProviderClass = linksProviderClass == null ? ClassName.get(EmptyLinksProvider.class) : linksProviderClass;
        this.requestContextClass = requestContextClass;
        this.skipSerialize = skipSerialize;
        this.skipDeserialize = skipDeserialize;
        this.subClassInfo = subClassInfo == null ? Optional.empty() : subClassInfo;
        this.deserializerClass = deserializerClass == null ? NULL_BI_FUNCTION : deserializerClass;
    }

    public ClassName getDeserializerClass() {
        return deserializerClass;
    }

    public boolean hasDeserializerClass() {
        return !deserializerClass.equals(NULL_BI_FUNCTION);
    }

    public ClassName getRepresenterClass() {
        return representerClass;
    }

    public ClassName getModelClass() {
        return modelClass;
    }

    public ClassName getLinksProviderClass() {
        return linksProviderClass;
    }

    public ClassName getRequestContextClass() {
        return requestContextClass;
    }

    public boolean shouldSkipSerialize() {
        return skipSerialize;
    }

    public boolean shouldSkipDeserialize() {
        return skipDeserialize;
    }

    public Optional<RepresentsSubClassesAnnotation> getRepresentsSubClassesAnnotation() {
        return subClassInfo;
    }

    protected String packageNameRelocated() {
        return PACKAGE_NAME_PREFIX + getRepresenterClass().packageName();
    }

    String mapperClassImplSimpleName() {
        return mapperClassImplRelocated().simpleName();
    }

    public ClassName mapperClassImplRelocated() {
        return ClassName.bestGuess(packageNameRelocated() + "." + getRepresenterClass().simpleName().replaceAll("Representer$", "") + MAPPER_CLASS_SUFFIX);
    }

    public boolean hasLinksProvider() {
        return !getLinksProviderClass().equals(ClassName.get(EmptyLinksProvider.class));
    }


}
