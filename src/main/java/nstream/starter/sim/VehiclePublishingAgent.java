package nstream.starter.sim;

import nstream.adapter.confluent.ConfluentPublishingAgent;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import nstream.adapter.common.provision.ProvisionLoader;
import swim.api.SwimLane;
import swim.api.lane.ValueLane;
import swim.json.Json;
import swim.structure.Value;

public class VehiclePublishingAgent extends ConfluentPublishingAgent<Value, Integer, String> {

  @SwimLane("toPublish")
  ValueLane<Value> toPublish = this.<Value>valueLane()
      .didSet((n, o) -> {
        if (n != null && n.isDistinct() && !n.equals(o)) {
          final ProducerRecord<Integer, String> result = createPublishable(n);
          publishAsync(result);
        }
      });

  @Override
  protected ProducerRecord<Integer, String> createPublishable(Value state) {
    return new ProducerRecord<>("json-topic",
        getProp("id").intValue(),
        Json.toString(state));
  }

  @Override
  public void didStart() {
    super.didStart();
    assignProducer(ProvisionLoader.<Producer<Integer, String>>
        getProvision("vehicles-confluent-producer").value());
  }

}
