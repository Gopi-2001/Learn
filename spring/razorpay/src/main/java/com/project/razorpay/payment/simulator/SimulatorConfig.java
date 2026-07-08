package com.project.razorpay.payment.simulator;

import com.project.razorpay.common.enums.ChaosMode;
import com.project.razorpay.vault.entity.CardToken;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "payment.simulator")
public class SimulatorConfig {

    private Integer pollIntervals = 2000;
    private String chaosMode = ChaosMode.NORMAL.toString();

    private Map<String, MethodSimulatorConfig> methods = new HashMap<>();

    @Getter
    @Setter
    public static class MethodSimulatorConfig{

        private Integer minDelaySeconds = 1;
        private Integer maxDelaySeconds = 5;
        private Integer successRate = 88;

    }


}
