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

package cd.go.jrepresenter;

import java.net.MalformedURLException;
import java.net.URL;

public class RequestContext {

    private final String host;
    private final int port;
    private final String protocol;

    public RequestContext(RequestContext requestContext) {
        this.host = requestContext.host;
        this.port = requestContext.port;
        this.protocol = requestContext.protocol;
    }

    public RequestContext(String protocol, String host, int port) {
        this.host = host;
        this.port = protocol.equalsIgnoreCase("https") && port == 443 || protocol.equals("http") && port == 80 ? -1 : port;
        this.protocol = protocol;
    }

    public Link build(String name, String template, Object... args) {
        try {
            return new Link(name, new URL(protocol, host, port, String.format(template, args)).toExternalForm());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
