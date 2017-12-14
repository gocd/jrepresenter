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

package cd.go.jrepresenter.examples;

import java.util.List;

public class Pipeline {

    private CaseInsensitiveString name;
    private List<Stage> stages;

    public Pipeline(String name, List<Stage> stages) {
        this.name = new CaseInsensitiveString(name);
        this.stages = stages;
    }

    public Pipeline() {
    }

    public CaseInsensitiveString getName() {
        return name;
    }

    public List<Stage> getStages() {
        return stages;
    }

    public void setName(CaseInsensitiveString name) {
        this.name = name;
    }

    public void setStages(List<Stage> stages) {
        this.stages = stages;
    }

    @Override
    public String toString() {
        return "Pipeline{" +
                "name=" + name +
                ", stages=" + stages +
                '}';
    }
}