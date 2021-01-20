package com.optimus.eds.model;

public class WorkStatus {

    private Long syncDate;
    private Integer dayStarted;

    public WorkStatus( Integer dayStarted) {
        this.dayStarted = dayStarted;
    }

    public Long getSyncDate() {
        return syncDate==null?0L:syncDate;
    }

    public void setSyncDate(Long syncDate) {
        this.syncDate = syncDate;
    }
    public Integer getDayStarted() {
        return dayStarted==null?0:dayStarted;
    }

    public boolean isDayStarted(){
        return getDayStarted()==1;
    }

    public void setDayStarted(Integer dayStarted) {
        this.dayStarted = dayStarted;
    }

}
