package people.output.orientdb;

import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;

public class OrientDbConnectionProducer {

    public static OrientDbConnection createPLocalConnection(final String dbFilePath) {
        return OrientDbConnection.builder()
                .db(new OrientDB("plocal:" + dbFilePath.replace("\\","/"), OrientDBConfig.defaultConfig()))
                .build();
    }

    public static OrientDbConnection createRemoteConnection(final String host, final String dbName, final String user, final String password) {
        OrientDB orientDB = new OrientDB("remote:" + host +"/" + dbName, user, password, OrientDBConfig.defaultConfig());
        return OrientDbConnection.builder().db(orientDB).build();
    }
}
