package org.smartregister.addo.repository;

import org.smartregister.CoreLibrary;

public class MonthlyReportRepository {

    private String anmUser;

    public  MonthlyReportRepository(){
        anmUser = CoreLibrary.getInstance().context().allSharedPreferences().fetchRegisteredANM();
    }

    public String getAdolescentVisits(){
        String query = "select count(*) from ( " +
                "select distinct(base_entity_id), date(datetime(visit_date/1000, 'unixepoch')) as date_visited, visit_json, visit_type " +
                "from visits " +
                "where datetime(visit_date/1000, 'unixepoch') > date('now', 'start of month') " +
                "and visit_type in ('Adolescent ADDO Visit' ) " +
                "and visit_json like \"%"+anmUser+"%\" "+
                "group by base_entity_id, date_visited" +
                ")";
        return query;
    }

    public String getChildVisits(){
        String query = "select count(*) from ( " +
                "select distinct(base_entity_id), date(datetime(visit_date/1000, 'unixepoch')) as date_visited, visit_json, visit_type " +
                "from visits " +
                "where datetime(visit_date/1000, 'unixepoch') > date('now', 'start of month') " +
                "and visit_type in ('Child ADDO Visit' ) " +
                "and visit_json like \"%"+anmUser+"%\" "+
                "group by base_entity_id, date_visited" +
                ")";
        return query;
    }


}
