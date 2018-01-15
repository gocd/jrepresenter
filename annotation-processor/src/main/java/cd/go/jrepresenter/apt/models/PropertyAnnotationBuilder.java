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

import com.squareup.javapoet.TypeName;

public final class PropertyAnnotationBuilder {
    protected RepresenterAnnotation parent;
    protected boolean embedded;
    protected boolean renderNull = true;
    protected Attribute modelAttribute;
    protected Attribute jsonAttribute;
    protected TypeName representerClassName;
    protected TypeName serializerClassName;
    protected TypeName deserializerClassName;
    protected TypeName getterClassName;
    protected TypeName setterClassName;
    protected TypeName skipParse;
    protected TypeName skipRender;

    private PropertyAnnotationBuilder() {
    }

    public static PropertyAnnotationBuilder aPropertyAnnotation() {
        return new PropertyAnnotationBuilder();
    }

    public PropertyAnnotationBuilder withParent(RepresenterAnnotation parent) {
        this.parent = parent;
        return this;
    }

    public PropertyAnnotationBuilder withEmbedded(boolean embedded) {
        this.embedded = embedded;
        return this;
    }

    public PropertyAnnotationBuilder withRenderNull(boolean renderNull) {
        this.renderNull = renderNull;
        return this;
    }

    public PropertyAnnotationBuilder withModelAttribute(Attribute modelAttribute) {
        this.modelAttribute = modelAttribute;
        return this;
    }

    public PropertyAnnotationBuilder withJsonAttribute(Attribute jsonAttribute) {
        this.jsonAttribute = jsonAttribute;
        return this;
    }

    public PropertyAnnotationBuilder withRepresenterClassName(TypeName representerClassName) {
        this.representerClassName = representerClassName;
        return this;
    }

    public PropertyAnnotationBuilder withSerializerClassName(TypeName serializerClassName) {
        this.serializerClassName = serializerClassName;
        return this;
    }

    public PropertyAnnotationBuilder withDeserializerClassName(TypeName deserializerClassName) {
        this.deserializerClassName = deserializerClassName;
        return this;
    }

    public PropertyAnnotationBuilder withGetterClassName(TypeName getterClassName) {
        this.getterClassName = getterClassName;
        return this;
    }

    public PropertyAnnotationBuilder withSetterClassName(TypeName setterClassName) {
        this.setterClassName = setterClassName;
        return this;
    }

    public PropertyAnnotationBuilder withSkipParse(TypeName skipParse) {
        this.skipParse = skipParse;
        return this;
    }

    public PropertyAnnotationBuilder withSkipRender(TypeName skipRender) {
        this.skipRender = skipRender;
        return this;
    }

    public PropertyAnnotation build() {
        PropertyAnnotation propertyAnnotation = new PropertyAnnotation(modelAttribute, jsonAttribute, serializerClassName, deserializerClassName, representerClassName, getterClassName, setterClassName, skipParse, skipRender, renderNull);
        propertyAnnotation.setParent(parent);
        propertyAnnotation.setEmbedded(embedded);
        return propertyAnnotation;
    }
}
