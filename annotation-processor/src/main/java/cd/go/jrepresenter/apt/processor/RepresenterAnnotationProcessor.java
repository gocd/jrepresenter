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

package cd.go.jrepresenter.apt.processor;

import cd.go.jrepresenter.annotations.*;
import cd.go.jrepresenter.annotations.Collection;
import cd.go.jrepresenter.apt.models.*;
import cd.go.jrepresenter.apt.util.DebugStatement;
import cd.go.jrepresenter.apt.util.TypeUtil;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.MirroredTypeException;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cd.go.jrepresenter.apt.models.BaseAnnotation.TRUE_FUNCTION;
import static javax.tools.Diagnostic.Kind.NOTE;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions("jrepresenterDebug")
public class RepresenterAnnotationProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new LinkedHashSet<>(Arrays.asList(
                Represents.class.getName(),
                Property.class.getName(),
                Collection.class.getName(),
                Errors.class.getName()
        ));
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        if (processingEnv.getOptions().getOrDefault("jrepresenterDebug", "false").equals("true")) {
            DebugStatement.enable();
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        ClassToAnnotationMap classToAnnotationMap = new ClassToAnnotationMap();

        roundEnv.getElementsAnnotatedWith(Represents.class).forEach(representerClass -> {
            Represents represents = representerClass.getAnnotation(Represents.class);
            ClassName modelClassName = (ClassName) getClassNameFromAnnotationMethod(represents, "value");
            ClassName linksBuilderClassName = (ClassName) getClassNameFromAnnotationMethod(represents, "linksProvider");
            ClassName deserializerClassName = (ClassName) getClassNameFromAnnotationMethod(represents, "deserializer");
            ClassName representerClassName = ClassName.bestGuess(((TypeElement) representerClass).getQualifiedName().toString());
            RepresentsSubClasses annotation = representerClass.getAnnotation(RepresentsSubClasses.class);
            Optional<RepresentsSubClassesAnnotation> representsSubClassesAnnotation = extractSubClassInfo(annotation);

            classToAnnotationMap.add(RepresenterAnnotationBuilder.aRepresenterAnnotation()
                    .withRepresenterClass(representerClassName)
                    .withModelClass(modelClassName)
                    .withLinksProviderClass(linksBuilderClassName)
                    .withSkipDeserialize(represents.skipDeserialize())
                    .withSkipSerialize(represents.skipSerialize())
                    .withSubClassInfo(representsSubClassesAnnotation)
                    .withDeserializerClass(deserializerClassName)
                    .build());
        });

        roundEnv.getElementsAnnotatedWith(Property.class).forEach(method -> {
            Property annotation = method.getAnnotation(Property.class);

            String jsonAttributeName = getJsonAttributeName(method);
            String modelAttributeName = getModelAttributeName(method, annotation);

            TypeName modelAttributeType = getClassNameFromAnnotationMethod(annotation, "modelAttributeType");
            TypeName jsonAttributeType = ClassName.get(((ExecutableType) method.asType()).getReturnType());

            PropertyAnnotation propertyAnnotation = PropertyAnnotationBuilder.aPropertyAnnotation()
                    .withModelAttribute(new Attribute(modelAttributeName, modelAttributeType))
                    .withJsonAttribute(new Attribute(jsonAttributeName, jsonAttributeType))
                    .withEmbedded(annotation.embedded())
                    .withSerializerClassName(getClassNameFromAnnotationMethod(annotation, "serializer"))
                    .withDeserializerClassName(getClassNameFromAnnotationMethod(annotation, "deserializer"))
                    .withRepresenterClassName(getClassNameFromAnnotationMethod(annotation, "representer"))
                    .withGetterClassName(getClassNameFromAnnotationMethod(annotation, "getter"))
                    .withSetterClassName(getClassNameFromAnnotationMethod(annotation, "setter"))
                    .withSkipParse(getClassNameFromAnnotationMethod(annotation, "skipParse"))
                    .withSkipRender(getClassNameFromAnnotationMethod(annotation, "skipRender"))
                    .withRenderNull(annotation.renderNull())
                    .build();

            classToAnnotationMap.addAnnotatedMethod(ClassName.get(method.getEnclosingElement().asType()), propertyAnnotation);
        });

        roundEnv.getElementsAnnotatedWith(Errors.class).forEach(method -> {
            Errors annotation = method.getAnnotation(Errors.class);

            PropertyAnnotation propertyAnnotation = PropertyAnnotationBuilder.aPropertyAnnotation()
                    .withModelAttribute(new Attribute("errors", TypeUtil.mapOf(Map.class, ClassName.get(String.class), TypeUtil.listOf(String.class))))
                    .withJsonAttribute(new Attribute("errors", TypeUtil.mapOf(Map.class, ClassName.get(String.class), TypeUtil.listOf(String.class))))
                    .withGetterClassName(getClassNameFromAnnotationMethod(annotation, "getter"))
                    .withSkipParse(TRUE_FUNCTION)
                    .withSkipRender(getClassNameFromAnnotationMethod(annotation, "skipRender"))
                    .build();

            classToAnnotationMap.addAnnotatedMethod(ClassName.get(method.getEnclosingElement().asType()), propertyAnnotation);
        });

        roundEnv.getElementsAnnotatedWith(Collection.class).forEach(method -> {
            Collection annotation = method.getAnnotation(Collection.class);

            String jsonAttributeName = getJsonAttributeName(method);
            String modelAttributeName = getModelAttributeName(method, annotation);

            TypeName modelAttributeType = getClassNameFromAnnotationMethod(annotation, "modelAttributeType");
            TypeName jsonAttributeType = ClassName.get(((ExecutableType) method.asType()).getReturnType());

            CollectionAnnotation propertyAnnotation = CollectionAnnotationBuilder.aCollectionAnnotation()
                    .withRepresenterClassName(getClassNameFromAnnotationMethod(annotation, "representer"))
                    .withModelAttribute(new Attribute(modelAttributeName, modelAttributeType))
                    .withJsonAttribute(new Attribute(jsonAttributeName, jsonAttributeType))
                    .withEmbedded(annotation.embedded())
                    .withSerializerClassName(getClassNameFromAnnotationMethod(annotation, "serializer"))
                    .withDeserializerClassName(getClassNameFromAnnotationMethod(annotation, "deserializer"))
                    .withGetterClassName(getClassNameFromAnnotationMethod(annotation, "getter"))
                    .withSetterClassName(getClassNameFromAnnotationMethod(annotation, "setter"))
                    .withSkipParse(getClassNameFromAnnotationMethod(annotation, "skipParse"))
                    .withSkipRender(getClassNameFromAnnotationMethod(annotation, "skipRender"))
                    .withRenderEmpty(annotation.renderEmpty())
                    .build();

            ClassName representerClass = ClassName.bestGuess(method.getEnclosingElement().toString());

            classToAnnotationMap.addAnnotatedMethod(representerClass, propertyAnnotation);
        });

        writeFiles(classToAnnotationMap);

        return true;
    }

    private Optional<RepresentsSubClassesAnnotation> extractSubClassInfo(RepresentsSubClasses annotation) {
        if (annotation == null) {
            return Optional.empty();
        }
        RepresentsSubClasses.SubClassInfo[] subClassInfos = annotation.subClasses();
        List<SubClassInfoAnnotation> subClassInfoAnnotations = Stream.of(subClassInfos).map(subClassInfo -> {
            String value = subClassInfo.value();
            TypeName representer = getClassNameFromAnnotationMethod(subClassInfo, "representer");
            TypeName linksProvider = getClassNameFromAnnotationMethod(subClassInfo, "linksProvider");
            return new SubClassInfoAnnotation(representer, value, linksProvider);
        }).collect(Collectors.toList());
        return Optional.of(
                new RepresentsSubClassesAnnotation(annotation.property(), annotation.nestedUnder(), subClassInfoAnnotations));
    }

    private TypeName getClassNameFromAnnotationMethod(Annotation annotation, String methodName) {
        try {
            Method method = annotation.getClass().getMethod(methodName);
            Class<?> type = (Class<?>) method.invoke(annotation);
            return TypeName.get(type);
        } catch (MirroredTypeException mte) {
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            return TypeName.get(classTypeMirror);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            if (e.getCause() instanceof MirroredTypeException) {
                DeclaredType classTypeMirror = (DeclaredType) ((MirroredTypeException) e.getCause()).getTypeMirror();
                return TypeName.get(classTypeMirror);
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    private String getJsonAttributeName(Element method) {
        return method.getSimpleName().toString();
    }

    private String getModelAttributeName(Element method, Property annotation) {
        String modelAttributeName = annotation.modelAttributeName();

        if (modelAttributeName == null || modelAttributeName.trim().equals("")) {
            modelAttributeName = method.getSimpleName().toString();
        }
        return modelAttributeName;
    }

    private String getModelAttributeName(Element method, Collection annotation) {
        String modelAttributeName = annotation.modelAttributeName();

        if (modelAttributeName == null || modelAttributeName.trim().equals("")) {
            modelAttributeName = method.getSimpleName().toString();
        }
        return modelAttributeName;
    }

    private void writeFiles(ClassToAnnotationMap classToAnnotationMap) {
        if (classToAnnotationMap.isEmpty()) {
            return;
        }

        try {
            writeConstantsFile(classToAnnotationMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        classToAnnotationMap.forEach((representerAnnotation) -> {
            try {
                writeMapperFile(classToAnnotationMap, representerAnnotation);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void writeConstantsFile(ClassToAnnotationMap context) throws IOException {
        MapperJavaConstantsFile javaSourceFile = new MapperJavaConstantsFile(context);
        processingEnv.getMessager().printMessage(NOTE, "Generating representer for " + javaSourceFile.getModelClass());
        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(javaSourceFile.getModelClass().toString());

        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            out.append(javaSourceFile.toSource());
        }

    }

    private void writeMapperFile(ClassToAnnotationMap context, RepresenterAnnotation representerAnnotation) throws IOException {
        MapperJavaSourceFile javaSourceFile = new MapperJavaSourceFile(representerAnnotation, context);
        processingEnv.getMessager().printMessage(NOTE, "Generating representer for " + javaSourceFile.representerAnnotation.getModelClass() + " into " + javaSourceFile.representerAnnotation.mapperClassImplRelocated());
        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(javaSourceFile.representerAnnotation.mapperClassImplRelocated().toString());

        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            out.append(javaSourceFile.toSource());
        }
    }

}
