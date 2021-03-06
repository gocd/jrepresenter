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

package cd.go.jrepresenter.examples.representers;

import cd.go.jrepresenter.annotations.Collection;
import cd.go.jrepresenter.annotations.Property;
import cd.go.jrepresenter.annotations.Represents;
import cd.go.jrepresenter.examples.CaseInsensitiveString;
import cd.go.jrepresenter.examples.Pipeline;
import cd.go.jrepresenter.examples.Stage;
import cd.go.jrepresenter.examples.serializers.CaseInsensitiveStringDeserializer;
import cd.go.jrepresenter.examples.serializers.CaseInsensitiveStringSerializer;

import java.util.List;
import java.util.Map;

@Represents(value = Pipeline.class, linksProvider = PipelineUrlBuilder.class)
public interface PipelineRepresenter {

    @Property(serializer = CaseInsensitiveStringSerializer.class, deserializer = CaseInsensitiveStringDeserializer.class, modelAttributeType = CaseInsensitiveString.class)
    public String name();

    @Collection(representer = StagesRepresenter.class, modelAttributeType = Stage.class)
    public List<Map> stages();

}
