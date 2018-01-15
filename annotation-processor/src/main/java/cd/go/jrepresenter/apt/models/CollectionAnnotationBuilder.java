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

public final class CollectionAnnotationBuilder {
    protected RepresenterAnnotation parent;
    protected boolean embedded;
    protected boolean renderEmpty = true;
    protected Attribute modelAttribute;
    protected Attribute jsonAttribute;
    protected TypeName representerClassName;
    protected TypeName serializerClassName;
    protected TypeName deserializerClassName;
    protected TypeName getterClassName;
    protected TypeName setterClassName;
    protected TypeName skipParse;
    protected TypeName skipRender;

    private CollectionAnnotationBuilder() {
    }

    public static CollectionAnnotationBuilder aCollectionAnnotation() {
        return new CollectionAnnotationBuilder();
    }

    public CollectionAnnotationBuilder withParent(RepresenterAnnotation parent) {
        this.parent = parent;
        return this;
    }

    public CollectionAnnotationBuilder withEmbedded(boolean embedded) {
        this.embedded = embedded;
        return this;
    }

    public CollectionAnnotationBuilder withRenderEmpty(boolean renderEmpty) {
        this.renderEmpty = renderEmpty;
        return this;
    }

    public CollectionAnnotationBuilder withModelAttribute(Attribute modelAttribute) {
        this.modelAttribute = modelAttribute;
        return this;
    }

    public CollectionAnnotationBuilder withJsonAttribute(Attribute jsonAttribute) {
        this.jsonAttribute = jsonAttribute;
        return this;
    }

    public CollectionAnnotationBuilder withRepresenterClassName(TypeName representerClassName) {
        this.representerClassName = representerClassName;
        return this;
    }

    public CollectionAnnotationBuilder withSerializerClassName(TypeName serializerClassName) {
        this.serializerClassName = serializerClassName;
        return this;
    }

    public CollectionAnnotationBuilder withDeserializerClassName(TypeName deserializerClassName) {
        this.deserializerClassName = deserializerClassName;
        return this;
    }

    public CollectionAnnotationBuilder withGetterClassName(TypeName getterClassName) {
        this.getterClassName = getterClassName;
        return this;
    }

    public CollectionAnnotationBuilder withSetterClassName(TypeName setterClassName) {
        this.setterClassName = setterClassName;
        return this;
    }

    public CollectionAnnotationBuilder withSkipParse(TypeName skipParse) {
        this.skipParse = skipParse;
        return this;
    }

    public CollectionAnnotationBuilder withSkipRender(TypeName skipRender) {
        this.skipRender = skipRender;
        return this;
    }

    public CollectionAnnotation build() {
        CollectionAnnotation collectionAnnotation = new CollectionAnnotation(modelAttribute, jsonAttribute, representerClassName, serializerClassName, deserializerClassName, getterClassName, setterClassName, skipParse, skipRender, renderEmpty);
        collectionAnnotation.setParent(parent);
        collectionAnnotation.setEmbedded(embedded);
        return collectionAnnotation;
    }
}
