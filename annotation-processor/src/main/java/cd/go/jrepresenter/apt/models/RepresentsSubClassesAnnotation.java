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

import cd.go.jrepresenter.apt.util.IfElseBuilder;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RepresentsSubClassesAnnotation {

    private final String property;
    private final String nestedUnder;
    private final List<SubClassInfoAnnotation> subClassInfos;

    public RepresentsSubClassesAnnotation(String property, String nestedUnder, List<SubClassInfoAnnotation> subClassInfos) {
        this.property = property;
        this.nestedUnder = nestedUnder;
        this.subClassInfos = subClassInfos;
    }

    public String getProperty() {
        return property;
    }

    public List<SubClassInfoAnnotation> getSubClassInfos() {
        return subClassInfos;
    }

    public String getNestedUnder() {
        return nestedUnder;
    }

    public CodeBlock getSerializeCodeBlock(ClassToAnnotationMap context) {
        CodeBlock.Builder builder = CodeBlock.builder();
        builder.addStatement("$T subClassProperties = null", Map.class);
        IfElseBuilder ifElseBuilder = new IfElseBuilder(builder);
        this.getSubClassInfos().forEach(subClassInfo -> {
            RepresenterAnnotation subClassRepresenter = context.findRepresenterAnnotation(subClassInfo.getRepresenterClass());
            TypeName subClass = subClassRepresenter.getModelClass();
            ifElseBuilder.addIf("value instanceof $T", subClass)
                    .withBody(subClassInfo.getSerializeCodeBlock(subClassRepresenter));

        });
        String nestedUnder = this.getNestedUnder();
        if (nestedUnder.isEmpty()) {
            builder.addStatement("$N.putAll(subClassProperties)", MapperJavaSourceFile.JSON_OBJECT_VAR_NAME);
        } else {
            builder.addStatement("$N.put($S, subClassProperties)", MapperJavaSourceFile.JSON_OBJECT_VAR_NAME, nestedUnder);
        }
        return builder.build();
    }

    public CodeBlock getDeserializeCodeBlock(ClassToAnnotationMap context, RepresenterAnnotation representerAnnotation) {
        CodeBlock.Builder builder = CodeBlock.builder();

        builder.addStatement("$T model = null", representerAnnotation.getModelClass());

        //TODO: Enhancement: get the right type instead of String.class?
        builder.addStatement("$T $N = ($T) $N.get($S)", String.class, getProperty(), String.class, MapperJavaSourceFile.JSON_OBJECT_VAR_NAME, getProperty());

        IfElseBuilder ifElseBuilder = new IfElseBuilder(builder);

        getSubClassInfos().forEach(subType -> ifElseBuilder
                .addIf("$S.equals($N)", subType.getValue(), getProperty())
                .withBody(modelFromSubClass(context, subType)));
        ifElseBuilder
                .addElse("throw new $T($S)", RuntimeException.class,
                        String.format("Could not find any subclass for specified %s. Possible values are: %s", getProperty(),
                                getAllPossiblePropertyValues()));
        return builder.build();
    }

    private CodeBlock modelFromSubClass(ClassToAnnotationMap context, SubClassInfoAnnotation subType) {
        RepresenterAnnotation subTypeRepresenterAnnotation = context.findRepresenterAnnotation(subType.getRepresenterClass());
        String nestedUnder = this.getNestedUnder();
        if (nestedUnder.isEmpty()) {
            return CodeBlock.builder()
                    .addStatement("model = $T.fromJSON($N, requestContext)", subTypeRepresenterAnnotation.mapperClassImplRelocated(), MapperJavaSourceFile.JSON_OBJECT_VAR_NAME)
                    .build();
        } else {
            return CodeBlock.builder()
                    .addStatement("model = $T.fromJSON(($T) $N.get($S), requestContext)",
                            subTypeRepresenterAnnotation.mapperClassImplRelocated(),
                            Map.class,
                            MapperJavaSourceFile.JSON_OBJECT_VAR_NAME,
                            nestedUnder)
                    .build();
        }
    }

    private String getAllPossiblePropertyValues() {
        return subClassInfos.stream().map(SubClassInfoAnnotation::getValue).collect(Collectors.joining(","));
    }

}
