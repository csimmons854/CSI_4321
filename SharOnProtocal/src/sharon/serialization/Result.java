/************************************************
 *
 * Author: Chris Simmons, Mitchell Shannon
 * Assignment: Program0Test
 * Class: CSI 4321 Data Communications
 *
 ************************************************/

package sharon.serialization;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Represents a SharOn search result and provides serialization/deserialization
 */
public class Result {

    private long fileID; //unique Id for file
    private long fileSize; //size of file
    private String fileName; //name of the file

    /**
     * Result constructor that sets all fields by reading the MessageInput
     *
     * @param in is the MessageInput that is read to set fields
     * @throws BadAttributeValueException
     *     if in is empty, does not follow format, or is too large
     */
    public Result(MessageInput in)
            throws BadAttributeValueException, IOException {

        this.fileID = in.getInt();
        if(this.fileID  < 0)
        {
            this.fileID = this.fileID & 0x00000000FFFFFFFFL;
        }

        this.fileSize = in.getInt();
        if(this.fileSize  < 0)
        {
            this.fileSize = this.fileSize & 0x00000000FFFFFFFFL;
        }

        this.fileName = in.getString();
        if (!this.fileName.matches("^[a-zA-Z0-9._-]+$"))
        {
            throw new BadAttributeValueException("File Name uses invalid characters", this.fileName);
        }
    }


    /**
     * Result constructor that sets all fields to respective parameters
     *
     * @param fileID is the long that is the new fileId
     * @param fileSize is the long that is the new fileSize
     * @param fileName is the String that is the new fileName
     *
     * @throws BadAttributeValueException
     *     if any long parameter is negative, or String parameter is null
     */
    public Result(long fileID, long fileSize, String fileName)
            throws BadAttributeValueException
    {
        if(fileID < 0)
        {
            throw new BadAttributeValueException(
                    "Invalid fileID, fileID < 0",  "" + fileID);
        }

        if (fileID > 0xFFFFFFFFL)
        {
            throw new BadAttributeValueException(
                    "Invalid fileID, fileID > largest unsigned int",
                    "" + fileID);
        }

        if (fileSize < 0)
        {
            throw new BadAttributeValueException(
                    "Invalid fileSize, fileSize < 0",  "" + fileSize);
        }

        if (fileName == null)
        {
            throw new BadAttributeValueException("File Name is null", "null");
        }

        if (!fileName.matches("^[a-zA-Z0-9._-]+$"))
        {
            throw new BadAttributeValueException("File Name uses invalid characters", fileName);
        }

        this.fileID = fileID;
        this.fileSize = fileSize;
        this.fileName = fileName;
    }

    /**
     * Serialize Result to given output stream
     *
     * @param out is the output stream to serialize to
     *
     * @throws java.io.IOException
     *     if out is unable to be serialized
     */
    public void encode(MessageOutput out)
            throws  java.io.IOException
    {
        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        temp.write(ByteBuffer.allocate(4).putInt((int)fileID).array());
        temp.write(ByteBuffer.allocate(4).putInt((int)fileSize).array());
        temp.write(fileName.getBytes());
        temp.close();
        out.setMsgOut(temp);
    }

    /**
     * Returns human-readable Result object
     *
     * @return String of human-readable Result object
     */
    @Override
    public java.lang.String toString()
    {
        return fileID + ", " + fileSize + ", " + fileName;
    }

    /**
     * Get fileId
     *
     * @return long of fileId
     */
    public long getFileID()
    {
        return fileID;
    }

    /**
     * set fileId
     *
     * @param fileID is the long that is the new fileId
     *
     * @throws BadAttributeValueException
     *     if the long is negative
     */
    public void setFileID(long fileID)
            throws BadAttributeValueException
    {
        if(fileID < 0)
        {
            throw new BadAttributeValueException(
                    "Invalid fileID, fileID < 0",  "" + fileID);
        }

        if (fileID > 0xFFFFFFFFL)
        {
            throw new BadAttributeValueException(
                    "Invalid fileID, fileID > largest unsigned int",
                    "" + fileID);
        }
        this.fileID = fileID;
    }

    /**
     * get fileSize
     *
     * @return long of fileSize
     */
    public long getFileSize()
    {
        return fileSize;
    }

    /**
     * set fileSize
     *
     * @param fileSize is the long that is the new fileSize
     *
     * @throws BadAttributeValueException
     *     if the long is less than zero
     */
    public void setFileSize(long fileSize)
            throws BadAttributeValueException
    {
        if (fileSize < 0)
        {
            throw new BadAttributeValueException(
                    "Invalid fileSize, fileSize < 0",  "" + fileSize);
        }
        this.fileSize = fileSize;
    }

    /**
     * get fileName
     *
     * @return String of fileName
     */
    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(java.lang.String fileName)
            throws BadAttributeValueException
    {
        if (fileName == null)
        {
            throw new BadAttributeValueException("File Name is null", "null");
        }

        if (!fileName.matches("^[a-zA-Z0-9._-]+$"))
        {
            throw new BadAttributeValueException("File Name uses invalid characters", fileName);
        }

        this.fileName = fileName;
    }

    /**
     * @param o
     * @return True or False based on the equivalency of the objects
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Result)) return false;

        Result result = (Result) o;

        if (fileID != result.fileID) return false;
        if (fileSize != result.fileSize) return false;
        return fileName != null ? fileName.equals(result.fileName) : result.fileName == null;
    }

    /**
     * @return the hashCode
     */
    @Override
    public int hashCode() {
        int result = (int) (fileID ^ (fileID >>> 32));
        result = 31 * result + (int) (fileSize ^ (fileSize >>> 32));
        result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
        return result;
    }
}
