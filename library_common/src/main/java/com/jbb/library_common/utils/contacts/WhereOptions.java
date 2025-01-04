package com.jbb.library_common.utils.contacts;

class WhereOptions {
    private String where;
    private String[] whereArgs;
    public void setWhere(String where) {
        this.where = where;
    }
    public String getWhere() {
        return where;
    }
    public void setWhereArgs(String[] whereArgs) {
        this.whereArgs = whereArgs;
    }
    public String[] getWhereArgs() {
        return whereArgs;
    }
}