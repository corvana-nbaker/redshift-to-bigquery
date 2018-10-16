import com.amazonaws.services.s3.AmazonS3;
import com.opencsv.CSVWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedshiftUtils {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private static final String DRIVER_CLASS_NAME = "com.amazon.redshift.jdbc42.Driver";
    private RedshiftConfig config;
    private S3Config s3Config;
    private String iamRole = "arn:aws:iam::707260601894:role/redshift-s3";

    public RedshiftUtils(RedshiftConfig config, S3Config s3Config) {
        this.config = config;
        this.s3Config = s3Config;
    }

    DataSource getDatasource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(getJdbcUrl());
        ds.setUsername(config.getUser());
        ds.setPassword(config.getPassword());
        ds.setDriverClassName(DRIVER_CLASS_NAME);
        ds.setConnectionProperties("ssl=true;sslMode=verify-full;");
        return ds;
    }

    String getJdbcUrl() {
        return "jdbc:redshift://" + config.getHost() + ":" + config.getPort() + "/" + config.getDb();
    }

    List<String> getTableNames(String schema, String prefix) {
        String sql = "select distinct(tablename) from pg_table_def where schemaname = ? and tablename like ?";
        DataSource ds = getDatasource();
        List<String> tables = new ArrayList<>();

        try (Connection connection = ds.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setString(1, schema);
            stmt.setString(2, prefix + "%");
            ResultSet resultSet = stmt.executeQuery();

            while(resultSet.next()) {
                tables.add(resultSet.getString(1));
            }
            resultSet.close();
        } catch (SQLException e) {
            log.error("Error getting table names for prefix " + prefix, e);
        }
        return tables;
    }

    void unloadTableDefinition(String tableName, String schema) {
        String sql = "select \"column\", \"type\" from pg_table_def where schemaname = ? AND tablename = ?";

        DataSource ds = getDatasource();
        log.info("UNLOADING table metadata " + tableName);
        try (Connection connection = ds.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setString(1, schema);
            stmt.setString(2, tableName);

            ResultSet resultSet = stmt.executeQuery();

            StringWriter writer = new StringWriter();
            CSVWriter csv = new CSVWriter(writer, ',');
            csv.writeAll(resultSet, true);
            csv.close();
            resultSet.close();
            AmazonS3 s3 = Utils.getS3(s3Config);
            s3.putObject(s3Config.getBucket(), s3Config.getPrefix() + "/" + tableName + "/metadata.csv", writer.toString());
        } catch (SQLException e) {
            log.error("Error unloading table metadata " + tableName, e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void unloadTableToS3(String tableName) {
        String bucketAndPrefix = s3Config.getBucket() + "/" + s3Config.getPrefix();
        String sql = "UNLOAD ('SELECT * FROM \"" + tableName + "\"') " +
                "TO 's3://" + bucketAndPrefix + "/" + tableName + "/data/' " +
                "IAM_ROLE '" + iamRole + "' ALLOWOVERWRITE ADDQUOTES";

        DataSource ds = getDatasource();
        log.info("UNLOADING table {} to {}", tableName, bucketAndPrefix);
        try (Connection connection = ds.getConnection();
             Statement stmt = connection.createStatement()
        ) {
            stmt.execute(sql);
        } catch (SQLException e) {
            log.error("Error unloading data for table " + tableName, e);
        }
    }
}
