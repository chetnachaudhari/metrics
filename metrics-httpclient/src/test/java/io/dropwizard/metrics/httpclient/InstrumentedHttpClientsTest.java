package io.dropwizard.metrics.httpclient;

import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.junit.Before;
import org.junit.Test;

import io.dropwizard.metrics.MetricName;
import io.dropwizard.metrics.MetricRegistry;
import io.dropwizard.metrics.MetricRegistryListener;
import io.dropwizard.metrics.Timer;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class InstrumentedHttpClientsTest {
    private final HttpClientMetricNameStrategy metricNameStrategy =
            mock(HttpClientMetricNameStrategy.class);
    private final MetricRegistryListener registryListener =
            mock(MetricRegistryListener.class);
    private final MetricRegistry metricRegistry = new MetricRegistry();
    private final HttpClient client =
            InstrumentedHttpClients.createDefault(metricRegistry, metricNameStrategy);

    @Before
    public void setUp() throws Exception {
        metricRegistry.addListener(registryListener);
    }

    @Test
    public void registersExpectedMetricsGivenNameStrategy() throws Exception {
        final HttpGet get = new HttpGet("http://example.com?q=anything");
        final MetricName metricName = MetricName.build("some.made.up.metric.name");

        when(metricNameStrategy.getNameFor(anyString(), any(HttpRequest.class)))
                .thenReturn(metricName);

        client.execute(get);

        verify(registryListener).onTimerAdded(eq(metricName), any(Timer.class));
    }
}
