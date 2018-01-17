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

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static cd.go.jrepresenter.apt.models.TestConstants.*;

public class PropertyAnnotationTest {

    @Test
    public void shouldGenerateCodeToSerializeWithTargetWithoutSerializer() throws Exception {
        Attribute modelAttribute = new Attribute("fname", STRING_CLASS);
        Attribute jsonAttribute = new Attribute("firstName", STRING_CLASS);

        PropertyAnnotation propertyAnnotation = PropertyAnnotationBuilder.aPropertyAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .build();
        CodeBlock codeBlock = propertyAnnotation.getSerializeCodeBlock(null, "json");
        assertThat(codeBlock.toString()).isEqualTo("json.put(\"first_name\", value.getFname());\n");
    }

    @Test
    public void shouldGenerateCodeToSerializeWithTargetWithSerializer() throws Exception {
        Attribute modelAttribute = new Attribute("fname", CASE_INSENSITIVE_STRING);
        Attribute jsonAttribute = new Attribute("firstName", STRING_CLASS);
        PropertyAnnotation propertyAnnotation = PropertyAnnotationBuilder.aPropertyAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .withSerializerClassName(CASE_INSENSITIVE_STRING_SERIALIZER)
                .build();
        CodeBlock codeBlock = propertyAnnotation.getSerializeCodeBlock(null, "json");
        assertThat(codeBlock.toString()).isEqualTo("json.put(\"first_name\", new com.tw.CaseInsensitiveStringSerializer().apply(value.getFname(), requestContext));\n");
    }

    @Test
    public void shouldGenerateCodeToDeserializeWithoutSerializer() {
        Attribute modelAttribute = new Attribute("fname", STRING_CLASS);
        Attribute jsonAttribute = new Attribute("firstName", STRING_CLASS);
        PropertyAnnotation propertyAnnotation = PropertyAnnotationBuilder.aPropertyAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .build();
        CodeBlock codeBlock = propertyAnnotation.doGetDeserializeCodeBlock(null);
        String expectedCode = "" +
                "if (jsonObject.containsKey(\"first_name\")) {\n" +
                "  java.lang.Object jsonAttribute = jsonObject.get(\"first_name\");\n" +
                "  if (!(jsonAttribute instanceof java.lang.String)) {\n" +
                "    cd.go.jrepresenter.JsonParseException.throwBadJsonType(\"first_name\", java.lang.String.class, jsonObject);\n" +
                "  }\n" +
                "  java.lang.String deserializedJsonAttribute = (java.lang.String) jsonAttribute;\n" +
                "  java.lang.String modelAttribute = (java.lang.String) deserializedJsonAttribute;\n" +
                "  model.setFname(modelAttribute);\n" +
                "}\n";
        assertThat(codeBlock.toString()).isEqualTo(expectedCode);
    }

    @Test
    public void shouldGenerateCodeToDeserializeWithoutSerializerWithPrimitiveType() {
        Attribute modelAttribute = new Attribute("age", INT_TYPE);
        Attribute jsonAttribute = new Attribute("age", INT_TYPE);
        PropertyAnnotation propertyAnnotation = PropertyAnnotationBuilder.aPropertyAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .build();
        CodeBlock codeBlock = propertyAnnotation.doGetDeserializeCodeBlock(null);
        String expectedCodeBlock = "" +
                "if (jsonObject.containsKey(\"age\")) {\n" +
                "  java.lang.Object jsonAttribute = jsonObject.get(\"age\");\n" +
                "  if (!(jsonAttribute instanceof int)) {\n" +
                "    cd.go.jrepresenter.JsonParseException.throwBadJsonType(\"age\", int.class, jsonObject);\n" +
                "  }\n" +
                "  int deserializedJsonAttribute = (int) jsonAttribute;\n" +
                "  int modelAttribute = (int) deserializedJsonAttribute;\n" +
                "  model.setAge(modelAttribute);\n" +
                "}\n";
        assertThat(codeBlock.toString()).isEqualTo(expectedCodeBlock);
    }

    @Test
    public void shouldGenerateCodeToDeserializeWithDeserializerClass() {
        Attribute modelAttribute = new Attribute("fname", CASE_INSENSITIVE_STRING);
        Attribute jsonAttribute = new Attribute("firstName", STRING_CLASS);
        PropertyAnnotation propertyAnnotation = PropertyAnnotationBuilder.aPropertyAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .withDeserializerClassName(CASE_INSENSITIVE_STRING_DESERIALIZER)
                .build();

        CodeBlock codeBlock = propertyAnnotation.doGetDeserializeCodeBlock(null);
        assertThat(codeBlock.toString()).isEqualTo("" +
                "if (jsonObject.containsKey(\"first_name\")) {\n" +
                "  java.lang.Object jsonAttribute = jsonObject.get(\"first_name\");\n" +
                "  if (!(jsonAttribute instanceof java.lang.String)) {\n" +
                "    cd.go.jrepresenter.JsonParseException.throwBadJsonType(\"first_name\", java.lang.String.class, jsonObject);\n" +
                "  }\n" +
                "  com.tw.CaseInsensitiveString deserializedJsonAttribute = new com.tw.CaseInsensitiveStringDeserializer().apply((java.lang.String) jsonAttribute, requestContext);\n" +
                "  com.tw.CaseInsensitiveString modelAttribute = (com.tw.CaseInsensitiveString) deserializedJsonAttribute;\n" +
                "  model.setFname(modelAttribute);\n" +
                "}\n");
    }

    @Test
    public void shouldGenerateCodeToSerializePropertyUsingRepresenter() {
        Attribute modelAttribute = new Attribute("triggeredBy", USER_MODEL);
        Attribute jsonAttribute = new Attribute("user", null);

        RepresenterAnnotation representerAnnotation = RepresenterAnnotationBuilder.aRepresenterAnnotation()
                .withRepresenterClass(TestConstants.USER_REPRESENTER_CLASS)
                .withModelClass(USER_MODEL)
                .withLinksProviderClass(EMPTY_LINKS_PROVIDER)
                .withSkipDeserialize(false)
                .withSkipSerialize(false)
                .build();
        ClassToAnnotationMap context = new ClassToAnnotationMap();
        context.add(representerAnnotation);

        PropertyAnnotation propertyAnnotation = PropertyAnnotationBuilder.aPropertyAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .withRepresenterClassName(TestConstants.USER_REPRESENTER_CLASS)
                .build();

        CodeBlock codeBlock = propertyAnnotation.getSerializeCodeBlock(context, "json");
        assertThat(codeBlock.toString()).isEqualTo("json.put(\"user\", gen.com.tw.UserMapper.toJSON(value.getTriggeredBy(), requestContext));\n");
    }

    @Test
    public void shouldGenerateCodeToDeserializePropertyUsingRepresenter() {
        Attribute modelAttribute = new Attribute("triggeredBy", USER_MODEL);
        Attribute jsonAttribute = new Attribute("user", ClassName.get(Map.class));

        RepresenterAnnotation representerAnnotation = RepresenterAnnotationBuilder.aRepresenterAnnotation()
                .withRepresenterClass(TestConstants.USER_REPRESENTER_CLASS)
                .withModelClass(USER_MODEL)
                .withLinksProviderClass(EMPTY_LINKS_PROVIDER)
                .withSkipDeserialize(false)
                .withSkipSerialize(false)
                .build();
        ClassToAnnotationMap context = new ClassToAnnotationMap();
        context.add(representerAnnotation);

        PropertyAnnotation propertyAnnotation = PropertyAnnotationBuilder.aPropertyAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .withRepresenterClassName(TestConstants.USER_REPRESENTER_CLASS)
                .build();

        CodeBlock codeBlock = propertyAnnotation.doGetDeserializeCodeBlock(context);
        String expectedCode = "" +
                "if (jsonObject.containsKey(\"user\")) {\n" +
                "  java.lang.Object jsonAttribute = jsonObject.get(\"user\");\n" +
                "  if (!(jsonAttribute instanceof java.util.Map)) {\n" +
                "    cd.go.jrepresenter.JsonParseException.throwBadJsonType(\"user\", java.util.Map.class, jsonObject);\n" +
                "  }\n" +
                "  com.tw.User deserializedJsonAttribute = (java.util.Map) jsonAttribute;\n" +
                "  com.tw.User modelAttribute = gen.com.tw.UserMapper.fromJSON((java.util.Map) deserializedJsonAttribute, requestContext);\n" +
                "  model.setTriggeredBy(modelAttribute);\n" +
                "}\n";
        assertThat(codeBlock.toString()).isEqualTo(expectedCode);
    }

    @Test
    public void shouldGenerateCodeToSerializeWithTargetWithGetter() throws Exception {
        Attribute modelAttribute = new Attribute("fname", STRING_CLASS);
        Attribute jsonAttribute = new Attribute("firstName", STRING_CLASS);
        PropertyAnnotation propertyAnnotation = PropertyAnnotationBuilder.aPropertyAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .withGetterClassName(FNAME_GETTER)
                .build();

        CodeBlock codeBlock = propertyAnnotation.getSerializeCodeBlock(null, "json");
        assertThat(codeBlock.toString()).isEqualTo("json.put(\"first_name\", new com.tw.FNameGetter().apply(value, requestContext));\n");
    }

    @Test
    public void shouldGenerateCodeToSerializeWithTargetWithGetterAndSerializer() throws Exception {
        Attribute modelAttribute = new Attribute("fname", STRING_CLASS);
        Attribute jsonAttribute = new Attribute("firstName", STRING_CLASS);
        PropertyAnnotation propertyAnnotation = PropertyAnnotationBuilder.aPropertyAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .withSerializerClassName(CASE_INSENSITIVE_STRING_SERIALIZER)
                .withGetterClassName(FNAME_GETTER)
                .build();

        CodeBlock codeBlock = propertyAnnotation.getSerializeCodeBlock(null, "json");
        assertThat(codeBlock.toString()).isEqualTo("json.put(\"first_name\", new com.tw.CaseInsensitiveStringSerializer().apply(new com.tw.FNameGetter().apply(value, requestContext), requestContext));\n");
    }

    @Test
    public void shouldGenerateCodeToDeserializeWithSetter() {
        Attribute modelAttribute = new Attribute("triggeredBy", USER_MODEL);
        Attribute jsonAttribute = new Attribute("user", STRING_CLASS);
        PropertyAnnotation propertyAnnotation = PropertyAnnotationBuilder.aPropertyAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .withSetterClassName(TRIGGERED_BY_SETTER)
                .build();

        CodeBlock codeBlock = propertyAnnotation.doGetDeserializeCodeBlock(null);
        String expectedCode = "" +
                "if (jsonObject.containsKey(\"user\")) {\n" +
                "  java.lang.Object jsonAttribute = jsonObject.get(\"user\");\n" +
                "  if (!(jsonAttribute instanceof java.lang.String)) {\n" +
                "    cd.go.jrepresenter.JsonParseException.throwBadJsonType(\"user\", java.lang.String.class, jsonObject);\n" +
                "  }\n" +
                "  com.tw.User deserializedJsonAttribute = (java.lang.String) jsonAttribute;\n" +
                "  com.tw.User modelAttribute = (com.tw.User) deserializedJsonAttribute;\n" +
                "  new com.tw.TriggeredBySetter().accept(model, modelAttribute, requestContext);\n" +
                "}\n";
        assertThat(codeBlock.toString()).isEqualTo(expectedCode);
    }

    @Test
    public void shouldGenerateCodeToDeserializeWithSetterAndDeserializer() {
        Attribute modelAttribute = new Attribute("triggeredBy", USER_MODEL);
        Attribute jsonAttribute = new Attribute("user", STRING_CLASS);
        PropertyAnnotation propertyAnnotation = PropertyAnnotationBuilder.aPropertyAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .withDeserializerClassName(CASE_INSENSITIVE_STRING_DESERIALIZER)
                .withSetterClassName(TRIGGERED_BY_SETTER)
                .build();

        CodeBlock codeBlock = propertyAnnotation.doGetDeserializeCodeBlock(null);
        String expectedCode = "" +
                "if (jsonObject.containsKey(\"user\")) {\n" +
                "  java.lang.Object jsonAttribute = jsonObject.get(\"user\");\n" +
                "  if (!(jsonAttribute instanceof java.lang.String)) {\n" +
                "    cd.go.jrepresenter.JsonParseException.throwBadJsonType(\"user\", java.lang.String.class, jsonObject);\n" +
                "  }\n" +
                "  com.tw.User deserializedJsonAttribute = new com.tw.CaseInsensitiveStringDeserializer().apply((java.lang.String) jsonAttribute, requestContext);\n" +
                "  com.tw.User modelAttribute = (com.tw.User) deserializedJsonAttribute;\n" +
                "  new com.tw.TriggeredBySetter().accept(model, modelAttribute, requestContext);\n" +
                "}\n";

        assertThat(codeBlock.toString()).isEqualTo(expectedCode);
    }

    @Test
    public void shouldGenerateCodeToDeserializeWithSetterAndRepresenter() {

    }


}
