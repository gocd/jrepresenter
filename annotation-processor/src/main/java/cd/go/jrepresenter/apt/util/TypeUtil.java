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

package cd.go.jrepresenter.apt.util;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.List;
import java.util.Map;

public class TypeUtil {
    public static ParameterizedTypeName listOf(TypeName type) {
        return ParameterizedTypeName.get(ClassName.get(List.class), type);
    }

    public static ParameterizedTypeName listOf(Class<?> clazz) {
        return ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(clazz));
    }

    public static ParameterizedTypeName mapOf(Class<? extends Map> clazz, TypeName keyType, TypeName valueType) {
        return ParameterizedTypeName.get(ClassName.get(clazz), keyType, valueType);
    }
}
