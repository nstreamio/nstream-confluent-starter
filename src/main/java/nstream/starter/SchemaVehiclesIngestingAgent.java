package nstream.starter;

import nstream.adapter.confluent.ConfluentIngestingPatch;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

/**
 * A skeletal {@link ConfluentIngestingPatch} extension.
 * <p>"No-code" type ingesting agents configure {@code ConfluentIngestingPatch} in
 * {@code server.recon}. "Low-code" variations first create a base class that
 * extends {@code ConfluentIngestingPatch}, then utilize that class in {@code
 * server.recon}. You'll notice that this is identical to how {@link
 * PolarityMemberAgent} and its corresponding {@code GroupPatch} defined in
 * {@code server.recon} operate -- after all, ingesting agents are still web
 * agents.
 * <p>A fresh clone of this repository uses the "no-code" variation; thus, this
 * class is unused until {@code schema/server.recon} is modified to point to it.
 */
public class SchemaVehiclesIngestingAgent extends ConfluentIngestingPatch<Integer, GenericRecord> {

  @Override
  protected void stageReception() {
    prepareConsumer();
    this.pollTimer = scheduleWithInformedBackoff(this::pollTimer,
        this.ingressSettings.firstFetchDelayMillis(),
        this::nextBackoff,
        i -> !i.isEmpty(),
        500L,
        this::poll,
        this::ingestBatch);
  }

  private long nextBackoff(ConsumerRecords<Integer, GenericRecord> records, long oldBackoff) {
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

}
