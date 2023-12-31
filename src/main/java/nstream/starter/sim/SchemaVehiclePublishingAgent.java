package nstream.starter.sim;

import nstream.adapter.common.provision.ProvisionLoader;
import nstream.adapter.confluent.ConfluentPublishingAgent;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import swim.api.SwimLane;
import swim.api.lane.ValueLane;
import swim.structure.Value;

public class SchemaVehiclePublishingAgent
    extends ConfluentPublishingAgent<Value, Integer, GenericRecord> {

  @SwimLane("toPublish")
  ValueLane<Value> toPublish = this.<Value>valueLane()
      .didSet((n, o) -> {
        if (n != null && n.isDistinct() && !n.equals(o)) {
          final ProducerRecord<Integer, GenericRecord> result = createPublishable(n);
          publishAsync(result);
        }
      });

  @Override
  protected ProducerRecord<Integer, GenericRecord> createPublishable(Value state) {
    return new ProducerRecord<>("vehicle-schema",
        getProp("id").intValue(),
        VehiclesSimulation.avroLocation(state));
  }

  @Override
  public void didStart() {
    super.didStart();
    assignProducer(ProvisionLoader.<Producer<Integer, GenericRecord>>
        getProvision("vehicles-confluent-producer").value());
  }

}
