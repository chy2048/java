package mypackage;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import mypackage.MyGangliaReporter;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;
import info.ganglia.gmetric4j.gmetric.GMetric;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableMetrics(proxyTargetClass = true)
public class SpringMetrics extends MetricsConfigurerAdapter {

	private MetricRegistry metricRegistry = new MetricRegistry();

	private HealthCheckRegistry healthCheckRegistry = new HealthCheckRegistry();

	@Value("${ganglia_ip}")
	protected String ganglia_ip;

	@Value("${ganglia_port}")
	protected int ganglia_port;

	@Value("${ganglia_period}")
	protected int ganglia_period;

	@PostConstruct
	public void init() {
		try {
			HexinGangliaReporter reporter = HexinGangliaReporter.forRegistry(metricRegistry)
					.build(new GMetric(ganglia_ip, ganglia_port, GMetric.UDPAddressingMode.MULTICAST, 1));
			reporter.start(ganglia_period, TimeUnit.SECONDS);
			registerReporter(reporter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Bean(name="metricRegistry")
	@Override
	public MetricRegistry getMetricRegistry() {
		return metricRegistry;
	}

	@Override
	public HealthCheckRegistry getHealthCheckRegistry() {
		return healthCheckRegistry;
	}

	@Override
	public void configureReporters(MetricRegistry metricRegistry) {
	}

}
