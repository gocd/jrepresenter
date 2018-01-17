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
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class RepresentsSubClassesAnnotationTest {

    private ClassName guestUserRepresenterClass = ClassName.bestGuess("com.tw.representers.GuestUserRepresenter");
    private ClassName adminUserRepresenterClass = ClassName.bestGuess("com.tw.representers.AdminUserRepresenter");
    private ClassName guestUserClass = ClassName.bestGuess("com.tw.GuestUser");
    private ClassName adminUserClass = ClassName.bestGuess("com.tw.AdminUser");
    private ClassToAnnotationMap context;
    private SubClassInfoAnnotation guestSubClassInfo;
    private SubClassInfoAnnotation adminSubClassInfo;
    private RepresenterAnnotation userRepresenterAnnotation;

    @Before
    public void setUp() throws Exception {
        context = new ClassToAnnotationMap();

        userRepresenterAnnotation = RepresenterAnnotationBuilder.aRepresenterAnnotation()
                .withRepresenterClass(TestConstants.USER_REPRESENTER_CLASS)
                .withModelClass(TestConstants.USER_MODEL)
                .withLinksProviderClass(null)
                .withSkipDeserialize(false)
                .withSkipSerialize(false)
                .build();
        context.add(userRepresenterAnnotation);

        context.add(RepresenterAnnotationBuilder.aRepresenterAnnotation()
                .withRepresenterClass(guestUserRepresenterClass)
                .withModelClass(guestUserClass)
                .withLinksProviderClass(null)
                .withSkipDeserialize(false)
                .withSkipSerialize(false)
                .build());

        context.add(RepresenterAnnotationBuilder.aRepresenterAnnotation()
                .withRepresenterClass(adminUserRepresenterClass)
                .withModelClass(adminUserClass)
                .withLinksProviderClass(null)
                .withSkipDeserialize(false)
                .withSkipSerialize(false)
                .build());
        guestSubClassInfo = new SubClassInfoAnnotation(guestUserRepresenterClass, "guest", null);
        adminSubClassInfo = new SubClassInfoAnnotation(adminUserRepresenterClass, "admin", null);
    }

    @Test
    public void shouldGetSerializeCodeBlockWhenNestedUnderAttribute() {
        RepresentsSubClassesAnnotation representsSubClassesAnnotation = new RepresentsSubClassesAnnotation("type",
                "attributes", Arrays.asList(guestSubClassInfo, adminSubClassInfo));

        CodeBlock serializeCodeBlock = representsSubClassesAnnotation.getSerializeCodeBlock(context);

        String expectedCode = "" +
                "java.util.Map subClassProperties = null;\n" +
                "if (value instanceof com.tw.GuestUser) {\n" +
                "  subClassProperties = gen.com.tw.representers.GuestUserMapper.toJSON((com.tw.GuestUser) value, requestContext);\n" +
                "}\n" +
                "else if (value instanceof com.tw.AdminUser) {\n" +
                "  subClassProperties = gen.com.tw.representers.AdminUserMapper.toJSON((com.tw.AdminUser) value, requestContext);\n" +
                "}\n" +
                "jsonObject.put(\"attributes\", subClassProperties);\n";
        assertThat(serializeCodeBlock.toString()).isEqualTo(expectedCode);
    }

    @Test
    public void shouldGetSerializeCodeBlockWhenNotNestedUnderAttribute() {
        RepresentsSubClassesAnnotation representsSubClassesAnnotation = new RepresentsSubClassesAnnotation("type",
                "", Arrays.asList(guestSubClassInfo, adminSubClassInfo));

        CodeBlock serializeCodeBlock = representsSubClassesAnnotation.getSerializeCodeBlock(context);

        String expectedCode = "" +
                "java.util.Map subClassProperties = null;\n" +
                "if (value instanceof com.tw.GuestUser) {\n" +
                "  subClassProperties = gen.com.tw.representers.GuestUserMapper.toJSON((com.tw.GuestUser) value, requestContext);\n" +
                "}\n" +
                "else if (value instanceof com.tw.AdminUser) {\n" +
                "  subClassProperties = gen.com.tw.representers.AdminUserMapper.toJSON((com.tw.AdminUser) value, requestContext);\n" +
                "}\n" +
                "jsonObject.putAll(subClassProperties);\n";
        assertThat(serializeCodeBlock.toString()).isEqualTo(expectedCode);
    }

    @Test
    public void shouldGetDeserializeCodeBlockWhenNestedUnderAttribute() {
        RepresentsSubClassesAnnotation representsSubClassesAnnotation = new RepresentsSubClassesAnnotation("type",
                "attributes", Arrays.asList(guestSubClassInfo, adminSubClassInfo));

        CodeBlock deserializeCodeBlock = representsSubClassesAnnotation.getDeserializeCodeBlock(context, userRepresenterAnnotation);

        String expectedCode = "" +
                "com.tw.User model = null;\n" +
                "java.lang.String type = (java.lang.String) jsonObject.get(\"type\");\n" +
                "if (\"guest\".equals(type)) {\n" +
                "  model = gen.com.tw.representers.GuestUserMapper.fromJSON((java.util.Map) jsonObject.get(\"attributes\"), requestContext);\n" +
                "}\n" +
                "else if (\"admin\".equals(type)) {\n" +
                "  model = gen.com.tw.representers.AdminUserMapper.fromJSON((java.util.Map) jsonObject.get(\"attributes\"), requestContext);\n" +
                "}\n" +
                "else {\n" +
                "  throw new java.lang.RuntimeException(\"Could not find any subclass for specified type. Possible values are: guest,admin\");\n" +
                "}\n";
        assertThat(deserializeCodeBlock.toString()).isEqualTo(expectedCode);
    }

    @Test
    public void shouldGetDeserializeCodeBlockWhenNotNestedUnderAttribute() {
        RepresentsSubClassesAnnotation representsSubClassesAnnotation = new RepresentsSubClassesAnnotation("type",
                "", Arrays.asList(guestSubClassInfo, adminSubClassInfo));

        CodeBlock deserializeCodeBlock = representsSubClassesAnnotation.getDeserializeCodeBlock(context, userRepresenterAnnotation);

        String expectedCode = "" +
                "com.tw.User model = null;\n" +
                "java.lang.String type = (java.lang.String) jsonObject.get(\"type\");\n" +
                "if (\"guest\".equals(type)) {\n" +
                "  model = gen.com.tw.representers.GuestUserMapper.fromJSON(jsonObject, requestContext);\n" +
                "}\n" +
                "else if (\"admin\".equals(type)) {\n" +
                "  model = gen.com.tw.representers.AdminUserMapper.fromJSON(jsonObject, requestContext);\n" +
                "}\n" +
                "else {\n" +
                "  throw new java.lang.RuntimeException(\"Could not find any subclass for specified type. Possible values are: guest,admin\");\n" +
                "}\n";
        assertThat(deserializeCodeBlock.toString()).isEqualTo(expectedCode);
    }

}
