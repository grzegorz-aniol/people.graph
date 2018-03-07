package people.output.orientdb;

import com.orientechnologies.orient.core.db.OrientDB;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrientDbConnection {

    private OrientDB db;
}
