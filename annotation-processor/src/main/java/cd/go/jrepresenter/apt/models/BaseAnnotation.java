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

import cd.go.jrepresenter.JsonParseException;
import cd.go.jrepresenter.apt.util.DebugStatement;
import cd.go.jrepresenter.util.FalseFunction;
import cd.go.jrepresenter.util.NullBiConsumer;
import cd.go.jrepresenter.util.NullFunction;
import cd.go.jrepresenter.util.TrueFunction;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import static cd.go.jrepresenter.apt.models.MapperJavaConstantsFile.SKIP_PARSE_BUILDER;
import static cd.go.jrepresenter.apt.models.MapperJavaConstantsFile.SKIP_RENDER_BUILDER;
import static cd.go.jrepresenter.apt.models.MapperJavaSourceFile.*;
import static cd.go.jrepresenter.apt.util.TypeUtil.listOf;

public abstract class BaseAnnotation {
    protected static final ClassName NULL_FUNCTION = ClassName.get(NullFunction.class);
    protected static final ClassName NULL_BICONSUMER = ClassName.get(NullBiConsumer.class);
    public static final ClassName TRUE_FUNCTION = ClassName.get(TrueFunction.class);
    protected static final ClassName FALSE_FUNCTION = ClassName.get(FalseFunction.class);
    public static final ClassName VOID_CLASS = ClassName.get(Void.class);

    protected final Attribute modelAttribute;
    protected final Attribute jsonAttribute;
    protected final TypeName representerClassName;
    protected final TypeName serializerClassName;
    protected final TypeName deserializerClassName;
    protected final TypeName getterClassName;
    protected final TypeName setterClassName;

    protected final TypeName skipParse;
    protected final TypeName skipRender;

    protected RepresenterAnnotation parent;
    protected boolean embedded;

    BaseAnnotation(Attribute modelAttribute, Attribute jsonAttribute, TypeName representerClassName, TypeName serializerClassName, TypeName deserializerClassName, TypeName getterClassName, TypeName setterClassName, TypeName skipParse, TypeName skipRender) {
        this.modelAttribute = modelAttribute;
        this.jsonAttribute = jsonAttribute;
        this.representerClassName = representerClassName == null ? VOID_CLASS : representerClassName;
        this.serializerClassName = serializerClassName == null ? NULL_FUNCTION : serializerClassName;
        this.deserializerClassName = deserializerClassName == null ? NULL_FUNCTION : deserializerClassName;
        this.getterClassName = getterClassName == null ? NULL_FUNCTION : getterClassName;
        this.setterClassName = setterClassName == null ? NULL_BICONSUMER : setterClassName;
        this.skipParse = skipParse == null ? FALSE_FUNCTION : skipParse;
        this.skipRender = skipRender == null ? FALSE_FUNCTION : skipRender;
    }

    public final CodeBlock getSerializeCodeBlock(ClassToAnnotationMap classToAnnotationMap, String jsonVariableName) {
        CodeBlock.Builder builder = CodeBlock.builder();
        if (skipRender.equals(FALSE_FUNCTION)) {
            if (renderEmptyOrNull()) {
                builder.add(doSetSerializeCodeBlock(classToAnnotationMap, jsonVariableName));
            } else {
                addControlFlowForNullCheck(builder)
                        .add(doSetSerializeCodeBlock(classToAnnotationMap, jsonVariableName))
                        .endControlFlow();
            }
        } else if (!skipRender.equals(TRUE_FUNCTION)) {
            builder
                    .beginControlFlow("if (!$T.apply(value))", SKIP_RENDER_BUILDER.fieldName(skipRender))
                    .add(doSetSerializeCodeBlock(classToAnnotationMap, jsonVariableName))
                    .endControlFlow();
        }


        return builder.build();
    }

    protected abstract CodeBlock.Builder addControlFlowForNullCheck(CodeBlock.Builder builder);

    protected abstract boolean renderEmptyOrNull();

    public final CodeBlock getDeserializeCodeBlock(ClassToAnnotationMap context) {
        if (skipParse.equals(FALSE_FUNCTION)) {
            return CodeBlock.builder()
                    .add(doGetDeserializeCodeBlock(context))
                    .build();

        }
        if (skipParse.equals(TRUE_FUNCTION)) {
            return CodeBlock.builder().build();
        } else {
            return CodeBlock.builder()
                    .beginControlFlow("if (!$T.apply(value))", SKIP_PARSE_BUILDER.fieldName(skipParse))
                    .add(doGetDeserializeCodeBlock(context))
                    .endControlFlow()
                    .build();

        }
    }

    private CodeBlock doSetSerializeCodeBlock(ClassToAnnotationMap context, String jsonVariableName) {
        return putInJson(jsonVariableName, applyRenderRepresenter(context, applySerializer(applyGetter())));
    }

    protected abstract CodeBlock applySerializer(CodeBlock getterCodeBlock);

    protected boolean hasRepresenter() {
        return !representerClassName.equals(VOID_CLASS);
    }

    protected boolean hasGetterClass() {
        return !getterClassName.equals(NULL_FUNCTION);
    }

    protected boolean hasSetterClass() {
        return !setterClassName.equals(NULL_BICONSUMER);
    }

    CodeBlock applyGetter() {
        CodeBlock.Builder builder = CodeBlock.builder();
        if (hasGetterClass()) {
            return builder.add("$T.apply(value)", MapperJavaConstantsFile.GETTERS_BUILDER.fieldName(getterClassName)).build();
        } else {
            return builder.add("value.$N()", modelAttributeGetter()).build();
        }
    }

    CodeBlock applyRenderRepresenter(ClassToAnnotationMap context, CodeBlock getterWithSerializer) {
        if (hasRepresenter()) {
            return CodeBlock.builder()
                    .add("$T.toJSON(", context.findRepresenterAnnotation(representerClassName).mapperClassImplRelocated())
                    .add(getterWithSerializer)
                    .add(", requestContext)")
                    .build();
        } else {
            return getterWithSerializer;
        }
    }

    CodeBlock putInJson(String jsonVariableName, CodeBlock whatToPut) {
        return CodeBlock.builder()
                .add("$[")
                .add("$N.put($S, ", jsonVariableName, jsonAttribute.nameAsSnakeCase())
                .add(whatToPut)
                .add(");\n$]")
                .build();
    }

    CodeBlock doGetDeserializeCodeBlock(ClassToAnnotationMap context) {
        CodeBlock deserializeCodeBlock = applySetter(applyParseRepresenter(context, applyDeserializer(getValueFromJson())));
        return CodeBlock.builder()
                .beginControlFlow("if ($N.containsKey($S))", JSON_OBJECT_VAR_NAME, jsonAttribute.nameAsSnakeCase())
                .add(deserializeCodeBlock)
                .endControlFlow()
                .build();
    }

    protected abstract CodeBlock applyDeserializer(CodeBlock valueFromJson);

    protected String modelAttributeGetter() {
        return "get" + modelAttribute.name.substring(0, 1).toUpperCase() + modelAttribute.name.substring(1);
    }

    protected String modelAttributeSetter() {
        return "set" + modelAttribute.name.substring(0, 1).toUpperCase() + modelAttribute.name.substring(1);
    }

    public void setParent(RepresenterAnnotation parent) {
        this.parent = parent;
    }

    public boolean isEmbedded() {
        return embedded;
    }

    public void setEmbedded(boolean embedded) {
        this.embedded = embedded;
    }

    protected boolean hasSerializer() {
        return !serializerClassName.equals(NULL_FUNCTION);
    }

    protected boolean hasDeserializer() {
        return !deserializerClassName.equals(NULL_FUNCTION);
    }

    private CodeBlock applySetter(CodeBlock codeToSet) {
        CodeBlock.Builder builder = CodeBlock.builder()
                .add(codeToSet)
                .add(DebugStatement.printDebug("begin applying setter"));

        if (hasSetterClass()) {
            builder.addStatement("new $T().accept(model, $N)", setterClassName, MapperJavaSourceFile.MODEL_ATTRIBUTE_VARIABLE_NAME);
        } else {
            builder.addStatement("model.$N($N)", modelAttributeSetter(), MapperJavaSourceFile.MODEL_ATTRIBUTE_VARIABLE_NAME);
        }

        builder.add(DebugStatement.printDebug("end applying setter"));
        return builder.build();
    }

    private CodeBlock applyParseRepresenter(ClassToAnnotationMap context, CodeBlock deserializedCodeBlock) {
        CodeBlock.Builder builder = CodeBlock.builder()
                .add(deserializedCodeBlock)
                .add(DebugStatement.printDebug("begin applying parse representation"));

        TypeName targetType;
        if (this instanceof CollectionAnnotation) {
            targetType = listOf(modelAttribute.type);
        } else {
            targetType = modelAttribute.type;
        }

        if (hasRepresenter()) {
            ClassName mapperClass = context.findRepresenterAnnotation(representerClassName).mapperClassImplRelocated();
            builder.addStatement("$T $N = $T.fromJSON(($T) $N)", targetType, MODEL_ATTRIBUTE_VARIABLE_NAME, mapperClass, jsonAttributeRawType(), DESERIALIZED_JSON_ATTRIBUTE_NAME);
        } else {
            builder.addStatement("$T $N = ($T) $N", targetType, MODEL_ATTRIBUTE_VARIABLE_NAME, targetType, DESERIALIZED_JSON_ATTRIBUTE_NAME);
        }

        builder.add(DebugStatement.printDebug("end applying parse representation"));

        return builder.build();
    }

    private CodeBlock getValueFromJson() {
        return CodeBlock.builder()
                .add(DebugStatement.printDebug("begin to get the value from json"))
                .addStatement("$T $N = $N.get($S)", Object.class, JSON_ATTRIBUTE_VARIABLE_NAME, JSON_OBJECT_VAR_NAME, jsonAttribute.nameAsSnakeCase())
                .beginControlFlow("if (!($N instanceof $T))", JSON_ATTRIBUTE_VARIABLE_NAME, jsonAttributeRawType())
                .addStatement("$T.throwBadJsonType($S, $T.class, $N)", JsonParseException.class, jsonAttribute.nameAsSnakeCase(), jsonAttributeRawType(), JSON_OBJECT_VAR_NAME)
                .endControlFlow()
                .add(DebugStatement.printDebug("end to get the value from json"))
                .build();
    }

    TypeName jsonAttributeRawType() {
        TypeName type = jsonAttribute.type;
        if (type instanceof ParameterizedTypeName) {
            type = ((ParameterizedTypeName) type).rawType;
        }
        return type;
    }
}
