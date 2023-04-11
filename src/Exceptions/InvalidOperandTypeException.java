package Exceptions;

public class InvalidOperandTypeException extends Exception
{
    public InvalidOperandTypeException()
    {
        super("Instrukcija ne podrzava dati tip operanda");
    }

    public InvalidOperandTypeException(String message)
    {
        super(message);
    }


}
