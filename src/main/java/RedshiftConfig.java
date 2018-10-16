public class RedshiftConfig {
    private String host;
    private int port = 5439;
    private String user;
    private String password;
    private String db;

    public RedshiftConfig(String host, int port, String db) {
        this(host, port, System.getenv("REDSHIFT_USERNAME"), System.getenv("REDSHIFT_PASSWORD"), db);
    }

    public RedshiftConfig(String host, int port, String user, String password, String db) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.db = db;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }
}
