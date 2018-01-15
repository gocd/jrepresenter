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

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;

public final class BaseAnnotationBuilder {
    protected RepresenterAnnotation parent;
    protected boolean embedded;
    protected Attribute modelAttribute;
    protected Attribute jsonAttribute;
    protected TypeName representerClassName;
    protected TypeName serializerClassName;
    protected TypeName deserializerClassName;
    protected TypeName getterClassName;
    protected TypeName setterClassName;
    protected TypeName skipParse;
    protected TypeName skipRender;
    private boolean renderEmptyOrNull = true;

    private BaseAnnotationBuilder() {
    }

    public static BaseAnnotationBuilder aBaseAnnotation() {
        return new BaseAnnotationBuilder();
    }

    public BaseAnnotationBuilder withParent(RepresenterAnnotation parent) {
        this.parent = parent;
        return this;
    }

    public BaseAnnotationBuilder withEmbedded(boolean embedded) {
        this.embedded = embedded;
        return this;
    }

    public BaseAnnotationBuilder withModelAttribute(Attribute modelAttribute) {
        this.modelAttribute = modelAttribute;
        return this;
    }

    public BaseAnnotationBuilder withJsonAttribute(Attribute jsonAttribute) {
        this.jsonAttribute = jsonAttribute;
        return this;
    }

    public BaseAnnotationBuilder withRepresenterClassName(TypeName representerClassName) {
        this.representerClassName = representerClassName;
        return this;
    }

    public BaseAnnotationBuilder withSerializerClassName(TypeName serializerClassName) {
        this.serializerClassName = serializerClassName;
        return this;
    }

    public BaseAnnotationBuilder withDeserializerClassName(TypeName deserializerClassName) {
        this.deserializerClassName = deserializerClassName;
        return this;
    }

    public BaseAnnotationBuilder withGetterClassName(TypeName getterClassName) {
        this.getterClassName = getterClassName;
        return this;
    }

    public BaseAnnotationBuilder withSetterClassName(TypeName setterClassName) {
        this.setterClassName = setterClassName;
        return this;
    }

    public BaseAnnotationBuilder withSkipParse(TypeName skipParse) {
        this.skipParse = skipParse;
        return this;
    }

    public BaseAnnotationBuilder withSkipRender(TypeName skipRender) {
        this.skipRender = skipRender;
        return this;
    }

    public BaseAnnotationBuilder withRenderEmptyOrNull(boolean renderEmptyOrNull) {
        this.renderEmptyOrNull = renderEmptyOrNull;
        return this;
    }

    public BaseAnnotation build() {
        BaseAnnotation baseAnnotation = new BaseAnnotation(modelAttribute, jsonAttribute, representerClassName, serializerClassName, deserializerClassName, getterClassName, setterClassName, skipParse, skipRender) {

            @Override
            protected CodeBlock.Builder addControlFlowForNullCheck(CodeBlock.Builder builder) {
                return builder.beginControlFlow("if (/* perform some conditional */)");
            }

            @Override
            protected boolean renderEmptyOrNull() {
                return renderEmptyOrNull;
            }

            @Override
            protected CodeBlock applySerializer(CodeBlock getterCodeBlock) {
                return CodeBlock.builder().add("/* apply some serializer here */").build();
            }

            @Override
            protected CodeBlock applyDeserializer(CodeBlock valueFromJson) {
                return CodeBlock.builder().add("/* apply some deserializer here */").build();
            }
        };
        baseAnnotation.setParent(parent);
        baseAnnotation.setEmbedded(embedded);
        return baseAnnotation;
    }
}
