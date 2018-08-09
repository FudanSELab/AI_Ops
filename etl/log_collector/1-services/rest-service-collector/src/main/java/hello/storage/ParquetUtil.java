package hello.storage;

import java.io.IOException;
import hello.domain.Trace;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.column.ParquetProperties;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.example.data.GroupFactory;
import org.apache.parquet.example.data.simple.SimpleGroupFactory;
import org.apache.parquet.hadoop.ParquetFileWriter;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.example.ExampleParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.MessageTypeParser;

public class ParquetUtil {

    public static void parquetWriter(Trace trace) throws IOException{
        MessageType schema = MessageTypeParser.parseMessageType(
                "message Pair {\n" +
                            " required binary traceId (UTF8);\n" +
                            " required binary id (UTF8);\n" +
                            " required binary name (UTF8);\n" +
                            " required INT64 timestamp;\n" +
                            " required INT64 duration;\n" +
                            " repeated group annotation {\n"+
                                " required INT64 timestamp;\n" +
                                " required binary value (UTF8);\n" +
                            "}\n"+
                            " repeated group binaryAnnotation {\n"+
                                " required binary key (UTF8);\n" +
                                " required binary value (UTF8);\n" +
                            "}\n"+
                        "}");

        GroupFactory factory = new SimpleGroupFactory(schema);

        Path path = new Path("/parquet/traces.parquet");

        Configuration configuration = new Configuration();

        ExampleParquetWriter.Builder builder = ExampleParquetWriter

                .builder(path).withWriteMode(ParquetFileWriter.Mode.OVERWRITE)

                .withWriterVersion(ParquetProperties.WriterVersion.PARQUET_1_0)

                .withCompressionCodec(CompressionCodecName.SNAPPY)

                .withConf(configuration).withType(schema);
        ParquetWriter writer = builder.build();


        Group group = factory.newGroup()
                .append("traceId",trace.getTraceId())
                .append("id",trace.getId())
                .append("name",trace.getName())
                .append("timestamp",trace.getTimestamp())
                .append("duration",trace.getDuration());
        for(int i = 0;i < trace.getAnnotations().size();i++){
            Group tmpG = group.addGroup("annotation");
            tmpG.append("timestamp", trace.getAnnotations().get(i).getTimestamp());
            tmpG.append("value", trace.getAnnotations().get(i).getValue());
        }
        for(int i = 0;i < trace.getBinaryAnnotations().size();i++){
            Group tmpG = group.addGroup("binaryAnnotation");
            tmpG.append("key", trace.getBinaryAnnotations().get(i).getKey());
            tmpG.append("value", trace.getBinaryAnnotations().get(i).getValue());
        }

        writer.write(group);

        System.out.println("write end");
        writer.close();
    }

}
