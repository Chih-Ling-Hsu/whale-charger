package com.netdb.nthu.whalecharger.model;

/**
 * Created by Huang on 17/01/2015.
 */
public class HistoryItem {
    private long id;
    private String dateOrigin;
    private String dateEnd;
    private Integer goal;
    private String result;
    private Integer state;
    private Integer unit;

    public HistoryItem(){
        this.id=-1;
        this.dateOrigin="";
        this.dateEnd="";
        this.goal=0;
        this.result="";
        this.state=0;
        this.unit=0;
    }

    public HistoryItem(long id,String dateOrigin,String dateEnd,Integer goal,String result,Integer state,Integer unit){
        this.id=id;
        this.dateOrigin=dateOrigin;
        this.dateEnd=dateEnd;
        this.goal=goal;
        this.result=result;
        this.state=state;
        this.unit=unit;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDateOrigin() {
        return dateOrigin;
    }

    public void setDateOrigin(String dateOrigin) {
        this.dateOrigin = dateOrigin;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public Integer getGoal() {
        return goal;
    }

    public void setGoal(Integer goal) {
        this.goal = goal;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getUnit() {
        return unit;
    }

    public void setUnit(Integer unit) {
        this.unit = unit;
    }
}
