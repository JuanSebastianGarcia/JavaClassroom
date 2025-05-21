package co.uniquindio.ingesis.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Implements a readiness health check to verify if the service is ready to
 * handle requests.
 */
@Readiness
@ApplicationScoped
public class ReadinessCheck implements HealthCheck {

    /**
     * Executes the readiness check and returns the service readiness status.
     *
     * @return HealthCheckResponse indicating the service is ready
     */
    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.up("Service is ready");
    }
}
