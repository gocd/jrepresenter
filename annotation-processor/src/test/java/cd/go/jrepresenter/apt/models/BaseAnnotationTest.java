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
import org.assertj.core.api.Assertions;
import org.junit.Test;

import static cd.go.jrepresenter.apt.models.TestConstants.STRING_CLASS;

public class BaseAnnotationTest {

    @Test
    public void shouldRenderCodeBlockAsIsIfSkipParseAndRenderAreFalse() {
        Attribute modelAttribute = new Attribute("fname", STRING_CLASS);
        Attribute jsonAttribute = new Attribute("firstName", STRING_CLASS);
        BaseAnnotation baseAnnotation = BaseAnnotationBuilder.aBaseAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .withSkipParse(BaseAnnotation.FALSE_BI_FUNCTION)
                .withSkipRender(BaseAnnotation.FALSE_BI_FUNCTION)
                .build();
        Assertions.assertThat(baseAnnotation.getSerializeCodeBlock(null, "jsonObject").toString()).isEqualTo("jsonObject.put(\"first_name\", /* apply some serializer here */);\n");
        Assertions.assertThat(baseAnnotation.getDeserializeCodeBlock(null).toString()).isEqualTo("" +
                "if (jsonObject.containsKey(\"first_name\")) {\n" +
                "  /* apply some deserializer here */java.lang.String modelAttribute = (java.lang.String) deserializedJsonAttribute;\n" +
                "  model.setFname(modelAttribute);\n" +
                "}\n");
    }

    @Test
    public void shouldRenderNothingIfSkipParseAndRenderAreTrue() {
        BaseAnnotation baseAnnotation = BaseAnnotationBuilder.aBaseAnnotation()
                .withSkipParse(BaseAnnotation.TRUE_BI_FUNCTION)
                .withSkipRender(BaseAnnotation.TRUE_BI_FUNCTION)
                .build();

        Assertions.assertThat(baseAnnotation.getSerializeCodeBlock(null, null).toString()).isEqualTo("");
        Assertions.assertThat(baseAnnotation.getDeserializeCodeBlock(null).toString()).isEqualTo("");
    }

    @Test
    public void shouldRenderConditionalIfSkipParseAndRenderAreSetToAnyOtherFunction() {
        Attribute modelAttribute = new Attribute("fname", STRING_CLASS);
        Attribute jsonAttribute = new Attribute("firstName", STRING_CLASS);

        BaseAnnotation baseAnnotation = BaseAnnotationBuilder.aBaseAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .withSkipParse(ClassName.bestGuess("com.tw.SkipFooParse"))
                .withSkipRender(ClassName.bestGuess("com.tw.SkipFooRender"))
                .build();

        Assertions.assertThat(baseAnnotation.getSerializeCodeBlock(null, "jsonObject").toString()).isEqualTo("" +
                "if (!gen.cd.go.jrepresenter.Constants.SkipRenderers.SKIP_FOO_RENDER.apply(value, requestContext)) {\n" +
                "  jsonObject.put(\"first_name\", /* apply some serializer here */);\n" +
                "}\n");

        Assertions.assertThat(baseAnnotation.getDeserializeCodeBlock(null).toString()).isEqualTo("" +
                "if (!gen.cd.go.jrepresenter.Constants.SkipParsers.SKIP_FOO_PARSE.apply(value, requestContext)) {\n" +
                "  if (jsonObject.containsKey(\"first_name\")) {\n" +
                "    /* apply some deserializer here */java.lang.String modelAttribute = (java.lang.String) deserializedJsonAttribute;\n" +
                "    model.setFname(modelAttribute);\n" +
                "  }\n" +
                "}\n");
    }

    @Test
    public void shouldRenderGuardClauseIfRenderEmptyOrNullIsFalse() {
        Attribute modelAttribute = new Attribute("fname", STRING_CLASS);
        Attribute jsonAttribute = new Attribute("firstName", STRING_CLASS);

        BaseAnnotation baseAnnotation = BaseAnnotationBuilder.aBaseAnnotation()
                .withModelAttribute(modelAttribute)
                .withJsonAttribute(jsonAttribute)
                .withRenderEmptyOrNull(false)
                .build();

        Assertions.assertThat(baseAnnotation.getSerializeCodeBlock(null, "jsonObject").toString()).isEqualTo("" +
                "if (/* perform some conditional */) {\n" +
                "  jsonObject.put(\"first_name\", /* apply some serializer here */);\n" +
                "}\n");

    }
}
