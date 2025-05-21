package co.uniquindio.ingesis.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Implements a liveness health check to verify if the service is operational.
 */
@Liveness
@ApplicationScoped
public class ServiceHealthCheck implements HealthCheck {

    /**
     * Performs the liveness check and returns the health status.
     *
     * @return HealthCheckResponse indicating the service is up
     */
    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.up("Service is available");
    }
}
