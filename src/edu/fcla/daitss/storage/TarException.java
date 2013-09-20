package edu.fcla.daitss.storage;

@SuppressWarnings("serial")
public class TarException extends Exception {

    public TarException(String string) {
        super(string);
    }

    public TarException(Exception e) {
        super(e);
    }

}
