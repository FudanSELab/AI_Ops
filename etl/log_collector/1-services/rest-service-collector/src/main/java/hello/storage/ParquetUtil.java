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
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.example.ExampleParquetWriter;
import org.apache.parquet.hadoop.example.GroupReadSupport;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.MessageTypeParser;

public class ParquetUtil {

    static void parquetReader(String inPath) throws IOException{
        GroupReadSupport readSupport = new GroupReadSupport();
        ParquetReader.Builder<Group> reader= ParquetReader.builder(readSupport, new Path(inPath));
        ParquetReader<Group> build = reader.build();
        Group line=null;
        while((line=build.read())!=null){
//            Group time = line.getGroup("traceId", 0);
            System.out.println(line.getString("id", 0) + "\t" +
                    line.getString("name", 0) + "\t" +
                    line.getLong("timestamp", 0) + "\t");

        }
        System.out.println("读取结束");
    }

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
                                " required binary endpoint_serviceName (UTF8);\n" +
                                " required binary endpoint_ipv4 (UTF8);\n" +
                                " required INT32 endpoint_port;\n" +
                            "}\n"+
                            " repeated group binaryAnnotation {\n"+
                                " required binary key (UTF8);\n" +
                                " required binary value (UTF8);\n" +
                                " required binary endpoint_serviceName (UTF8);\n" +
                                " required binary endpoint_ipv4 (UTF8);\n" +
                                " required INT32 endpoint_port;\n" +
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


        Group span = factory.newGroup()
                .append("traceId",trace.getTraceId())
                .append("id",trace.getId())
                .append("name",trace.getName())
                .append("timestamp",trace.getTimestamp())
                .append("duration",trace.getDuration());
        for(int i = 0;i < trace.getAnnotations().length;i++){
            Group annotation = span.addGroup("annotation");
            annotation.append("timestamp", trace.getAnnotations()[i].getTimestamp());
            annotation.append("value", trace.getAnnotations()[i].getValue());
            annotation.append("endpoint_serviceName", trace.getAnnotations()[i].getEndpoint().getServiceName());
            annotation.append("endpoint_ipv4", trace.getAnnotations()[i].getEndpoint().getIpv4());
            annotation.append("endpoint_port", trace.getAnnotations()[i].getEndpoint().getPort());
        }
        for(int i = 0;i < trace.getBinaryAnnotations().length;i++){

            Group binaryAnnotation = span.addGroup("binaryAnnotation");
            binaryAnnotation.append("key", trace.getBinaryAnnotations()[i].getKey());
            binaryAnnotation.append("value", trace.getBinaryAnnotations()[i].getValue());
            binaryAnnotation.append("endpoint_serviceName", trace.getBinaryAnnotations()[i].getEndpoint().getServiceName());
            binaryAnnotation.append("endpoint_ipv4", trace.getBinaryAnnotations()[i].getEndpoint().getIpv4());
            binaryAnnotation.append("endpoint_port", trace.getBinaryAnnotations()[i].getEndpoint().getPort());
        }

        writer.write(span);

        System.out.println("write end");
        writer.close();
    }

}
