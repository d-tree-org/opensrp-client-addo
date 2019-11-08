package org.smartregister.addo.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;

import java.text.MessageFormat;

public class QueryBuilder {

    public static String getQuery(String[] joinTables, String mainCondition, String tableName,
                                  String filters, RecyclerViewPaginatedAdapter clientAdapter,
                                  String Sortqueries) {
        StringBuilder sb = new StringBuilder();

        if (ArrayUtils.isEmpty(joinTables)) {
            sb.append(MessageFormat.format("SELECT {0} FROM {1} ", CommonFtsObject.idColumn,
                    CommonFtsObject.searchTableName(tableName)));

            if (StringUtils.isNotBlank(mainCondition)) {
                sb.append(MessageFormat.format("WHERE {0} ", mainCondition));
            }

            if (StringUtils.isNotBlank(filters)) {
                sb.append(StringUtils.isNotBlank(mainCondition) ? "AND " : "WHERE ");
                sb.append(MessageFormat.format("PHRASE MATCH ''{}*'' ", filters.trim()));
            }

            if (StringUtils.isNotBlank(Sortqueries)) {
                sb.append(MessageFormat.format("ORDER BY {0} ", Sortqueries));
            }

            sb.append(MessageFormat.format("LIMIT {0}, {1} ", clientAdapter.getCurrentoffset(),
                    clientAdapter.getCurrentlimit()));
        } else  {

            StringBuilder sbJoin = new StringBuilder();
            sbJoin.append(MessageFormat.format("SELECT {0} FROM {1} ", CommonFtsObject.idColumn,
                    CommonFtsObject.searchTableName(tableName)));

            if (StringUtils.isNotBlank(mainCondition)) {
                sbJoin.append(MessageFormat.format("WHERE {0}", mainCondition));
            }

            if (StringUtils.isNotBlank(filters)) {
                sbJoin.append(StringUtils.isNotBlank(mainCondition) ? "AND " : "WHERE ");
                sbJoin.append(MessageFormat.format("PHRASE MATCH ''{0}*''", filters.trim()));
            }

            for (String tablename : joinTables ) {

                if (StringUtils.isNotBlank(sbJoin.toString().trim())) {
                    sbJoin.append("UNION");
                }

                sbJoin.append(MessageFormat.format("SELECT {0} FROM {1} ", CommonFtsObject.idColumn,
                        CommonFtsObject.searchTableName(tablename)));

                if (StringUtils.isNotBlank(mainCondition)) {
                    sbJoin.append(MessageFormat.format("WHERE {0} ", mainCondition));
                }
                if (StringUtils.isNotBlank(filters)) {
                    sbJoin.append(StringUtils.isNotBlank(mainCondition) ? "AND " : "WHERE ");
                    sbJoin.append(MessageFormat.format("PHRASE MATCH ''{0}*''", filters.trim()));
                }
            }

            sb.append(MessageFormat.format("SELECT {0} FROM {1} WHERE {2} IN",
                    CommonFtsObject.idColumn,
                    CommonFtsObject.searchTableName(tableName),
                    CommonFtsObject.idColumn));

            if (StringUtils.isNotBlank(sbJoin.toString())) {
                sb.append(MessageFormat.format("( {0} ) ", sbJoin.toString()));
            }

            if (StringUtils.isNotBlank(mainCondition)) {
                sb.append(MessageFormat.format(" AND {0} ", mainCondition));
            }

            if (StringUtils.isNotBlank(Sortqueries)) {
                sb.append(MessageFormat.format("ORDER BY {1} ", sbJoin.toString(), Sortqueries));
            }

            sb.append(MessageFormat.format("LIMIT {0}, {1} ",
                    clientAdapter.getCurrentoffset(),
                    clientAdapter.getCurrentlimit()
                    ));
        }

        return sb.toString();
    }
}
