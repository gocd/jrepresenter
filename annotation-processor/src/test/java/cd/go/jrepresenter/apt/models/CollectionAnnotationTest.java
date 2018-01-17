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

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static cd.go.jrepresenter.apt.models.TestConstants.USER_MODEL;
import static cd.go.jrepresenter.apt.models.TestConstants.USER_REPRESENTER_CLASS;
import static cd.go.jrepresenter.apt.util.TypeUtil.listOf;
import static org.assertj.core.api.Assertions.assertThat;

public class CollectionAnnotationTest {

    @Test
    public void shouldGenerateCodeToSerialize() throws Exception {

        RepresenterAnnotation representerAnnotation = RepresenterAnnotationBuilder.aRepresenterAnnotation()
                .withRepresenterClass(USER_REPRESENTER_CLASS)
                .withModelClass(USER_MODEL)
                .withLinksProviderClass(null)
                .withSkipDeserialize(false)
                .withSkipSerialize(false)
                .build();
        Attribute modelAttribute = new Attribute("usersInternal", listOf(USER_MODEL));
        Attribute jsonAttribute = new Attribute("users", ClassName.get(List.class));
        CollectionAnnotation annotation = CollectionAnnotationBuilder.aCollectionAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .withRepresenterClassName(USER_REPRESENTER_CLASS)
                .build();
        ClassToAnnotationMap context = new ClassToAnnotationMap();
        context.add(representerAnnotation);
        context.addAnnotatedMethod("com.foo.representers.UserRepresenter", annotation);

        CodeBlock codeBlock = annotation.getSerializeCodeBlock(context, "json");

        String expectedCode = "json.put(\"users\", gen.com.tw.UserMapper.toJSON(value.getUsersInternal(), requestContext));\n";
        assertThat(codeBlock.toString()).isEqualTo(expectedCode);
    }

    @Test
    public void shouldGenerateCodeToDeserialize() {

        RepresenterAnnotation representerAnnotation = RepresenterAnnotationBuilder.aRepresenterAnnotation()
                .withRepresenterClass(USER_REPRESENTER_CLASS)
                .withModelClass(USER_MODEL)
                .withLinksProviderClass(null)
                .withSkipDeserialize(false)
                .withSkipSerialize(false)
                .build();
        Attribute modelAttribute = new Attribute("usersInternal", listOf(USER_MODEL));
        Attribute jsonAttribute = new Attribute("users", listOf(Map.class));
        CollectionAnnotation annotation = CollectionAnnotationBuilder.aCollectionAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .withRepresenterClassName(USER_REPRESENTER_CLASS)
                .build();

        ClassToAnnotationMap context = new ClassToAnnotationMap();
        context.add(representerAnnotation);
        context.addAnnotatedMethod("com.foo.representers.UserRepresenter", annotation);

        CodeBlock codeBlock = annotation.doGetDeserializeCodeBlock(context);

        String expectedCode = "" +
                "if (jsonObject.containsKey(\"users\")) {\n" +
                "  java.lang.Object jsonAttribute = jsonObject.get(\"users\");\n" +
                "  if (!(jsonAttribute instanceof java.util.List)) {\n" +
                "    cd.go.jrepresenter.JsonParseException.throwBadJsonType(\"users\", java.util.List.class, jsonObject);\n" +
                "  }\n" +
                "  java.util.List deserializedJsonAttribute = (java.util.List) jsonAttribute;\n" +
                "  java.util.List<java.util.List<com.tw.User>> modelAttribute = gen.com.tw.UserMapper.fromJSON((java.util.List) deserializedJsonAttribute, requestContext);\n" +
                "  model.setUsersInternal(modelAttribute);\n" +
                "}\n";
        assertThat(codeBlock.toString()).isEqualTo(expectedCode);
    }

    @Test
    public void shouldGenerateCodeToDeserializeUsingDeserializer() {

        RepresenterAnnotation representerAnnotation = RepresenterAnnotationBuilder.aRepresenterAnnotation()
                .withRepresenterClass(USER_REPRESENTER_CLASS)
                .withModelClass(USER_MODEL)
                .withLinksProviderClass(null)
                .withSkipDeserialize(false)
                .withSkipSerialize(false)
                .build();
        Attribute modelAttribute = new Attribute("usersInternal", listOf(USER_MODEL));
        Attribute jsonAttribute = new Attribute("users", listOf(Map.class));
        CollectionAnnotation annotation = CollectionAnnotationBuilder.aCollectionAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .withDeserializerClassName(ClassName.bestGuess("com.tw.deserializers.UserDeserializer"))
                .withRepresenterClassName(USER_REPRESENTER_CLASS)
                .build();

        ClassToAnnotationMap context = new ClassToAnnotationMap();
        context.add(representerAnnotation);
        context.addAnnotatedMethod("com.foo.representers.UserRepresenter", annotation);

        CodeBlock codeBlock = annotation.doGetDeserializeCodeBlock(context);

        String expectedCode = "" +
                "if (jsonObject.containsKey(\"users\")) {\n" +
                "  java.lang.Object jsonAttribute = jsonObject.get(\"users\");\n" +
                "  if (!(jsonAttribute instanceof java.util.List)) {\n" +
                "    cd.go.jrepresenter.JsonParseException.throwBadJsonType(\"users\", java.util.List.class, jsonObject);\n" +
                "  }\n" +
                "  java.util.List deserializedJsonAttribute = ((java.util.List<java.util.Map>) (jsonAttribute)).stream().map(x -> new com.tw.deserializers.UserDeserializer().apply(x, requestContext)).collect(java.util.stream.Collectors.toList());\n" +
                "  java.util.List<java.util.List<com.tw.User>> modelAttribute = gen.com.tw.UserMapper.fromJSON((java.util.List) deserializedJsonAttribute, requestContext);\n" +
                "  model.setUsersInternal(modelAttribute);\n" +
                "}\n";
        assertThat(codeBlock.toString()).isEqualTo(expectedCode);

    }

    @Test
    public void shouldGenerateCodeToSerializeWithTargetWithSerializer() throws Exception {

        RepresenterAnnotation representerAnnotation = RepresenterAnnotationBuilder.aRepresenterAnnotation()
                .withRepresenterClass(USER_REPRESENTER_CLASS)
                .withModelClass(USER_MODEL)
                .withLinksProviderClass(null)
                .withSkipDeserialize(false)
                .withSkipSerialize(false)
                .build();
        Attribute modelAttribute = new Attribute("usersInternal", listOf(USER_MODEL));
        Attribute jsonAttribute = new Attribute("users", ClassName.get(List.class));
        CollectionAnnotation annotation = CollectionAnnotationBuilder.aCollectionAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .withSerializerClassName(ClassName.bestGuess("com.foo.serializers.UserSerializer"))
                .build();

        ClassToAnnotationMap context = new ClassToAnnotationMap();
        context.add(representerAnnotation);
        context.addAnnotatedMethod("com.foo.representers.UserRepresenter", annotation);

        CodeBlock codeBlock = annotation.getSerializeCodeBlock(context, "json");

        String expectedCode = "json.put(\"users\", (value.getUsersInternal()).stream().map(x -> new com.foo.serializers.UserSerializer().apply(x, requestContext)).collect(java.util.stream.Collectors.toList()));\n";
        assertThat(codeBlock.toString()).isEqualTo(expectedCode);
    }

    @Test
    public void shouldGenerateCodeToSerializeWithTargetWithGetter() throws Exception {

        RepresenterAnnotation representerAnnotation = RepresenterAnnotationBuilder.aRepresenterAnnotation()
                .withRepresenterClass(USER_REPRESENTER_CLASS)
                .withModelClass(USER_MODEL)
                .withLinksProviderClass(null)
                .withSkipDeserialize(false)
                .withSkipSerialize(false)
                .build();
        Attribute modelAttribute = new Attribute("usersInternal", listOf(USER_MODEL));
        Attribute jsonAttribute = new Attribute("users", ClassName.get(List.class));
        CollectionAnnotation annotation = CollectionAnnotationBuilder.aCollectionAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .withGetterClassName(ClassName.bestGuess("com.tw.UsersGetter"))
                .build();

        ClassToAnnotationMap context = new ClassToAnnotationMap();
        context.add(representerAnnotation);
        context.addAnnotatedMethod("com.foo.representers.UserRepresenter", annotation);

        CodeBlock codeBlock = annotation.getSerializeCodeBlock(context, "json");

        String expectedCode = "json.put(\"users\", new com.tw.UsersGetter().apply(value, requestContext));\n";
        assertThat(codeBlock.toString()).isEqualTo(expectedCode);
    }

    @Test
    public void shouldGenerateCodeToSerializeWithTargetWithGetterAndSerializer() throws Exception {

        RepresenterAnnotation representerAnnotation = RepresenterAnnotationBuilder.aRepresenterAnnotation()
                .withRepresenterClass(USER_REPRESENTER_CLASS)
                .withModelClass(USER_MODEL)
                .withLinksProviderClass(null)
                .withSkipDeserialize(false)
                .withSkipSerialize(false)
                .build();
        Attribute modelAttribute = new Attribute("usersInternal", listOf(USER_MODEL));
        Attribute jsonAttribute = new Attribute("users", ClassName.get(List.class));
        CollectionAnnotation annotation = CollectionAnnotationBuilder.aCollectionAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .build();

        ClassToAnnotationMap context = new ClassToAnnotationMap();
        context.add(representerAnnotation);
        context.addAnnotatedMethod("com.foo.representers.UserRepresenter", annotation);

        CodeBlock codeBlock = annotation.getSerializeCodeBlock(context, "json");

        String expectedCode = "json.put(\"users\", value.getUsersInternal());\n";
        assertThat(codeBlock.toString()).isEqualTo(expectedCode);
    }
}
