package deserializer;

import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

public class BaseAvroDeserializer<T extends SpecificRecord> implements Deserializer {
    private final Schema schema;
    private final DecoderFactory decoderFactory;

    public BaseAvroDeserializer(Schema schema) {
        this.decoderFactory = DecoderFactory.get();
        this.schema = schema;
    }

    public BaseAvroDeserializer(DecoderFactory decoderFactory, Schema schema) {
        this.decoderFactory = decoderFactory;
        this.schema = schema;
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }

        try {
            SpecificDatumReader<T> reader = new SpecificDatumReader<>(schema);
            BinaryDecoder decoder = decoderFactory.binaryDecoder(data, null);

            return reader.read(null, decoder);

        } catch (IOException e) {
            throw new SerializationException("Avro serializationException", e);
        }
    }
}
