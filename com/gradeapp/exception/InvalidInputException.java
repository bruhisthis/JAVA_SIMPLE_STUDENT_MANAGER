package com.gradeapp.exception;


public class InvalidInputException extends BaseExceptionApp {
    private static final String ID_ERROR_MESSAGE = " non valid id: ";
    private static final String GMAIL_ERROR_MESSAGE =" non valid gmail: ";
    private static final String STRING_ERROR_MESSAGE = " non valid string or null ";
    private static final String NULL_OBJECT_MESSAGE = " invlid input : null object";
    private static final String DOUBLE_ERROR_MESSAGE = " non valid score : null or out of bound ";

    private final String string;
    private final Integer integer;
    private final INPUTYPE type;
    private final Double doubletype;

    public enum INPUTYPE{
        STRING,EMAIL,DOUBLE
    }
    public InvalidInputException(){
        super(NULL_OBJECT_MESSAGE);
        string =null;
        integer = null;
        type = null;
        doubletype = null;
    } 

    public InvalidInputException(String message,String data,Integer integer,Double doublevalue,INPUTYPE notRegularString){
        super(messageBuilder(message, data,integer,doublevalue,notRegularString));
        this.string = data;
        this.integer = integer;
        this.type = notRegularString;
        this.doubletype = doublevalue;
    }

    private static String messageBuilder(String m,String d,Integer integer,Double valueDouble,INPUTYPE type){
        if (d == null && (integer == null || integer <= 0)&&(valueDouble == null || valueDouble > 100|| valueDouble < 0)) {
            return NULL_OBJECT_MESSAGE;
        }

        String idmsg = (integer == null || integer <= 0)?"missing id or invalid":"id:"+integer;
        switch (type) {
            case DOUBLE:
                if (valueDouble == null || valueDouble > 100|| valueDouble < 0) {
                    return m +idmsg+DOUBLE_ERROR_MESSAGE;
                }
            case EMAIL:  
                if (!d.contains("@gmail.com")){
                    return m+idmsg + GMAIL_ERROR_MESSAGE + d;
                }    
                return m+ID_ERROR_MESSAGE+idmsg ;
            case STRING:
            default:
                if (d == null || d.isBlank()){
                    return m+idmsg+STRING_ERROR_MESSAGE + d;
                }
                return m+ID_ERROR_MESSAGE+idmsg;
        }
        /* if (bool) {
            if (integer == null || integer <=0) {
                return ID_ERROR_MESSAGE+integer+","+GMAIL_ERROR_MESSAGE+d;
            }
            if (d.contains("@gmail.com")) {
                return ID_ERROR_MESSAGE+integer;
            }
            return GMAIL_ERROR_MESSAGE+d;
        }else {
            if (integer == null || integer <=0) {
                return ID_ERROR_MESSAGE+integer+","+STRING_ERROR_MESSAGE+d;
            }
            if (d != null) {
                return ID_ERROR_MESSAGE+integer;
            }
            return STRING_ERROR_MESSAGE+d;
        }*/
        
          
    }
    
    public Double getDoubletype() {
        return doubletype;
    }
    public String getString() {
        return string;
    }
    public Integer getInteger() {
        return integer;
    }
    public INPUTYPE getNotRegularString() {
        return type;
    }
}
