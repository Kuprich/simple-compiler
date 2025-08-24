package org.coderun.compiler.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DockerConfig {


    @Bean
    public DockerClient docker(){
        String dockerHost = System.getenv().getOrDefault("DOCKER_HOST", "unix:///var/run/docker.sock");

        var config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerHost)
                .withDockerTlsVerify(false)
                .build();

        var httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .build();

        return DockerClientImpl.getInstance(config, httpClient);
    }

}
