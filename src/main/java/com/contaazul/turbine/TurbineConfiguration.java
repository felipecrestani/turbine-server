/*
 * Copyright (C) 2016 Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.contaazul.turbine;

import java.util.Arrays;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.netflix.turbine.discovery.InstanceDiscovery;
import com.netflix.turbine.streaming.servlet.TurbineStreamServlet;

@Configuration
public class TurbineConfiguration {

	private static final String DEFAULT_TURBINE_URL_MAPPING = "/turbine.stream";

	@Bean
	InstanceDiscovery instanceDiscovery() {
		return new KubernetesInstanceDiscovery();
	}

	@Bean
	TurbineLifecycle turbineContextListener(InstanceDiscovery instanceDiscovery) {
		return new TurbineLifecycle( instanceDiscovery );
	}

	@Bean
	public ServletRegistrationBean turbineServletRegistration() {
		ServletRegistrationBean registration = new ServletRegistrationBean( turbineStreamServlet() );
		registration.setUrlMappings( Arrays.asList( DEFAULT_TURBINE_URL_MAPPING ) );
		return registration;
	}

	@Bean
	public TurbineStreamServlet turbineStreamServlet() {
		return new TurbineStreamServlet();
	}

}
