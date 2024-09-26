package smartsuite.deploy;

public enum TargetDB {
    ORACLE("_databaseId == 'oracle'", "oracle"),
    MYSQL("_databaseId == 'mysql'", "mysql"),
    POSTGRESQL("_databaseId == 'postgresql'", "postgresql"),
    MSSQL("_databaseId == 'mssql'", "mssql"),
    TIBERO("_databaseId == 'oracle'", "tibero");

    private final String databaseType;
    private final String name;

    TargetDB(String databaseType, String name) {
        this.databaseType = databaseType;
        this.name = name;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public String getName() {
        return name;
    }
}
