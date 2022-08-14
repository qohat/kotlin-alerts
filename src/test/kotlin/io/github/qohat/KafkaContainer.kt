package io.github.qohat

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.continuations.resource
import arrow.fx.coroutines.parZip
import arrow.fx.coroutines.release
import io.github.qohat.env.Env
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.withContext
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName
import java.lang.System.getProperty

object KafkaContainer {
    fun resource(): Resource<Env.Kafka> = resource {
        val network = Network.newNetwork()
        parZip(
            { zooKeeper(network).bind() },
            { kafka(network).bind() },
            { schemaRegistry(network).bind() }
        ) { _, kafka, registry ->
            Env.Kafka(
                server = kafka.bootstrapServers,
                schemaRegistryBaseUrl = "http://${registry.host}:${registry.getMappedPort(8081)}"
            )
        }
    }

    private fun kafka(network: Network) =
        resource {
            withContext(Dispatchers.IO) {
                val kafkaImage: DockerImageName =
                    if (getProperty("os.arch") == "aarch64") DockerImageName.parse("niciqy/cp-kafka-arm64:7.0.1")
                        .asCompatibleSubstituteFor("confluentinc/cp-kafka")
                    else DockerImageName.parse("confluentinc/cp-kafka:7.0.1")

                KafkaContainer(kafkaImage)
                    .withExposedPorts(9092, 9093)
                    .withNetwork(network)
                    .withNetworkAliases("broker")
                    .withEnv("KAFKA_HOST_NAME", "broker")
                    .withEnv("KAFKA_CONFLUENT_LICENSE_TOPIC_REPLICATION_FACTOR", "1")
                    .withEnv("KAFKA_CONFLUENT_BALANCER_TOPIC_REPLICATION_FACTOR", "1")
                    .withExternalZookeeper("zookeeper:2181")
                    .also { container -> runInterruptible(block = container::start) }
            }
        } release { kafka ->
            withContext(Dispatchers.IO) {
                kafka.close()
            }
        }

    private fun zooKeeper(network: Network) =
        resource {
            withContext(Dispatchers.IO) {
                val zooKeeperImage: DockerImageName =
                    if (getProperty("os.arch") == "aarch64") DockerImageName.parse("niciqy/cp-zookeeper-arm64:7.0.1")
                        .asCompatibleSubstituteFor("confluentinc/cp-zookeeper")
                    else DockerImageName.parse("confluentinc/cp-zookeeper:7.0.1")

                GenericContainer(zooKeeperImage)
                    .withNetwork(network)
                    .withNetworkAliases("zookeeper")
                    .withEnv("ZOOKEEPER_CLIENT_PORT", "2181")
                    .withEnv("ZOOKEEPER_TICK_TIME", "2000")
                    .also { container -> runInterruptible(block = container::start) }
            }
        } release { zookeeper ->
            withContext(Dispatchers.IO) {
                zookeeper.close()
            }
        }

    private fun schemaRegistry(network: Network) = resource {
        withContext(Dispatchers.IO) {
            val schemaRegistryImage: DockerImageName =
                if (getProperty("os.arch") == "aarch64") DockerImageName.parse("niciqy/cp-schema-registry-arm64:7.0.1")
                    .asCompatibleSubstituteFor("confluentinc/cp-schema-registry")
                else DockerImageName.parse("confluentinc/cp-schema-registry:7.0.1")

            GenericContainer(schemaRegistryImage)
                .withNetwork(network)
                .withExposedPorts(8081)
                .waitingFor(Wait.forHttp("/subjects"))
                .withNetworkAliases("schema-registry")
                .withEnv("SCHEMA_REGISTRY_HOST_NAME", "schema-registry")
                .withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS", "broker:9092")
                .withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:8081")
                .also { registry -> runInterruptible(block = registry::start) }
        }
    } release { registry ->
        withContext(Dispatchers.IO) {
            registry.close()
        }
    }
}