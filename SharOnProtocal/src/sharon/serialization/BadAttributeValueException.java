/************************************************
 *
 * Author: Chris Simmons, Mitchell Shannon
 * Assignment: Program0Test
 * Class: CSI 4321 Data Communications
 *
 ************************************************/

package sharon.serialization;

/**
 * thrown to indicate bad value
 */
public class BadAttributeValueException extends java.lang.Exception
{
    private String attributeName;
    /**
     * Constructs a BadAttributeException
     *
     * @param message is the String error message
     * @param attributeName is the String name of attribute
     */
    public BadAttributeValueException (String message, String attributeName)
    {
        super(message);
        if( message == null || attributeName == null)
        {
            throw new java.lang.NullPointerException();
        }
        this.attributeName = attributeName;
    }

    /**
     * Constructs a BadAttributeException
     *
     * @param message is the String error message
     * @param attributeName is the String name of attribute
     * @param cause is the throwable exception cause
     */
    public BadAttributeValueException (String message,
                                String attributeName,
                                Throwable cause)
    {
        super(message,cause);
        if( message == null || attributeName == null)
        {
            throw new java.lang.NullPointerException();
        }
        this.attributeName = attributeName;
    }

    /**
     * get attribute name
     *
     * @return String of attribute name
     */
    public String getAttributeName()
    {
        return attributeName;
    }

}
