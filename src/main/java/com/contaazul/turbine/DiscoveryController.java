package com.contaazul.turbine;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.config.DynamicPropertyFactory;
import com.netflix.turbine.discovery.Instance;

@RestController
public class DiscoveryController {

	@GetMapping("/discovery")
	public void greeting(HttpServletResponse resp) throws IOException {
		String suffix = DynamicPropertyFactory.getInstance().getStringProperty( "turbine.instanceUrlSuffix", "" )
				.getValue();

		// Actual logic goes here.
		PrintWriter out = resp.getWriter();
		out.println( "<h1>Hystrix Endpoints:</h1>" );
		try {
			for (Instance instance : new KubernetesInstanceDiscovery().getInstanceList()) {
				out.println(
						"<h3>http://" + instance.getHostname() + suffix + " " + instance.getCluster() + " up: " + instance
								.isUp() + "</h3>" );
			}
		} catch (Throwable t) {
			t.printStackTrace( out );
		}
		out.flush();
	}
}