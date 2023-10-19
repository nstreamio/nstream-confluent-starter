package nstream.starter;

import nstream.adapter.common.ingress.ValueAssembler;
import nstream.adapter.confluent.ConfluentIngestingPatch;
import nstream.adapter.confluent.ConfluentAdapterUtils;
import org.apache.kafka.clients.consumer.ConsumerRecords;

/**
 * A skeletal {@link ConfluentIngestingPatch} extension.
 * <p>"No-code" type ingesting agents configure {@code KafkaIngestingPatch} in
 * {@code server.recon}. "Low-code" variations first create a base class that
 * extends {@code ConfluentIngestingPatch}, then utilize that class in {@code
 * server.recon}. You'll notice that this is identical to how {@link
 * PolarityMemberAgent} and its corresponding {@code GroupPatch} defined in
 * {@code server.recon} operate -- after all, ingesting agents are still web
 * agents.
 */
public class VehiclesIngestingAgent extends ConfluentIngestingPatch<Integer, String> {

  @Override
  protected long nextBackoff(ConsumerRecords<Integer, String> records, long oldBackoff) {
    if (!records.isEmpty()) {
      return 0L;
    } else if (oldBackoff < 0) {
      return 500L;
    } else if (oldBackoff < 4000) {
      // Exponential backoff until 4 seconds
      return Math.min(oldBackoff * 2, 4000L);
    } else {
      // Linear backoff subsequently, to a max of 8 seconds
      return Math.min(oldBackoff + 1000L, 8000L);
    }
  }

  @Override
  protected void stageReception() {
    loadSettings("confluentIngressConf");
    this.kafkaConsumer = ConfluentAdapterUtils.createConsumer(this.ingressSettings);
    System.out.println(this.ingressSettings);
    this.kafkaConsumer.subscribe(this.ingressSettings.topics());
    this.keyAssembler = ValueAssembler.create(this.ingressSettings.keyMolder());
    this.valueAssembler = ValueAssembler.create(this.ingressSettings.valueMolder());
    this.pollTimer = scheduleWithInformedBackoff(this::pollTimer,
        this.ingressSettings.firstFetchDelayMillis(),
        this::nextBackoff,
        i -> !i.isEmpty(),
        500L,
        this::poll,
        this::ingest);
  }

}
