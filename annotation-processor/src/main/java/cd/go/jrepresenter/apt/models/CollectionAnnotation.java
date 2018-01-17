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

import cd.go.jrepresenter.apt.util.DebugStatement;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;

import java.util.stream.Collectors;

import static cd.go.jrepresenter.apt.models.MapperJavaSourceFile.JSON_ATTRIBUTE_VARIABLE_NAME;

public class CollectionAnnotation extends BaseAnnotation {
    protected final boolean renderEmpty;

    public CollectionAnnotation(Attribute modelAttribute, Attribute jsonAttribute, TypeName representerClassName, TypeName serializerClassName, TypeName deserializerClassName, TypeName getterClassName, TypeName setterClassName, TypeName skipParse, TypeName skipRender, boolean renderEmpty) {
        super(modelAttribute, jsonAttribute, representerClassName, serializerClassName, deserializerClassName, getterClassName, setterClassName, skipParse, skipRender);
        this.renderEmpty = renderEmpty;
    }


    @Override
    protected CodeBlock.Builder addControlFlowForNullCheck(CodeBlock.Builder builder) {
        return builder.beginControlFlow("if (value.$N() != null && value.$N().size() > 0))", modelAttributeGetter(), modelAttributeGetter());
    }

    @Override
    protected boolean renderEmptyOrNull() {
        return renderEmpty;
    }

    @Override
    protected CodeBlock applySerializer(CodeBlock valueFromGetter) {
        if (hasSerializer()) {
            return CodeBlock.builder()
                    .add(DebugStatement.printDebug("foo"))
                    .add("(")
                    .add(valueFromGetter)
                    .add(")")
                    .add(".stream().map(x -> new $T().apply(x, requestContext)).collect($T.toList())",
                            serializerClassName,
                            Collectors.class)
                    .build();
        } else {
            return valueFromGetter;
        }
    }

    @Override
    protected CodeBlock applyDeserializer(CodeBlock valueFromJson) {
        CodeBlock.Builder builder = CodeBlock.builder()
                .add(valueFromJson)
                .add(DebugStatement.printDebug("begin to apply deserializer"));
        if (hasDeserializer()) {
            builder.addStatement("$T $N = (($T) ($N)).stream().map(x -> new $T().apply(x, requestContext)).collect($T.toList())",
                    jsonAttributeRawType(),
                    MapperJavaSourceFile.DESERIALIZED_JSON_ATTRIBUTE_NAME,
                    jsonAttribute.type,
                    JSON_ATTRIBUTE_VARIABLE_NAME,
                    deserializerClassName,
                    Collectors.class);
        } else {
            builder.addStatement(
                    "$T $N = ($T) $N",
                    jsonAttributeRawType(),
                    MapperJavaSourceFile.DESERIALIZED_JSON_ATTRIBUTE_NAME,
                    jsonAttributeRawType(),
                    JSON_ATTRIBUTE_VARIABLE_NAME);
        }

        builder.add(DebugStatement.printDebug("end applying deserializer"));

        return builder.build();
    }

}
